/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.index.engine.DefaultRedisQueryKey;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Access point for {@link LayerIndex}, {@link LayerGroupIndex}, and {@link VirtualServiceIndex} operations against the redis repository.
 * <p>
 * Provides CRUD functions for layer, layer group, and virtual service indices.
 * Also provides some utility methods to simplify decoding these indices.
 */
@Slf4j
@Service
public class RedisLayerIndexFacade {

    @Autowired
    public CacheProperties cacheProperties;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConverter converter;

    public RedisLayerIndexFacade(@Qualifier("transactionalRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                 RedisConverter converter) {
        this.redisTemplate = redisTemplate;
        this.converter = converter;
    }

    /**
     * Remove the passed {@link LayerIndex}
     *
     * @param layerIndex Index to remove
     */
    public void delete(LayerIndex layerIndex) {
        redisTemplate.opsForHash().delete(LayerIndex.class.getSimpleName(), layerIndex.getName());
    }

    /**
     * Remove the passed {@link LayerGroupIndex}
     *
     * @param layerGroupIndex Index to remove
     */
    public void delete(LayerGroupIndex layerGroupIndex) {
        redisTemplate.opsForHash().delete(LayerGroupIndex.class.getSimpleName(), layerGroupIndex.getName());
    }

    /**
     * Remove the passed {@link VirtualServiceIndex}
     *
     * @param virtualServiceIndex Index to remove
     */
    public void delete(VirtualServiceIndex virtualServiceIndex) {
        redisTemplate.opsForHash().delete(VirtualServiceIndex.class.getSimpleName(), virtualServiceIndex.getName());
    }

    /**
     * Remove the {@link LayerIndex} corresponding to the named layer.
     *
     * @param name Name of the layer to remove the index for.
     */
    public void deleteLayerIndexByName(String name) {
        redisTemplate.opsForHash().delete(LayerIndex.class.getSimpleName(), name);
    }

    /**
     * Remove the {@link LayerGroupIndex} corresponding to the named layer group.
     *
     * @param name Name of the layer group to remove the index for.
     */
    public void deleteLayerGroupIndexByName(String name) {
        redisTemplate.opsForHash().delete(LayerGroupIndex.class.getSimpleName(), name);
    }

    /**
     * Remove the {@link VirtualServiceIndex} corresponding to the named object.
     *
     * @param name Name of the object to remove the index for.
     */
    public void deleteVirtualServiceIndexByName(String name) {
        redisTemplate.opsForHash().delete(VirtualServiceIndex.class.getSimpleName(), name);
    }

    /**
     * Create or update the provided {@LayerIndex}
     *
     * @param layerIndex
     */
    public void save(LayerIndex layerIndex) {
        redisTemplate.opsForHash().put(LayerIndex.class.getSimpleName(), layerIndex.getName(), layerIndex);
    }

    /**
     * Create or update the provided {@LayerGroupIndex}
     *
     * @param layerGroupIndex
     */
    public void save(LayerGroupIndex layerGroupIndex) {
        redisTemplate.opsForHash().put(LayerGroupIndex.class.getSimpleName(), layerGroupIndex.getName(), layerGroupIndex);
    }

    /**
     * Create or update the provided {@VirtualServiceIndex}
     *
     * @param virtualServiceIndex
     */
    public void save(VirtualServiceIndex virtualServiceIndex) {
        redisTemplate.opsForHash().put(VirtualServiceIndex.class.getSimpleName(), virtualServiceIndex.getName(), virtualServiceIndex);
    }

    /**
     * Create or update each entry in the provided list of {@LayerIndex}
     *
     * @param indices List of layer indices
     */
    public void saveLayerIndices(List<LayerIndex> indices) {
        for (LayerIndex index : indices) {
            save(index);
        }
    }

    /**
     * Create or update each entry in the provided list of {@LayerGroupIndex}
     *
     * @param indices List of layer group indices
     */
    public void saveLayerGroupIndices(List<LayerGroupIndex> indices) {
        for (LayerGroupIndex index : indices) {
            save(index);
        }
    }

    /**
     * Create or update each entry in the provided list of {@VirtualServiceIndex}
     *
     * @param indices List of virtual service indices
     */
    public void saveVirtualServiceIndices(List<VirtualServiceIndex> indices) {
        for (VirtualServiceIndex index : indices) {
            save(index);
        }
    }

    /**
     * Get the {@link LayerIndex} corresponding to the layer with the provided name
     *
     * @param name Name of the layer
     * @return
     */
    public LayerIndex getLayerIndex(String name) {
        return (LayerIndex) redisTemplate.opsForHash().get(LayerIndex.class.getSimpleName(), name);
    }

    /**
     * Get the {@link LayerGroupIndex} corresponding to the layer group with the provided name
     *
     * @param name Name of the layer group
     * @return
     */
    public LayerGroupIndex getLayerGroupIndex(String name) {
        return (LayerGroupIndex) redisTemplate.opsForHash().get(LayerGroupIndex.class.getSimpleName(), name);
    }

    /**
     * Get the {@link VirtualServiceIndex} corresponding to the object with the provided name
     *
     * @param name Name of the object
     * @return
     */
    public VirtualServiceIndex getVirtualServiceIndex(String name) {
        return (VirtualServiceIndex) redisTemplate.opsForHash().get(VirtualServiceIndex.class.getSimpleName(), name);
    }

    /**
     * Given a  list of layer names, returns a list of the corresponding {@link LayerIndex}es
     *
     * @param names Ordered list of layer names.
     * @return Ordered list of layer indices.
     */
    public List<LayerIndex> getLayerIndices(Collection<String> names) {
        return removeNulls(redisTemplate.opsForHash().multiGet(LayerIndex.class.getSimpleName(), (Collection)names));
    }

    /**
     * Returns all {@link LayerIndex} entries
     *
     * @return List of layer indices.
     */
    public List<LayerIndex> getLayerIndices() {
        return (List) redisTemplate.opsForHash().values(LayerIndex.class.getSimpleName());
    }

    /**
     * Given an ordered list of mixed layer and layer group names, returns a list of the {@link LayerGroupIndex}es
     * corresponding to the layer group names included in the list.
     *
     * @param names Ordered list of layer and layer group names.
     * @return Ordered list of layer group indices.
     */
    public List<LayerGroupIndex> getLayerGroupIndices(Collection<String> names) {
        return removeNulls(redisTemplate.opsForHash().multiGet(LayerGroupIndex.class.getSimpleName(), (Collection)names));
    }

    /**
     * Returns all {@link VirtualServiceIndex} entries
     *
     * @return List of virtual service indices.
     */
    public List<VirtualServiceIndex> getVirtualServiceIndices() {
        return (List)redisTemplate.opsForHash().values(LayerGroupIndex.class.getSimpleName());
    }

    /**
     * Given a  list of object names, returns a list of the corresponding {@link VirtualServiceIndex}es
     *
     * @param names Ordered list of workspace / global layer group names.
     * @return Ordered list of virtual service indices.
     */
    public List<VirtualServiceIndex> getVirtualServiceIndices(Collection<String> names) {
        return removeNulls(redisTemplate.opsForHash().multiGet(VirtualServiceIndex.class.getSimpleName(), (Collection)names));
    }

    /**
     * Returns all {@link LayerGroupIndex} entries
     *
     * @return List of layer group indices.
     */
    public List<LayerGroupIndex> getLayerGroupIndices() {
        return (List)redisTemplate.opsForHash().values(LayerGroupIndex.class.getSimpleName());
    }

    private <T> List<T> removeNulls(List<T> values) {
        List<T> notNullValues = new ArrayList<>();
        if (null != values) {
            for (T value : values) {
                if (value != null) {
                    notNullValues.add(value);
                }
            }
        }
        return notNullValues;
    }

    /**
     * Given an ordered list of mixed layer and layer group names, and a list of the corresponding
     * {@link LayerGroupIndex}es, returns an ordered list of layer names, where each
     * layer group name has been replaced by the layers it is comprised of.
     *
     * @param layerAndLayerGroupNames Ordered list of layer and layer group names.
     * @param layerGroupIndices       List of layer group indices matching layerAndLayerGroupNames.
     * @return Ordered list of layer names.
     */
    protected static List<String> getLayerNames(Collection<String> layerAndLayerGroupNames, Collection<LayerGroupIndex> layerGroupIndices) {
        List<String> layerNames = new ArrayList<>();

        for (String layerName : layerAndLayerGroupNames) {
            boolean layerGroup = false;
            for (LayerGroupIndex index : layerGroupIndices) {
                if (layerName.equals(index.getName())) {
                    layerGroup = true;
                    layerNames.addAll(index.getLayers());
                    break;
                }
            }
            if (!layerGroup) {
                layerNames.add(layerName);
            }
        }
        return layerNames;
    }

    /**
     * Given a list of {@link LayerGroupIndex}es, returns a list of the corresponding layer group
     * {@link IndexEntry}s.
     *
     * @param layerGroupIndices List of layer group indices.
     * @return List of layer group {@link IndexEntry}s.
     */
    protected static List<IndexEntry> getLayerGroupIds(Collection<LayerGroupIndex> layerGroupIndices) {
        List<IndexEntry> layerGroupEntries = new ArrayList<>();

        for (LayerGroupIndex index : layerGroupIndices) {
            layerGroupEntries.addAll(index.getLayerGroups());
        }
        return layerGroupEntries;
    }

    /**
     * Given a list of catalog ids, retrieves the catalog info objects from the redis repository and loads them into the
     * cache.
     * <p>
     *
     * @param ids list of layer index entries
     * @return The catalog info objects that were loaded into the cache
     */
    public List<Info> loadCatalogInfos(Collection<IndexEntry> ids, CachingCatalogFacade cachingCatalogFacade) {
        RedisMultiQueryCachingEngine queryEngine = new RedisMultiQueryCachingEngine(redisTemplate, converter);
        for (IndexEntry id : ids) {
            queryEngine.get(new DefaultRedisQueryKey(id.getClazz(), id.getId(), null),
                    new RedisMultiQueryCachingEngine.EmptyCatalogCacheVisitor());
        }
        queryEngine.execute(cacheProperties.getUseParallelQueries());
        return queryEngine.loadIntoCache(cachingCatalogFacade, null, true);
    }

    /**
     * Given a list of mixed layer and layer group names, preloads the {@link CachingCatalogFacade} cache with all
     * {@link CatalogInfo} objects referenced by those layers.
     * <p>
     * This requires the catalog to be using an instance of {@link CachingCatalogFacade} as its facade, and for the
     * facade to be using an instance of {@link RedisCatalogFacade} as its delegate.
     *
     * @param layerAndLayerGroupNames List of layer / layer group names
     * @param catalog                 Catalog
     */
    public void preloadCacheByNames(List<String> layerAndLayerGroupNames, Catalog catalog) {
        if (layerAndLayerGroupNames.size() == 0) {
            return;
        }

        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);

        if (cachingCatalogFacade == null) {
            return;
        }
        List<LayerGroupIndex> layerGroupIndices = getLayerGroupIndices(layerAndLayerGroupNames);
        List<String> layerNames = getLayerNames(layerAndLayerGroupNames, layerGroupIndices);
        List<LayerIndex> layerIndices = getLayerIndices(layerNames);

        List<IndexEntry> catalogIds = getLayerGroupIds(layerGroupIndices);

        for (LayerIndex index : layerIndices) {
            catalogIds.addAll(index.getIds());
        }
        //make id list unique
        catalogIds = new ArrayList<>(new HashSet<>(catalogIds));

        loadCatalogInfos(catalogIds, cachingCatalogFacade);
    }

