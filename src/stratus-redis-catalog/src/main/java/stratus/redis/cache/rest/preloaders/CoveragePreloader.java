/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;
import stratus.redis.index.engine.RedisValuesQuery;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class CoveragePreloader implements Preloader {

    private static final String[] PATHS = {
        "/rest/workspaces/{workspace}/coverages/{coverage}",
        "/rest/workspaces/{workspace}/coveragestores/{coveragestore}/coverages/{coverage}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String coveragestoreName = pathVariables.get(COVERAGESTORE);
        String coverageName = pathVariables.get(COVERAGE);
        //DELETE seems to be the most important one...
        if (workspaceName != null) {
            RedisValueQuery workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            RedisValueQuery coverageQuery;
            RedisValueQuery coveragestoreQuery;
            if (coveragestoreName == null) {
                coverageQuery = queryEngine.getResourceByName(namespaceQuery, coverageName, CoverageInfo.class);
                coveragestoreQuery = queryEngine.getStoreByResource(coverageQuery, CoverageStoreInfo.class);
            } else {
                coveragestoreQuery = queryEngine.getStoreByName(workspaceQuery, coveragestoreName, CoverageStoreInfo.class);
                coverageQuery = queryEngine.getResourceByStore(coveragestoreQuery, coverageName, CoverageInfo.class);
            }

            if ("DELETE".equals(method)) {
                //Get Layers by resource
                RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayers(coverageQuery);
                queryEngine.getStyles(layersQuery);

                //Get coverages by store
                queryEngine.getResourcesByStore(coveragestoreQuery, CoverageInfo.class);
                RedisValuesQuery<LayerGroupInfo> layerGroupsQuery = queryEngine.getLayerGroups();
                layersQuery = queryEngine.getLayersByLayerGroups(layerGroupsQuery);
                queryEngine.getStyles(layersQuery);
            }
        }
    }
}