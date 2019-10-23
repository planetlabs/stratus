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
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class WmsStoresPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/wmsstores"
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
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);

            if ("GET".equals(method)) {
                RedisValuesQuery<WMSStoreInfo> storesQuery = queryEngine.getStores(workspaceQuery, WMSStoreInfo.class);
                //Workaround to indicate to the cache that all resources for these stores are present.
                final CacheVisitor oldVisitor = storesQuery.getCacheVisitor();
                storesQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                    oldVisitor.apply(cache, value);
                    if (value.get() != null) {
                        for (StoreInfo store : (List<? extends StoreInfo>) value.get()) {
                            cache.setResourcesCached(store, WMSLayerInfo.class, true);
                        }
                    }
                });
            } else if ("POST".equals(method)) {
                queryEngine.getNamespaceByPrefix(workspaceName);
                queryEngine.getDefaultWorkspace();
            }
        }
    }
}
