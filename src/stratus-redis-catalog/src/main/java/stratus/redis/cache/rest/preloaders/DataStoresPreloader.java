/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.StoreInfo;
import org.springframework.stereotype.Component;
import stratus.redis.cache.CatalogCache;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.CacheVisitor;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;

import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class DataStoresPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/datastores"
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
                RedisValuesQuery<DataStoreInfo> storesQuery = queryEngine.getStores(workspaceQuery, DataStoreInfo.class);
                /* Workaround to indicate to the cache that all resources for these stores are present.
                 *
                 * The REST api constructs a dataStore model containing all the feature types which is used by both
                 * the list datastores endpoint (this one), and the get datastore endpoint. However, only the get
                 * datastore endpoint needs the featuretypes, so here we mark that all relevant featuretypes are already
                 * in the cache, so that we don't query redis for data we don't need.
                 */
                final CacheVisitor oldVisitor = storesQuery.getCacheVisitor();
                storesQuery.setVisitor((CacheVisitor<CatalogCache>) (cache, value) -> {
                    oldVisitor.apply(cache, value);
                    if (value.get() != null) {
                        for (StoreInfo store : (List<? extends StoreInfo>) value.get()) {
                            cache.setResourcesCached(store, FeatureTypeInfo.class, true);
                        }
                    }
                });
                //Don't need these:
                //RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);
                //queryEngine.getResourcesByNamespace(namespaceQuery, FeatureTypeInfo.class);
            } else if ("POST".equals(method)) {
                queryEngine.getNamespaceByPrefix(workspaceName);
                //Not querying stores by name as this requires preemptively decoding object body
                //Defaults used in store construction
                RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
            }
        }
    }
}