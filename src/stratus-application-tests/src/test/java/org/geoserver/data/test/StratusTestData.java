/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.data.test;

import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.SLDHandler;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServerDataDirectory;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.Map;

public class StratusTestData extends SystemTestData {

    Catalog catalog;

    public StratusTestData(Catalog catalog) throws IOException {
        super();
        this.catalog = catalog;
    }


    @Override
    public void setUp() throws Exception {

        //workspaces
        addWorkspace(DEFAULT_PREFIX, DEFAULT_URI, catalog);
        addWorkspace(SF_PREFIX, SF_URI, catalog);
        addWorkspace(CITE_PREFIX, CITE_URI, catalog);
        addWorkspace(CDF_PREFIX, CDF_URI, catalog);
        addWorkspace(CGF_PREFIX, CGF_URI, catalog);

        //default style
        addStyle(DEFAULT_VECTOR_STYLE, catalog);
        addStyle(DEFAULT_RASTER_STYLE, catalog);
    }

    @Override
    public void setUpDefault() throws Exception {
        setUpDefaultLayers();
        setUpSecurity();
    }

    /**
     * Sets up the default set of layers, which is all the vector layers whose names are included
     * in the {@link CiteTestData#TYPENAMES} array.
     */
    @Override
    public void setUpDefaultLayers() throws IOException {
        for (QName layerName : TYPENAMES) {
            addVectorLayer(layerName, catalog);
        }
    }

    public void setUpDefaultRasterLayers() throws IOException {
        addWorkspace(WCS_PREFIX, WCS_URI, catalog);
        addDefaultRasterLayer(TASMANIA_DEM, catalog);
        addDefaultRasterLayer(TASMANIA_BM, catalog);
        addDefaultRasterLayer(ROTATED_CAD, catalog);
        addDefaultRasterLayer(WORLD, catalog);
        addDefaultRasterLayer(MULTIBAND,catalog);
    }

    /**
     * Loads a style. Overridden to load into resource store
     * @see SystemTestData#addStyle(String, String, Class, Catalog)
     */
    @Override
    public void addStyle(WorkspaceInfo ws, String name, String filename, Class scope, Catalog catalog,
                         Map<StyleProperty, Object> properties) throws IOException {

        StyleInfo style = catalog.getStyleByName(ws, name);
        if (style == null) {
            style = catalog.getFactory().createStyle();
            style.setName(name);
            style.setWorkspace(ws);
        }

        GeoServerDataDirectory data = new GeoServerDataDirectory(this.data);
        File styles = data.get(style, "").dir();
        String target = new File( filename ).getName();
        File styleFile = new File(styles, target );
        catalog.getResourceLoader().copyFromClassPath(filename, styleFile, scope);

        //Copy the style file to the Resource store
        GeoServerDataDirectory destData = new GeoServerDataDirectory(catalog.getResourceLoader());
        InputStream in = new FileInputStream(styleFile);
        OutputStream out = destData.getStyles(filename).out();
        IOUtils.copy(in, out);
        in.close();
        out.close();

        style.setFilename(target);
        style.setFormat(StyleProperty.FORMAT.get(properties, SLDHandler.FORMAT));
        style.setFormatVersion(StyleProperty.FORMAT_VERSION.get(properties, SLDHandler.VERSION_10));
        style.setLegend(StyleProperty.LEGEND_INFO.get(properties, null));
        if (style.getId() == null) {
            catalog.add(style);
        }
        else {
            catalog.save(style);
        }
    }
}
