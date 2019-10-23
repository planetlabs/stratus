/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class WmsLayersPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/wmslayers",
            "/rest/workspaces/{workspace}/wmsstores/{wmsstore}/wmslayers"};

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String wmsstoreName = pathVariables.get(WMSSTORE);
        if (workspaceName != null) {
            //Main objects
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            if ("GET".equals(method)) {
                if (wmsstoreName != null) {
                    RedisValueQuery wmsstoreQuery = queryEngine.getStoreByName(workspaceQuery, wmsstoreName, WMSStoreInfo.class);
                    queryEngine.getResourcesByStore(wmsstoreQuery, WMSLayerInfo.class);
                } else {
                    RedisValuesQuery wmslayerQuery = queryEngine.getResourcesByNamespace(namespaceQuery, WMSLayerInfo.class);
                    queryEngine.getStores(workspaceQuery, WMSStoreInfo.class);
                }
            } else if ("POST".equals(method) && wmsstoreName != null) {
                RedisValueQuery wmsstoreQuery = queryEngine.getStoreByName(workspaceQuery, wmsstoreName, WMSStoreInfo.class);
                //Not querying featuretype as this requires decoding object body, which needs a bunch of catalog lookups

                //Defaults used in WMSLayer construction
                RedisValueQuery defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
                queryEngine.getDefaultNamespace();

                queryEngine.getDefaultStyles();
                queryEngine.getDefaultStyles(defaultWorkspaceQuery);
            }
        }

    }
}