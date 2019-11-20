/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.ResourceInfo;
import org.springframework.stereotype.Component;
import stratus.redis.cache.CatalogCache;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.CacheVisitor;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValuesQuery;

import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */
@Component
public class NamespacesPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/namespaces"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    /**
     * Preload catalog data for /namespaces
     *
     * Prevents unnecessary querying for resources
     *
     * @param method GET or POST
     * @param queryEngine
     */
    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        queryEngine.getDefaultNamespace();

        if ("GET".equals(method)) {
            RedisValuesQuery<NamespaceInfo> namespacesQuery = queryEngine.getNamespaces();
            queryEngine.getWorkspaces();
            /* Workaround to indicate to the cache that all resources for these namespaces are present.
             *
             * The REST api constructs a namespace model containing all the resources which is used by both
             * the list namespaces endpoint (this one), and the get namespace endpoint. However, only the get
             * namespace endpoint needs the resources, so here we mark that all relevant resources are already
             * in the cache, so that we don't query redis for data we don't need.
             */
            final CacheVisitor oldVisitor = namespacesQuery.getCacheVisitor();
            namespacesQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                oldVisitor.apply(cache, value);
                if (value.get() != null) {
                    for (NamespaceInfo namespace : (List<NamespaceInfo>) value.get()) {
                        cache.setResourcesCached(namespace, ResourceInfo.class, true);
                    }
                }
            });
        } else if ("POST".equals(method)) {
            queryEngine.getDefaultWorkspace();
        }
    }
}
