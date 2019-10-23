/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class FeatureTypePreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/featuretypes/{featuretype}",
            "/rest/workspaces/{workspace}/datastores/{datastore}/featuretypes/{featuretype}"
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
        String featuretypeName = pathVariables.get(FEATURETYPE);
        //DELETE seems to be the most important one...
        if (workspaceName != null) {
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            RedisValueQuery datastoreQuery;
            RedisValueQuery featureTypeQuery;
            if (datastoreName == null) {
                featureTypeQuery = queryEngine.getResourceByName(namespaceQuery, featuretypeName, FeatureTypeInfo.class);
                datastoreQuery = queryEngine.getStoreByResource(featureTypeQuery, DataStoreInfo.class);
            } else {
                datastoreQuery = queryEngine.getStoreByName(workspaceQuery, datastoreName, DataStoreInfo.class);
                featureTypeQuery = queryEngine.getResourceByStore(datastoreQuery, featuretypeName, FeatureTypeInfo.class);
            }

            if ("DELETE".equals(method)) {
                //Get default store
                queryEngine.getDefaultStore(workspaceQuery);
                //Get Layers by resource
                RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayers(featureTypeQuery);
                queryEngine.getStyles(layersQuery);

                //Get featureTypes by store
                queryEngine.getResourcesByStore(datastoreQuery, FeatureTypeInfo.class);
                RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                queryEngine.getResourcesByLayers(layersQuery, FeatureTypeInfo.class);
                queryEngine.getStyles(layersQuery);
            }
        }

    }
}
