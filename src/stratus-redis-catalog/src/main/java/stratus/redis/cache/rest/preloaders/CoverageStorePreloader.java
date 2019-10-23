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
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.ResourceInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class CoverageStorePreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/coveragestores/{coveragestore}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String coverageStoreName = pathVariables.get(COVERAGESTORE);
        if (workspaceName != null) {
            //Main objects
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery coveragestoreQuery = queryEngine.getStoreByName(workspaceQuery, coverageStoreName, CoverageStoreInfo.class);
            //PUT just needs ws and store
            if ("GET".equals(method) || "DELETE".equals(method)) {
                RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);
                RedisValuesQuery coveragesQuery = queryEngine.getResourcesByStore(coveragestoreQuery, CoverageInfo.class);
                //Nothing more needed for GET
                if ("DELETE".equals(method)) {
                    final CacheVisitor oldVisitor = coveragestoreQuery.getCacheVisitor();
                    coveragestoreQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                        oldVisitor.apply(cache, value);
                        if (value.get() != null) {
                            //Set all resource types as cached
                            cache.setResourcesCached((CoverageStoreInfo) value.get(), ResourceInfo.class, true);
                        }
                    });
                    RedisValuesQuery layersQuery = queryEngine.getLayersByResources(coveragesQuery);
                    queryEngine.getStyles(layersQuery);

                    //Get layer groups
                    RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                    layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                    queryEngine.getResourcesByLayers(layersQuery, CoverageInfo.class);
                    queryEngine.getStyles(layersQuery);

                    //Get Default ws and store
                    RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
                    queryEngine.getDefaultStore(defaultWorkspaceQuery);
                }
            }
        }
    }
}
