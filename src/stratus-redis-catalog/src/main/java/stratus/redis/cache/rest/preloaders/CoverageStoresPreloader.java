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
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class CoverageStoresPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/coveragestores"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        if (workspaceName != null) {
            //Main objects
            RedisValueQuery<WorkspaceInfo> workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);

            if ("GET".equals(method)) {
                RedisValuesQuery<CoverageStoreInfo> storesQuery = queryEngine.getStores(workspaceQuery, CoverageStoreInfo.class);
                /* Workaround to indicate to the cache that all resources for these stores are present.
                 *
                 * The REST api constructs a coverageStore model containing all the coverages which is used by both
                 * the list coveragestores endpoint (this one), and the get coveragestore endpoint. However, only the get
                 * datastore endpoint needs the coverages, so here we mark that all relevant coverages are already
                 * in the cache, so that we don't query redis for data we don't need.
                 */
                final CacheVisitor oldVisitor = storesQuery.getCacheVisitor();
                storesQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                    oldVisitor.apply(cache, value);
                    if (value.get() != null) {
                        for (StoreInfo store : (List<? extends StoreInfo>) value.get()) {
                            cache.setResourcesCached(store, CoverageInfo.class, true);
                        }
                    }
                });

            } else if ("POST".equals(method)) {
                queryEngine.getNamespaceByPrefix(workspaceName);
                //Not querying stores by name as this requires preemptively decoding object body
                //Defaults used in store construction
                RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
            }
        }
    }
}
