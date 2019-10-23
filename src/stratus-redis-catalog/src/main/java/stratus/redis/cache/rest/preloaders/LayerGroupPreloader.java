/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.IndexEntry;
import stratus.redis.index.LayerGroupIndex;
import stratus.redis.index.LayerIndex;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.index.engine.DefaultRedisQueryKey;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import org.geoserver.catalog.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class LayerGroupPreloader implements Preloader {

    @Autowired
    @Qualifier("catalog")
    Catalog catalog;

    @Autowired
    RedisLayerIndexFacade indexFacade;

    private static final String[] PATHS = {
            "/rest/layergroups/{layergroup}",
            "/rest/workspaces/{workspace}/layergroups/{layergroup}"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        String workspaceName = pathVariables.get(WORKSPACE);
        String layerGroupName = pathVariables.get(LAYERGROUP);
        //GET - layer groups, contained layers & styles; workspaces, namespaces, resources, stores, styles of layers
        if ("GET".equals(method)) {
            //Take advantage of the layer group index here:
            if (workspaceName != null && !"".equals(workspaceName)) {
                layerGroupName = workspaceName + ":" + layerGroupName;
            }
            List<LayerGroupIndex> layerGroupIndices = indexFacade.getLayerGroupIndices(Collections.singletonList(layerGroupName));
            if (layerGroupIndices.size() != 1) {
                return;
            }
            preloadLayerGroupsByIndices(layerGroupIndices, queryEngine);
        } else if ("PUT".equals(method)) {
            //We need all the layer groups so we can update the indices
            preloadLayerGroupsByIndices(indexFacade.getLayerGroupIndices(), queryEngine);
            //Also get the default workspace, in case the PUTed layers are not prefixed
            queryEngine.getDefaultNamespace();
            queryEngine.getDefaultWorkspace();
            //Some queries depend upon content of the PUT, can't effectively cache those

        } else if ("DELETE".equals(method)) {
            //We need all the layer groups so we can update the indices
            preloadLayerGroupsByIndices(indexFacade.getLayerGroupIndices(), queryEngine);
        }

        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);
        cachingCatalogFacade.getCache().setLayerGroupsCached(true);

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