    /**
     * Given a context for a virtual OWS service, consisting of either a global layer group name, or a workspace name
     * and an (optional) layer name, preloads {@link CachingCatalogFacade} cache with all {@link CatalogInfo} objects
     * required by the virtual service.
     * <p>
     * This requires the catalog to be using an instance of {@link CachingCatalogFacade} as its facade, and for the
     * facade to be using an instance of {@link RedisCatalogFacade} as its delegate.
     *
     * @param workspaceOrLayerGroupName first context entry - workspace or global layer group name
     * @param layerName                 second context entry - layer name
     * @param catalog                   catalog
     */
    public void preloadCacheByVirtualService(String workspaceOrLayerGroupName, String layerName, Catalog catalog) {
        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);

        if (cachingCatalogFacade == null) {
            return;
        }

        if (workspaceOrLayerGroupName != null) {
            Set<String> layerNames = new HashSet<>();
            Set<IndexEntry> catalogIds = new HashSet<>();
            VirtualServiceIndex index = getVirtualServiceIndex(workspaceOrLayerGroupName);
            if (index != null) {
                if (index.getType() == VirtualServiceIndex.Type.LAYER_GROUP) {
                    //get layers by layer group
                    layerNames = index.getLayers();
                    catalogIds.addAll(index.getLayerGroups());
                } else {
                    if (layerName == null) {
                        //get layers by workspace
                        layerNames = index.getLayers();
                    } else {
                        //intersect (in case we have a layer group with layers outside this workspace)
                        LayerGroupIndex layerGroupIndex = getLayerGroupIndex(layerName);
                        if (layerGroupIndex != null) {
                            catalogIds.addAll(layerGroupIndex.getLayerGroups());
                            layerNames = layerGroupIndex.getLayers();
                        } else {
                            layerNames.add(layerName);
                        }
                        layerNames.retainAll(index.getLayers());
                    }
                }
                List<LayerIndex> layerIndices = getLayerIndices(layerNames);
                for (LayerIndex layerIndex : layerIndices) {
                    catalogIds.addAll(layerIndex.getIds());
                }

                if (catalogIds.size() > 0) {
                    loadCatalogInfos(catalogIds, cachingCatalogFacade);
                }

            } // else get nothing
        }
    }

    /**
     * Given a list of {@link IndexEntry} ids, preloads the {@link CachingCatalogFacade} cache with all objects
     * referenced by those ids.
     * <p>
     * This requires the catalog to be using an instance of {@link CachingCatalogFacade} as its facade, and for the
     * facade to be using an instance of {@link RedisCatalogFacade} as its delegate.
     *
     * @param ids     List of catalog ids
     * @param catalog Catalog
     */
    public void preloadCacheByIds(List<IndexEntry> ids, Catalog catalog) {
        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);

        if (cachingCatalogFacade == null) {
            return;
        }
        //make id list unique
        ids = new ArrayList<>(new HashSet<>(ids));

        loadCatalogInfos(ids, cachingCatalogFacade);
    }


}
