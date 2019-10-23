/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class WmsLayerPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/wmslayers/{wmslayer}",
            "/rest/workspaces/{workspace}/wmsstores/{wmsstore}/wmslayers/{wmslayer}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String wmsstoreName = pathVariables.get(WMSSTORE);
        String wmslayerName = pathVariables.get(WMSLAYER);
        if (workspaceName != null) {
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            RedisValueQuery wmsstoreQuery;
            RedisValueQuery wmslayerQuery;
            if (wmsstoreName == null) {
                wmslayerQuery = queryEngine.getResourceByName(namespaceQuery, wmslayerName, WMSLayerInfo.class);
                wmsstoreQuery = queryEngine.getStoreByResource(wmslayerQuery, WMSStoreInfo.class);
            } else {
                wmsstoreQuery = queryEngine.getStoreByName(workspaceQuery, wmsstoreName, WMSStoreInfo.class);
                wmslayerQuery = queryEngine.getResourceByStore(wmsstoreQuery, wmslayerName, WMSLayerInfo.class);
            }

            if ("DELETE".equals(method)) {
                //Get default store
                queryEngine.getDefaultStore(workspaceQuery);
                //Get Layers by resource
                RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayers(wmslayerQuery);
                queryEngine.getStyles(layersQuery);

                //Get featureTypes by store
                queryEngine.getResourcesByStore(wmsstoreQuery, WMSLayerInfo.class);
                RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                queryEngine.getResourcesByLayers(layersQuery, WMSLayerInfo.class);
                queryEngine.getStyles(layersQuery);
            }
        }
    }
}
