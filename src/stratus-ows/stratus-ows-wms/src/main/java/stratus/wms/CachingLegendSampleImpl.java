/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms;

import ar.com.hjg.pngj.FilterType;
import ar.com.hjg.pngj.PngReader;
import it.geosolutions.imageio.plugins.png.PNGWriter;
import org.apache.commons.lang.ArrayUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogException;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.event.*;
import org.geoserver.config.impl.GeoServerLifecycleHandler;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wms.GetLegendGraphicOutputFormat;
import org.geoserver.wms.GetLegendGraphicRequest;
import org.geoserver.wms.capabilities.LegendSample;
import org.geoserver.wms.capabilities.LegendSampleImpl;
import org.geoserver.wms.legendgraphic.BufferedImageLegendGraphic;
import org.geoserver.wms.legendgraphic.PNGLegendOutputFormat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Modified implementation of {@link LegendSampleImpl}, that caches dimensions so as to avoid excess Redis queries
 */
public class CachingLegendSampleImpl implements CatalogListener, LegendSample,
        GeoServerLifecycleHandler {

    private static final Logger LOGGER = org.geotools.util.logging.Logging
            .getLogger(CachingLegendSampleImpl.class.getPackage().getName());

    private static final String DEFAULT_SAMPLE_FORMAT = "png";

    private Catalog catalog;

    private GeoServerResourceLoader loader;

    private Set<String> invalidated = new HashSet<>();

    private Map<Resource, Dimension> cache;

    Resource baseDir;

    public CachingLegendSampleImpl(Catalog catalog, GeoServerResourceLoader loader) {
        super();
        this.catalog = catalog;
        this.loader = loader;
        this.baseDir = loader.get(Paths.BASE);
        this.clean();
        this.catalog.addListener(this);
    }

    private boolean checkCache() {
        if (WMSCachingFilter.LEGEND_SIZE.get() != null) {
            cache = WMSCachingFilter.LEGEND_SIZE.get();
            return true;
        }
        return false;
    }

    /**
     * Clean up no more valid samples: SLD updated from latest sample creation.
     */
    private void clean() {
        for (StyleInfo style : catalog.getStyles()) {
            synchronized (style) {
                Resource styleResource = getStyleResource(style);
                Resource sampleFile;
                try {
                    // remove old samples
                    sampleFile = getSampleFile(style);
                    if (isStyleNewerThanSample(styleResource, sampleFile)) {
                        sampleFile.delete();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error cleaning invalid legend sample for " + style.getName(), e);
                }
            }
        }
        invalidated = new HashSet<>();
        cache = new HashMap<>();
    }

    /**
     * Checks if the given SLD resource is newer than the given sample file.
     *
     * @param styleResource
     * @param sampleFile
     *
     */
    private boolean isStyleNewerThanSample(Resource styleResource,
                                           Resource sampleFile) {
        return isSampleExisting(sampleFile)
                && styleResource.getType() == Resource.Type.RESOURCE
                && styleResource.lastmodified() > sampleFile.lastmodified();
    }

    /**
     * Returns  the cache sample for the given file, if
     * it exists, null otherwise.
     *
     * @param style
     *
     * @throws IOException
     */
    private Resource getSampleFile(StyleInfo style) throws IOException {
        String fileName = getSampleFileName(style);
        return getSampleFile(fileName);
    }

    /**
     * Returns  the cache sample with the given name.
     *
     * @param fileName
     *
     */
    private Resource getSampleFile(String fileName) {
        return getSamplesFolder().get(fileName);
    }

    /**
     * Gets a unique fileName for a sample.
     *
     * @param style
     *
     */
    private String getSampleFileName(StyleInfo style) {
        String prefix = "";
        if (style.getWorkspace() != null) {
            prefix = style.getWorkspace().getName() + "_";
        }
        return prefix + style.getName() + "." + DEFAULT_SAMPLE_FORMAT;
    }

    /**
     * Gets an SLD resource for the given style.
     *
     * @param style
     *
     */
    private Resource getStyleResource(StyleInfo style) {
        String[] prefix = new String[0];
        if (style.getWorkspace() != null) {
            prefix = new String[] { "workspaces", style.getWorkspace().getName() };
        }
        String fileName = style.getFilename();
        String[] pathParts = (String[]) ArrayUtils.addAll(prefix, new String[] {
                "styles", fileName });
        String path = Paths.path(pathParts);
        return loader.get(path);
    }

    /**
     * Calculates legendURL size (width x height) for the given style.
     *
     * @param style
     * @return legend dimensions
     * @throws IOException
     */
    public Dimension getLegendURLSize(StyleInfo style) throws Exception {
        synchronized (style) {
            GetLegendGraphicOutputFormat pngOutputFormat = new PNGLegendOutputFormat();

            Resource sampleLegend = getSampleFile(style);
            synchronized(cache) {
                if (isSampleExisting(sampleLegend)
                        && !isStyleSampleInvalid(style)) {
                    // using existing sample if sld has not been updated from
                    // latest sample update
                    return getSizeFromSample(sampleLegend);
                } else {
                    // generates a new sample, and save it on disk (in the dedicated folder) for
                    // later usage
                    return createNewSample(style, pngOutputFormat);
                }
            }
        }
    }

    private boolean isSampleExisting(Resource sampleLegend) {
        if (sampleLegend != null) {
            if (checkCache()) {
                return cache.get(sampleLegend) != null;
            } else {
                return sampleLegend.getType() == Resource.Type.RESOURCE;
            }
        }
        return false;
    }

    /**
     * Creates a new sample file for the given style and stores
     * it on disk.
     * The sample dimensions (width x height) are returned.
     *
     * @param style
     * @param pngOutputFormat
     *
     */
    private Dimension createNewSample(StyleInfo style,
                                      GetLegendGraphicOutputFormat pngOutputFormat) throws Exception {
        GetLegendGraphicRequest legendGraphicRequest = new GetLegendGraphicRequest();
        Resource sampleLegendFolder = getSamplesFolder();

        legendGraphicRequest.setStrict(false);
        legendGraphicRequest.setLayer(null);
        legendGraphicRequest.setStyle(style.getStyle());
        legendGraphicRequest.setFormat(pngOutputFormat.getContentType());
        Object legendGraphic = pngOutputFormat
                .produceLegendGraphic(legendGraphicRequest);
        if (legendGraphic instanceof BufferedImageLegendGraphic) {
            BufferedImage image = ((BufferedImageLegendGraphic) legendGraphic)
                    .getLegend();

            PNGWriter writer = new PNGWriter();
            OutputStream outStream = null;
            try {
                Resource sampleFile = sampleLegendFolder.get(getSampleFileName(style));
                outStream = sampleFile.out();
                writer.writePNG(image, outStream, 0.0f, FilterType.FILTER_NONE);
                removeStyleSampleInvalidation(style);
                Dimension dimension = new Dimension(image.getWidth(), image.getHeight());
                if (checkCache()) {
                    cache.put(sampleFile, dimension);
                }
                return dimension;
            } finally {
                if(outStream != null) {
                    outStream.close();
                }
            }

        }

        return null;
    }

    private Resource getSamplesFolder() {
        return baseDir.get(LegendSampleImpl.LEGEND_SAMPLES_FOLDER);
    }

    /**
     *
     * @param sampleLegendFile
     *
     */
    private Dimension getSizeFromSample(Resource sampleLegendFile) {
        if (checkCache()) {
            return cache.get(sampleLegendFile);
        } else {
            PngReader pngReader = null;
            try {
                // reads size using PNGJ reader, that can read metadata without reading
                // the full image
                pngReader = new PngReader(sampleLegendFile.file());
                return new Dimension(pngReader.imgInfo.cols, pngReader.imgInfo.rows);
            } finally {
                if (pngReader != null) {
                    pngReader.close();
                }
            }
        }
    }

    @Override
    public void handleAddEvent(CatalogAddEvent event) throws CatalogException {

    }

    @Override
    public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
        if (event.getSource() instanceof StyleInfo) {
            // invalidate removed styles (is this needed?)
            invalidateStyleSample((StyleInfo) event.getSource());
        }
    }

    @Override
    public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {

    }

    @Override
    public void handlePostModifyEvent(CatalogPostModifyEvent event)
            throws CatalogException {
        if (event.getSource() instanceof StyleInfo) {
            // invalidate updated styles
            invalidateStyleSample((StyleInfo) event.getSource());
        }
    }

    /**
     * Set the given style sample as invalid.
     *
     * @param style
     */
    private void invalidateStyleSample(StyleInfo style) {
        synchronized (style) {
            invalidated.add(getStyleName(style));
        }
    }

    /**
     * Remove the given style sample from invalid ones.
     *
     * @param style
     */
    private void removeStyleSampleInvalidation(StyleInfo style) {
        invalidated.remove(getStyleName(style));
    }

    /**
     * Checks if the given style sample is marked as invalid.
     *
     * @param style
     *
     */
    private boolean isStyleSampleInvalid(StyleInfo style) {
        return invalidated.contains(getStyleName(style));
    }

    /**
     * Gets a unique name for a style, considering the workspace definition, in the
     * form worspacename:stylename (or stylename if the style is global).
     *
     * @param styleInfo
     *
     */
    private String getStyleName(StyleInfo styleInfo) {
        return styleInfo.getWorkspace() != null ? (styleInfo.getWorkspace()
                .getName() + ":" + styleInfo.getName()) : styleInfo.getName();
    }

    @Override
    public void reloaded() {
        clean();
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onDispose() {
        catalog.removeListener( this );
    }

    @Override
    public void beforeReload() {

    }

    @Override
    public void onReload() {
        reloaded();
    }

}
