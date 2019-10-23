/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.CatalogCache;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.CacheVisitor;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.ResourceInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class DataStorePreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/datastores/{datastore}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String datastoreName = pathVariables.get(DATASTORE_ALT);
        if (workspaceName != null) {
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery<DataStoreInfo> storeQuery = queryEngine.getStoreByName(workspaceQuery, datastoreName, DataStoreInfo.class);

            //PUT just needs ws and store
            if ("GET".equals(method) || "DELETE".equals(method)) {
                RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);
                RedisValuesQuery featureTypesQuery = queryEngine.getResourcesByStore(storeQuery, FeatureTypeInfo.class);
                //Nothing more needed for GET
                if ("DELETE".equals(method)) {
                    final CacheVisitor oldVisitor = storeQuery.getCacheVisitor();
                    storeQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                        oldVisitor.apply(cache, value);
                        if (value.get() != null) {
                            //Set all resource types as cached
                            cache.setResourcesCached((DataStoreInfo) value.get(), ResourceInfo.class, true);
                        }
                    });
                    RedisValuesQuery layersQuery = queryEngine.getLayersByResources(featureTypesQuery);
                    queryEngine.getStyles(layersQuery);

                    //Get layer groups
                    RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                    layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                    queryEngine.getResourcesByLayers(layersQuery, FeatureTypeInfo.class);
                    queryEngine.getStyles(layersQuery);

                    //Get Default ws and store
                    RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
                    queryEngine.getDefaultStore(defaultWorkspaceQuery);
                }
            }
        }
    }
}
