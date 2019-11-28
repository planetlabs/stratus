/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Catalog;
import org.geoserver.ows.DispatcherCallback;
import org.geoserver.ows.Request;
import org.geoserver.ows.Response;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.platform.*;
import org.geotools.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.index.RedisLayerIndexFacade;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link DispatcherCallback} used by OWS endpoints to preload the {@link CachingCatalogFacade}
 * cache based on the request parameters.
 */
@Slf4j
public class OWSCachingCallback implements DispatcherCallback, ExtensionPriority {

    @Autowired
    Catalog catalog;

    @Autowired
    RedisLayerIndexFacade indexFacade;

    public OWSCachingCallback() { }

    public OWSCachingCallback(Catalog catalog, RedisLayerIndexFacade indexFacade) {
        this.catalog = catalog;
        this.indexFacade = indexFacade;
    }

    @Override
    public Request init(Request request) {

        String serviceName = null;
        String versionName = null;
        String requestName = null;

        /* Get Service + Request */

        //check kvp
        if (request.getKvp() != null) {
            serviceName = normalize(KvpUtils.getSingleValue(request.getKvp(), "service"));
            versionName = normalizeVersion(normalize(KvpUtils.getSingleValue(request.getKvp(), "version")));
            requestName = normalize(KvpUtils.getSingleValue(request.getKvp(), "request"));
        }
        //check the body
        if (request.getInput() != null) {
            try {
                Map xml = readOpPost(request.getInput());
                if (xml.get("service") != null) {
                    serviceName = normalize((String) xml.get("service"));
                }
                if (xml.get("version") != null) {
                    versionName = normalizeVersion(normalize((String) xml.get("version")));
                }
                if (xml.get("request") != null) {
                    requestName = normalize((String) xml.get("request"));
                }
            } catch (Exception e) {
                //Do nothing, let Dispatcher handle the exception
                log.debug("Error when preloading cache", e);
            }
        }
        //check the path / virtual service
        if (serviceName == null) {
            serviceName = request.getPath();
        }

        /* Check for virtual service */

        //TODO: Support virtual workspace/layer
        // /[ws]/ OR /[ws]/layer OR /[lg] (global lg only)

        //Ask for everything we might need, assume some will miss, let catalogdecorators filter the list
        //wsName can also be a global layer group name. TODO: Support global layer group
        String wsName = null;
        String layerName = null;

        if (request.getContext() != null) {
            wsName = request.getContext();

            int slash = wsName.indexOf('/');
            if (slash > -1) {
                layerName = wsName.substring(slash + 1);
                wsName = wsName.substring(0, slash);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Invoking ows cache preloading for: " + serviceName + " " + versionName + " " + requestName);
        }
        /* Handle different OWS Services */

        //For all of the following, filter by virtual service WS / Layer
        //Layer may be a layer group, so plan accordingly.
        //Initial index fetch will grab as much as plausible.
        for (OWSCachingHandler handler : GeoServerExtensions.extensions(OWSCachingHandler.class)) {
            try {
                handler.handle(serviceName, versionName, requestName, wsName, layerName, request);
            } catch (OWSCachingException e) {
                log.warn("Error caching catalog", e);
            }
        }
        return request;
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }

        if ("".equals(value.trim())) {
            return null;
        }

        return value.trim();
    }

    /**
     * Normalize the version, handling cases like forcing "x.y" to "x.y.z".
     */
    public static String normalizeVersion(String version) {
        if (version == null) {
            return null;
        }

        Version v = new Version(version);
        if (v.getMajor() == null) {
            return null;
        }

        if (v.getMinor() == null) {
            return String.format("%d.0.0", v.getMajor());
        }

        if (v.getRevision() == null) {
            return String.format("%d.%d.0", v.getMajor(), v.getMinor());
        }

        //version ok
        return version;
    }

    public static Map readOpPost(BufferedReader input) throws Exception {
        //create stream parser
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        //parse root element
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(input);
        try {
            parser.nextTag();

            Map map = new HashMap();
            map.put("request", parser.getName());
            map.put("namespace", parser.getNamespace());

            for (int i = 0; i < parser.getAttributeCount(); i++) {
                String attName = parser.getAttributeName(i);

                if ("service".equals(attName)) {
                    map.put("service", parser.getAttributeValue(i));
                }

                if ("version".equals(parser.getAttributeName(i))) {
                    map.put("version", parser.getAttributeValue(i));
                }

                if ("outputFormat".equals(attName)) {
                    map.put("outputFormat", parser.getAttributeValue(i));
                }
            }
            return map;

        } finally {
            //close parser + release resources
            parser.setInput(null);
            //reset the input stream
            input.reset();
        }
    }

    @Override
    public Service serviceDispatched(Request request, Service service) throws ServiceException {
        return null;
    }

    @Override
    public Operation operationDispatched(Request request, Operation operation) {
        return null;
    }

    @Override
    public Object operationExecuted(Request request, Operation operation, Object result) {
        return null;
    }

    @Override
    public Response responseDispatched(Request request, Operation operation, Object result, Response response) {
        return null;
    }

    @Override
    public void finished(Request request) {

    }

    @Override
    public int getPriority() {
        //Must come before LocalWorkspaceCallback and anything else that calls the catalog
        return -100;
    }
}
