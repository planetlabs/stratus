/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms;

import stratus.ows.OWSCachingException;
import stratus.ows.OWSCachingHandler;
import stratus.redis.index.RedisLayerIndexFacade;
import org.geoserver.catalog.Catalog;
import org.geoserver.ows.Request;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.wms.map.GetMapXmlReader;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.xml.styling.SLDParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static stratus.ows.OWSCachingCallback.normalize;

/**
 * Handle catalog caching for WMS requests
 */
public class WMSHandler extends OWSCachingHandler {
    private static final StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();

    @Autowired
    private Catalog catalog;
    @Autowired
    private RedisLayerIndexFacade indexFacade;

    public WMSHandler() {}

    public WMSHandler(Catalog catalog, RedisLayerIndexFacade indexFacade) {
        this.catalog = catalog;
        this.indexFacade = indexFacade;
    }

    @Override
    public void handle(String serviceName, String versionName, String requestName, String virtualWsName, String virtualLayerName, Request request) throws OWSCachingException {
        try {
            if ("WMS".equalsIgnoreCase(serviceName)) {
                if ("GetCapabilities".equalsIgnoreCase(requestName)) {
                    //All layers, filtered by virtual service
                    indexFacade.preloadCacheByVirtualService(virtualWsName, virtualLayerName, catalog);

                } else if ("GetMap".equalsIgnoreCase(requestName) ||
                        "GetFeatureInfo".equalsIgnoreCase(requestName) ||
                        "DescribeLayer".equalsIgnoreCase(requestName)) {
                    //layers key / StyledLayerDescriptor:NamedLayer:Name (see getMapXMLParser / SLDParser)
                    List<String> layers = parseGetMapXML(request);
                    if (layers.size() == 0) {
                        layers = getValues(request.getKvp(), "layers");
                    }
                    if (virtualWsName != null) {
                        List<String> wsLayers = new ArrayList<>();
                        for (String layer : layers) {
                            if (layer.startsWith(virtualWsName + ":")) {
                                wsLayers.add(layer);
                            } else {
                                wsLayers.add(virtualWsName + ":" + layer);
                            }
                        }
                        layers = wsLayers;
                    }
                    indexFacade.preloadCacheByNames(layers, catalog);

                } else if ("GetLegendGraphic".equalsIgnoreCase(requestName)) {
                    //layer key
                    String layerString = normalize(KvpUtils.getSingleValue(request.getKvp(), "layer"));
                    if (layerString != null && !layerString.equals("")) {
                        if (virtualWsName != null && !layerString.startsWith(virtualWsName + ":")) {
                            layerString = virtualWsName + ":" + layerString;
                        }
                        indexFacade.preloadCacheByNames(Collections.singletonList(layerString), catalog);
                    }
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new OWSCachingException(e);
        }
    }

    private List<String> parseGetMapXML(Request request) throws IOException, SAXException, ParserConfigurationException {
        List<String> layers = new ArrayList<>();

        if (request.getInput() != null && request.getHttpRequest() != null) {
            BufferedReader reader = splitRequestReader(request);

            List<GetMapXmlReader> readers = GeoServerExtensions.extensions(GetMapXmlReader.class);
            if (readers.size() > 0) {
                GetMapXmlReader xmlReader = readers.get(0);

                javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                        .newInstance();

                dbf.setExpandEntityReferences(false);
                dbf.setValidating(false);
                dbf.setNamespaceAware(true);

                javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
                EntityResolver entityResolver = xmlReader.getWMS().getCatalog().getResourcePool().getEntityResolver();
                if(entityResolver != null) {
                    db.setEntityResolver(entityResolver);
                }

                InputSource input = new InputSource(reader);
                org.w3c.dom.Document dom = db.parse(input);

                SLDParser sldParser = new SLDParser(styleFactory);

                Node rootNode = dom.getDocumentElement();
                StyledLayerDescriptor sld = sldParser.parseDescriptor(xmlReader.getNode(rootNode,
                        "StyledLayerDescriptor"));

                for (StyledLayer layer : sld.getStyledLayers()) {
                    if (layer.getName() != null && !layer.getName().trim().equals("")) {
                        layers.add(layer.getName());
                    }
                }
            }
        }
        return layers;
    }
}
