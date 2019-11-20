/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.stereotype.Component;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValueQuery;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class CoveragesPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/workspaces/{workspace}/coverages",
            "/rest/workspaces/{workspace}/coveragestores/{coveragestore}/coverages"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String coveragestoreName = pathVariables.get(COVERAGESTORE);

        if (workspaceName != null) {
            //Main objects
            RedisValueQuery<WorkspaceInfo> workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
            RedisValueQuery<NamespaceInfo> namespaceQuery = queryEngine.getNamespaceByPrefix(workspaceName);

            if ("GET".equals(method)) {
                if (coveragestoreName != null) {
                    RedisValueQuery<CoverageStoreInfo> coveragestoreQuery = queryEngine.getStoreByName(workspaceQuery, coveragestoreName, CoverageStoreInfo.class);
                    queryEngine.getResourcesByStore(coveragestoreQuery, CoverageInfo.class);
                } else {
                    queryEngine.getResourcesByNamespace(namespaceQuery, CoverageInfo.class);
                    queryEngine.getStores(workspaceQuery, CoverageStoreInfo.class);
                }
            } else if ("POST".equals(method) && coveragestoreName != null) {
                RedisValueQuery coveragestoreQuery = queryEngine.getStoreByName(workspaceQuery, coveragestoreName, CoverageStoreInfo.class);
                // Not querying coverage as this requires decoding object body, which needs a bunch of catalog lookups

                //Defaults used in coverage construction
                RedisValueQuery<WorkspaceInfo> defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
                queryEngine.getDefaultNamespace();

                queryEngine.getDefaultStyles();
                queryEngine.getDefaultStyles(defaultWorkspaceQuery);
            }
        }
    }

}