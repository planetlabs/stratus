/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.IndexEntry;
import stratus.redis.index.LayerGroupIndex;
import stratus.redis.index.LayerIndex;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.index.engine.DefaultRedisQueryKey;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;

import java.util.*;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class LayerGroupsPreloader implements Preloader {

    @Autowired
    RedisLayerIndexFacade indexFacade;

    @Autowired
    @Qualifier("catalog")
    Catalog catalog;

    private static final String[] PATHS = {
            "/rest/layergroups",
            "/rest/workspaces/{workspace}/layergroups"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        //GET - layer groups, contained layers & styles; workspaces, namespaces, resources, stores, styles of layers
        if ("GET".equals(method)) {
            //Take advantage of the layer group index here:
            List<LayerGroupIndex> layerGroupIndices = indexFacade.getLayerGroupIndices();
            List<LayerGroupIndex> filteredLayerGroupIndices = new ArrayList<>();

            for (LayerGroupIndex index : layerGroupIndices) {
                //global layer group
                String lgPrefix = "";
                if (workspaceName == null) {
                    workspaceName = "";
                }
                int wsIndex = (index.getName().indexOf(':'));
                if (wsIndex > 0) {
                    lgPrefix = index.getName().substring(0, wsIndex);
                }
                if (workspaceName.equals(lgPrefix)) {
                    filteredLayerGroupIndices.add(index);
                }
            }
            preloadLayerGroupsByIndices(filteredLayerGroupIndices, queryEngine);
        } else if ("POST".equals(method)) {
            //Mostly depends upon contents of the POST, not a lot can be cached here
            //get the default workspace, in case the POSTed layers are not prefixed
            queryEngine.getDefaultNamespace();
            queryEngine.getDefaultWorkspace();
            if (workspaceName != null && "".equals(workspaceName)) {
                queryEngine.getWorkspaceByName(workspaceName);
                queryEngine.getNamespaceByPrefix(workspaceName);
            }
        }

        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);
        (cachingCatalogFacade.getCache()).setLayerGroupsCached(true);
    }

    protected void preloadLayerGroupsByIndices(Collection<LayerGroupIndex> indices, RedisMultiQueryCachingEngine queryEngine) {

        List<IndexEntry> catalogIds = new ArrayList<>();
        List<String> layerNames = new ArrayList<>();
        for (LayerGroupIndex index : indices) {
            layerNames.addAll(index.getLayers());
            catalogIds.addAll(index.getLayerGroups());
        }

        List<LayerIndex> layerIndices = indexFacade.getLayerIndices(layerNames);
        for (LayerIndex index : layerIndices) {
            catalogIds.addAll(index.getIds());
        }
        //make id list unique
        catalogIds = new ArrayList<>(new HashSet<>(catalogIds));
        for (IndexEntry id : catalogIds) {
            queryEngine.get(new DefaultRedisQueryKey(id.getClazz(), id.getId(), null),
                    new RedisMultiQueryCachingEngine.EmptyCatalogCacheVisitor());
        }
    }
}