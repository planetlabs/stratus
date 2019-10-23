/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.DefaultRedisQueryKey;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisStaticValueQuery;
import stratus.redis.index.engine.RedisValueQuery;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.geoserver.catalog.CatalogFacade.NO_WORKSPACE;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class StylesPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/styles",
            "/rest/workspaces/{workspace}/styles"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        RedisValueQuery<WorkspaceInfo> workspaceQuery;
        if (workspaceName == null || "".equals(workspaceName)) {
            //Dummy query to hold "NO_WORKSPACE" id and value
            workspaceQuery = new RedisStaticValueQuery<>(NO_WORKSPACE, new DefaultRedisQueryKey<>(WorkspaceInfo.class, ""),
                    new RedisMultiQueryCachingEngine.EmptyCatalogCacheVisitor());
        } else {
            workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
        }
        if ("GET".equals(method)) {
            queryEngine.getStylesByWorkspace(workspaceQuery);
        }
        //POST just needs workspace (if applicable), nothing more
    }
}
