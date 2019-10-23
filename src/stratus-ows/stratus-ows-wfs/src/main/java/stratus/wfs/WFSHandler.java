/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs;

import stratus.ows.OWSCachingException;
import stratus.ows.OWSCachingHandler;
import stratus.redis.index.RedisLayerIndexFacade;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.geoserver.ows.Request;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.ServiceException;
import stratus.wfs.xml.WfsXmlParserHelper;
import stratus.wfs.xml.v1_0_0.Wfs1_0_0XmlParserHelper;
import stratus.wfs.xml.v1_1_0.Wfs1_1_0XmlParserHelper;
import stratus.wfs.xml.v2_0.Wfs2_0XmlParserHelper;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle catalog caching for WFS requests
 */
public class WFSHandler extends OWSCachingHandler {

    @Autowired
    private GeoServer geoServer;
    @Autowired
    private Catalog catalog;
    @Autowired
    private RedisLayerIndexFacade indexFacade;

    private Wfs1_0_0XmlParserHelper wfs1_0_0XmlParserHelper;
    private Wfs1_1_0XmlParserHelper wfs1_1_0XmlParserHelper;
    private Wfs2_0XmlParserHelper wfs2_0XmlParserHelper;

    public WFSHandler() {}

    public WFSHandler(GeoServer geoServer, RedisLayerIndexFacade indexFacade) {
        this.geoServer = geoServer;
        this.catalog = geoServer.getCatalog();
        this.indexFacade = indexFacade;
    }

    @PostConstruct
    private void initParserHelpers() {
        wfs1_0_0XmlParserHelper = new Wfs1_0_0XmlParserHelper(geoServer,
                GeoServerExtensions.bean(org.geoserver.wfs.xml.v1_0_0.WFSConfiguration.class));

        wfs1_1_0XmlParserHelper = new Wfs1_1_0XmlParserHelper(geoServer,
                GeoServerExtensions.bean(org.geoserver.wfs.xml.v1_1_0.WFSConfiguration.class));

        wfs2_0XmlParserHelper = new Wfs2_0XmlParserHelper(geoServer);
    }

