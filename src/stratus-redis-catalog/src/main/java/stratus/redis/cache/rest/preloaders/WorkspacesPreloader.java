/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.CatalogCache;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.CacheVisitor;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */
@Component
public class WorkspacesPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    /**
     * Preload catalog data for /workspaces
     *
     * Prevents unnecessary querying for stores
     *
     * @param method GET or POST
     * @param queryEngine
     */
    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        queryEngine.getDefaultWorkspace();

        if ("GET".equals(method)) {
            RedisValuesQuery<WorkspaceInfo> workspacesQuery = queryEngine.getWorkspaces();
            /* Workaround to indicate to the cache that all stores for these workspaces are present.
             *
             * The REST api constructs a workspace model containing all the stores which is used by both
             * the list workspaces endpoint (this one), and the get workspace endpoint. However, only the get
             * workspace endpoint needs the stores, so here we mark that all relevant stores are already
             * in the cache, so that we don't query redis for data we don't need.
             */
            final CacheVisitor oldVisitor = workspacesQuery.getCacheVisitor();
            workspacesQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                oldVisitor.apply(cache, value);
                if (value.get() != null) {
                    for (WorkspaceInfo workspace : (List<WorkspaceInfo>) value.get()) {
                        cache.setStoresCached(workspace, StoreInfo.class, true);
                    }
                }
            });
        } else if ("POST".equals(method)) {
            queryEngine.getDefaultNamespace();
        }
    }
}
