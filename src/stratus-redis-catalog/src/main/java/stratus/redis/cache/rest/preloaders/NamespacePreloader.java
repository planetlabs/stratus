/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.*;
import org.springframework.stereotype.Component;
import stratus.redis.cache.CatalogCache;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.CacheVisitor;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */
@Component
public class NamespacePreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/namespaces/{namespace}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String namespaceName = pathVariables.get(NAMESPACE);
        RedisValueQuery<NamespaceInfo> namespaceQuery;

        //If name == "default", DELETE not supported, and getDefault instead of get by name
        if ("default".equals(namespaceName)) {
            namespaceQuery = queryEngine.getDefaultNamespace();
            if ("DELETE".equals(method)) {
                return;
            }
        } else {
            namespaceQuery = queryEngine.getNamespaceByPrefix(namespaceName);
            if ("GET".equals(method)) {
                queryEngine.getDefaultNamespace();
            }
        }

        if ("GET".equals(method) || "DELETE".equals(method)) {
            RedisValueQuery<WorkspaceInfo> workspaceQuery = queryEngine.getWorkspaceByName(namespaceName);
            queryEngine.getStores(workspaceQuery, DataStoreInfo.class);
            queryEngine.getStores(workspaceQuery, CoverageStoreInfo.class);
            queryEngine.getStores(workspaceQuery, WMSStoreInfo.class);

            //Mark all stores in the workspace as cached
            final CacheVisitor oldWsVisitor = workspaceQuery.getCacheVisitor();
            workspaceQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                oldWsVisitor.apply(cache, value);
                if (value.get() != null) {
                    cache.setStoresCached((WorkspaceInfo) value.get(), StoreInfo.class, true);
                }
            });

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

            if ("DELETE".equals(method)) {
                //DELETE namespace has no recursive option (is this a bug?)
                queryEngine.getDefaultWorkspace();
                queryEngine.getDefaultNamespace();
            }

        } else if ("PUT".equals(method)) {
            queryEngine.getWorkspaceByName(namespaceName);
        }
    }

}