    @Override
    public void handle(String serviceName, String versionName, String requestName, String virtualWsName, String virtualLayerName, Request request) throws OWSCachingException {
        try {
            if ("WFS".equalsIgnoreCase(serviceName)) {
                if ("GetCapabilities".equalsIgnoreCase(requestName)) {
                    //all FeatureType layers, filtered by virtual service
                    //TODO: limit by ResourceInfo class?
                    indexFacade.preloadCacheByVirtualService(virtualWsName, virtualLayerName, catalog);

                } else if ("GetFeature".equalsIgnoreCase(requestName) ||
                        "GetFeatureWithLock".equalsIgnoreCase(requestName) ||
                        "GetPropertyValue".equalsIgnoreCase(requestName) ||
                        "DescribeFeatureType".equalsIgnoreCase(requestName)) {

                    //typeNames key (typeName for WFS 1.1.0 and earlier) / wfs:Query typeName(s) parameter
                    List<String> layers = parseGetTypeNamesXML(versionName, request);
                    if (layers.size() == 0) {
                        if ("2.0.0".equalsIgnoreCase(versionName)) {
                            layers = getValues(request.getKvp(), "typeNames");
                        } else {
                            layers = getValues(request.getKvp(), "typeName");
                        }
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

                } else if ("Transaction".equalsIgnoreCase(requestName)) {
                    //DescribeFeatureType schema location
                    List<String> layers = parseGetTypeNamesXML(versionName, request);
                    indexFacade.preloadCacheByNames(layers, catalog);
                }
            }
        } catch (ServiceException | IOException e) {
            throw new OWSCachingException(e);
        }
    }

    private List<String> parseGetTypeNamesXML(String versionName, Request request) throws ServiceException, IOException {
        List<String> layers = new ArrayList<>();
        WfsXmlParserHelper parserHelper;

        if ("1.0.0".equalsIgnoreCase(versionName)) {
            parserHelper = wfs1_0_0XmlParserHelper;
        } else if ("1.1.0".equalsIgnoreCase(versionName)) {
            parserHelper = wfs1_1_0XmlParserHelper;
        } else if ("2.0.0".equalsIgnoreCase(versionName)) {
            parserHelper = wfs2_0XmlParserHelper;
        } else {
            return layers;
        }

        if (request.getInput() != null && request.getHttpRequest() != null /*&& request.getHttpRequest().getContentLength() >= 0*/) {
            BufferedReader reader = splitRequestReader(request);

            Object parsed = parserHelper.parse(null, reader, request.getKvp());

            List typeNames = new ArrayList<>();
            //WFS 2.0 GetFeature
            if (parsed instanceof net.opengis.wfs20.GetFeatureType) {
                net.opengis.wfs20.GetFeatureType parsedRequest = (net.opengis.wfs20.GetFeatureType) parsed;

                for (Object q : parsedRequest.getAbstractQueryExpression()) {
                    if (q instanceof net.opengis.wfs20.QueryType) {
                        net.opengis.wfs20.QueryType query = (net.opengis.wfs20.QueryType) q;
                        if (query.getTypeNames() != null) {
                            typeNames.addAll(query.getTypeNames());
                        }
                    }
                }
            //WFS 1.0 or 1.1 GetFeature
            } else if (parsed instanceof net.opengis.wfs.GetFeatureType) {
                net.opengis.wfs.GetFeatureType parsedRequest = (net.opengis.wfs.GetFeatureType) parsed;

                for (Object q : parsedRequest.getQuery()) {
                    if (q instanceof net.opengis.wfs.QueryType) {
                        net.opengis.wfs.QueryType query = (net.opengis.wfs.QueryType) q;
                        if (query.getTypeName() != null) {
                            typeNames.addAll(query.getTypeName());
                        }
                    }
                }
            //WFS 2.0 DescribeFeatureType
            } else if (parsed instanceof net.opengis.wfs20.DescribeFeatureTypeType) {
                net.opengis.wfs20.DescribeFeatureTypeType parsedRequest = (net.opengis.wfs20.DescribeFeatureTypeType) parsed;

                if (parsedRequest.getTypeName() != null) {
                    typeNames.addAll(parsedRequest.getTypeName());
                }
            //WFS 1.0 or 1.1 DescribeFeatureType
            } else if (parsed instanceof net.opengis.wfs.DescribeFeatureTypeType) {
                net.opengis.wfs.DescribeFeatureTypeType parsedRequest = (net.opengis.wfs.DescribeFeatureTypeType) parsed;

                if (parsedRequest.getTypeName() != null) {
                    typeNames.addAll(parsedRequest.getTypeName());
                }
            //WFS 2.0 Transaction
            } else if (parsed instanceof net.opengis.wfs20.TransactionType) {
                net.opengis.wfs20.TransactionType parsedRequest = (net.opengis.wfs20.TransactionType) parsed;
                for (Object entry : parsedRequest.getGroup()) {
                    if (entry instanceof FeatureMap.Entry) {
                        entry = ((FeatureMap.Entry) entry).getValue();
                    }
                    if (entry instanceof net.opengis.wfs20.InsertType) {
                        net.opengis.wfs20.InsertType insert = (net.opengis.wfs20.InsertType) entry;
                        for (Object feature : insert.getAny()) {
                            if (feature instanceof Feature) {
                                FeatureType ft = ((Feature) feature).getType();
                                typeNames.add(ft.getName().getURI().substring(ft.getName().getURI().lastIndexOf('/')+1));
                            }
                        }
                    } else if (entry instanceof net.opengis.wfs20.UpdateType) {
                        net.opengis.wfs20.UpdateType update = (net.opengis.wfs20.UpdateType) entry;
                        typeNames.add(update.getTypeName().getPrefix() + ":" + update.getTypeName().getLocalPart());
                    } else if (entry instanceof net.opengis.wfs20.DeleteType) {
                        net.opengis.wfs20.DeleteType delete = (net.opengis.wfs20.DeleteType) entry;
                        typeNames.add(delete.getTypeName().getPrefix() + ":" + delete.getTypeName().getLocalPart());
                    }
                }

            //WFS 1.0 or 1.1 Transaction
            } else if (parsed instanceof net.opengis.wfs.TransactionType) {
                net.opengis.wfs.TransactionType parsedRequest = (net.opengis.wfs.TransactionType) parsed;
                for (Object entry : parsedRequest.getGroup()) {
                    if (entry instanceof FeatureMap.Entry) {
                        entry = ((FeatureMap.Entry) entry).getValue();
                    }
                    if (entry instanceof net.opengis.wfs.InsertElementType) {
                        net.opengis.wfs.InsertElementType insert = (net.opengis.wfs.InsertElementType) entry;
                        for (Object feature : insert.getFeature()) {
                            if (feature instanceof Feature) {
                                FeatureType ft = ((Feature) feature).getType();
                                typeNames.add(ft.getName().getURI().substring(ft.getName().getURI().lastIndexOf('/')+1));
                            }
                        }
                    } else if (entry instanceof net.opengis.wfs.UpdateElementType) {
                        net.opengis.wfs.UpdateElementType update = (net.opengis.wfs.UpdateElementType) entry;
                        typeNames.add(update.getTypeName().getPrefix() + ":" + update.getTypeName().getLocalPart());
                    } else if (entry instanceof net.opengis.wfs.DeleteElementType) {
                        net.opengis.wfs.DeleteElementType delete = (net.opengis.wfs.DeleteElementType) entry;
                        typeNames.add(delete.getTypeName().getPrefix() + ":" + delete.getTypeName().getLocalPart());
                    }
                }
            }
            for (Object t : typeNames) {
                if (t instanceof String) {
                    layers.add((String) t);
                }
                if (t instanceof QName) {
                    QName qName = (QName) t;
                    layers.add(qName.getPrefix()+":"+qName.getLocalPart());
                }
            }

        }
        return layers;
    }
}
