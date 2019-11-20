/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.DefaultRedisQueryKey;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisStaticValueQuery;
import stratus.redis.index.engine.RedisValueQuery;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class StylePreloader implements Preloader {

    @Autowired
    @Qualifier("catalog")
    Catalog catalog;

    private static final String[] PATHS = {
            "/rest/styles/{style}",
            "/rest/workspaces/{workspace}/styles/{style}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String styleName = pathVariables.get(STYLE);
        RedisValueQuery<WorkspaceInfo> workspaceQuery;
        if (workspaceName == null || "".equals(workspaceName)) {
            //Dummy query to hold "NO_WORKSPACE" id and value
            workspaceQuery = new RedisStaticValueQuery<>(null, new DefaultRedisQueryKey<>(WorkspaceInfo.class, ""),
                    new RedisMultiQueryCachingEngine.EmptyCatalogCacheVisitor());
        } else {
            workspaceQuery = queryEngine.getWorkspaceByName(workspaceName);
        }
        queryEngine.getStyleByName(workspaceQuery, styleName);
        //Nothing extra needed for PUT, just GET and DELETE
        if ("GET".equals(method) && (workspaceName == null || "".equals(workspaceName))) {
            //Also try with the default workspace:
            RedisValueQuery<WorkspaceInfo> defaultWorkspaceQuery = queryEngine.getDefaultWorkspace();
            queryEngine.getStyleByName(defaultWorkspaceQuery, styleName);
        } else if ("DELETE".equals(method)) {
            //Get all layers and layer groups to test for contained styles
            //TODO: If we ever get filters working better, rely on those instead
            //Alternatively, don't do any caching for DELETE; its probably not an improvement...
            queryEngine.getLayers();
            queryEngine.getLayerGroups();
            queryEngine.getResources(FeatureTypeInfo.class);
            queryEngine.getResources(CoverageInfo.class);
            queryEngine.getResources(WMSLayerInfo.class);
            queryEngine.getStores(CoverageStoreInfo.class);
            queryEngine.getStores(DataStoreInfo.class);
            queryEngine.getStores(WMSStoreInfo.class);
            queryEngine.getNamespaces();
            queryEngine.getWorkspaces();
        }

        if (null != workspaceName) {
            CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);
            (cachingCatalogFacade.getCache()).setLayerGroupsCached(true);
        }
    }
}