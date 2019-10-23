/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class LayerPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/layers/{layer}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String layerName = pathVariables.get(LAYER);
        RedisValueQuery workspaceQuery;
        RedisValueQuery namespaceQuery;

        //Split on ':'
        int colon = layerName.indexOf(':');
        if ( colon != -1 ) {
            //search by resource name
            String workspaceName = layerName.substring(0, colon);
            layerName = layerName.substring(colon + 1);

            workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);
        } else {
            //If no workspace, get and use default
            workspaceQuery = queryEngine.getDefaultWorkspace();
            namespaceQuery = queryEngine.getDefaultNamespace();
        }
        RedisValueQuery<CoverageInfo> coverageQuery = queryEngine.getResourceByName(namespaceQuery, layerName, CoverageInfo.class);
        RedisValueQuery<FeatureTypeInfo> ftQuery = queryEngine.getResourceByName(namespaceQuery, layerName, FeatureTypeInfo.class);
        RedisValueQuery<WMSLayerInfo> wmsQuery = queryEngine.getResourceByName(namespaceQuery, layerName, WMSLayerInfo.class);

        queryEngine.getStoreByResource(coverageQuery, CoverageStoreInfo.class);
        queryEngine.getStoreByResource(ftQuery, DataStoreInfo.class);
        queryEngine.getStoreByResource(wmsQuery, WMSStoreInfo.class);

        RedisValuesQuery<LayerInfo> covLayersQuery = queryEngine.getLayers(coverageQuery);
        RedisValuesQuery<LayerInfo> ftLayersQuery = queryEngine.getLayers(ftQuery);
        RedisValuesQuery<LayerInfo> wmsLayersQuery = queryEngine.getLayers(wmsQuery);

        queryEngine.getStyles(covLayersQuery);
        queryEngine.getStyles(ftLayersQuery);
        queryEngine.getStyles(wmsLayersQuery);

        if ("PUT".equals(method) || "DELETE".equals(method)) {
            //Get layer groups
            RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
            RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
            queryEngine.getResourcesByLayers(layersQuery, FeatureTypeInfo.class);
            queryEngine.getResourcesByLayers(layersQuery, CoverageInfo.class);
            queryEngine.getResourcesByLayers(layersQuery, WMSLayerInfo.class);
            queryEngine.getStyles(layersQuery);
        }
    }
}