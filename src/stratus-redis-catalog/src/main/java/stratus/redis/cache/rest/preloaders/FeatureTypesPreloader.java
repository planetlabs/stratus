/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.springframework.stereotype.Component;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class FeatureTypesPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/featuretypes",
            "/rest/workspaces/{workspace}/datastores/{datastore}/featuretypes"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    /**
     * Preload catalog data for a REST featuretypes/{featuretype} operation.
     */
    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String datastoreName = pathVariables.get(DATASTORE);
        if (workspaceName != null) {
            //Main objects
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            if ("GET".equals(method)) {
                if (datastoreName != null) {
                    RedisValueQuery datastoreQuery = queryEngine.getStoreByName(workspaceQuery, datastoreName, DataStoreInfo.class);
                    queryEngine.getResourcesByStore(datastoreQuery, FeatureTypeInfo.class);
                } else {
                    queryEngine.getResourcesByNamespace(namespaceQuery, FeatureTypeInfo.class);
                }
            } else if ("POST".equals(method) && datastoreName != null) {
                RedisValueQuery datastoreQuery = queryEngine.getStoreByName(workspaceQuery, datastoreName, DataStoreInfo.class);

                /* Not querying featuretype as this requires decoding object body, which needs a bunch of catalog lookups

                //Tests for name collision on featureType
                String featureTypeName = requestObject == null ? "" : requestObject.getName();
                queryEngine.getResourceByStore(datastoreQuery, featureTypeName, CoverageInfo.class);
                queryEngine.getResourceByStore(datastoreQuery, featureTypeName, FeatureTypeInfo.class);
                queryEngine.getResourceByStore(datastoreQuery, featureTypeName, WMSLayerInfo.class);

                queryEngine.getResourceByName(namespaceQuery, featureTypeName, CoverageInfo.class);
                queryEngine.getResourceByName(namespaceQuery, featureTypeName, FeatureTypeInfo.class);
                queryEngine.getResourceByName(namespaceQuery, featureTypeName, WMSLayerInfo.class);

                 //Tests for name collision on layer
                 queryEngine.getLayerByName(namespaceQuery, featureTypeName);
                 */

                //Defaults used in featureType construction
                RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
                queryEngine.getDefaultNamespace();

                queryEngine.getDefaultStyles();
                queryEngine.getDefaultStyles(defaultWorkspaceQuery);
            }
        }
    }

}
