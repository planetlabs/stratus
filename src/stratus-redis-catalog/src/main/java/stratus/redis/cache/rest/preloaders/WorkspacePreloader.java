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
import org.geoserver.catalog.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */
@Component
public class WorkspacePreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);

        RedisValueQuery<WorkspaceInfo> workspaceQuery;

        //If name == "default", DELETE not supported, and getDefault instead of get by name
        if ("default".equals(workspaceName)) {
            workspaceQuery = queryEngine.getDefaultWorkspace();
            if ("DELETE".equals(method)) {
                return;
            }
        } else {
            workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            if ("GET".equals(method)) {
                queryEngine.getDefaultWorkspace();
            }
        }

        if ("GET".equals(method) || "DELETE".equals(method)) {
            queryEngine.getStores(workspaceQuery, DataStoreInfo.class);
            queryEngine.getStores(workspaceQuery, CoverageStoreInfo.class);
            queryEngine.getStores(workspaceQuery, WMSStoreInfo.class);

            //Mark all stores in the workspace as cached
            final CacheVisitor oldWsVisitor = workspaceQuery.getCacheVisitor();
            workspaceQuery.setVisitor((cache, value) -> {
                oldWsVisitor.apply(cache, value);
                if (value.get() != null) {
                    if (cache instanceof CatalogCache) {
                        ((CatalogCache) cache).setStoresCached((WorkspaceInfo) value.get(), StoreInfo.class, true);
                    }
                }
            });

            if ("DELETE".equals(method)) {
                RedisValueQuery<NamespaceInfo> namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);
                RedisValuesQuery<FeatureTypeInfo> ftQuery = queryEngine.getResourcesByNamespace(namespaceQuery, FeatureTypeInfo.class);
                RedisValuesQuery<CoverageInfo> coverageQuery = queryEngine.getResourcesByNamespace(namespaceQuery, CoverageInfo.class);
                RedisValuesQuery<WMSLayerInfo> wmsQuery = queryEngine.getResourcesByNamespace(namespaceQuery, WMSLayerInfo.class);
                //Mark all resources in the namespace as cached
                final CacheVisitor oldNsVisitor = namespaceQuery.getCacheVisitor();
                namespaceQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                    oldNsVisitor.apply(cache, value);
                    if (value.get() != null) {
                        cache.setResourcesCached((NamespaceInfo) value.get(), ResourceInfo.class, true);
                    }
                });

                RedisValuesQuery<LayerInfo> covLayersQuery = queryEngine.getLayersByResources(coverageQuery);
                RedisValuesQuery<LayerInfo> ftLayersQuery = queryEngine.getLayersByResources(ftQuery);
                RedisValuesQuery<LayerInfo> wmsLayersQuery = queryEngine.getLayersByResources(wmsQuery);

                queryEngine.getStyles(covLayersQuery);
                queryEngine.getStyles(ftLayersQuery);
                queryEngine.getStyles(wmsLayersQuery);

                //Get layer groups
                RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                queryEngine.getResourcesByLayers(layersQuery, FeatureTypeInfo.class);
                queryEngine.getStyles(layersQuery);
            }
        } else if ("PUT".equals(method)) {
            queryEngine.getNamespaceByPrefix(workspaceName);
        }
    }
}
