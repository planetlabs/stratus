/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geoserver.config.GeoServerInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.convert.RedisConverter;
import stratus.redis.cache.Cache;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.CatalogCache;
import stratus.redis.geoserver.CachingGeoServerFacade;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Batches redis queries for latter execution
 *
 * Usage:
 *
 * Register queries using the "get" methods.
 * Resolve queries using {@link #execute(boolean)} - this determines what queries depend upon the results of others, and
 * executes all queries in MULTI blocks based on these dependencies.
 */
@Slf4j
public class RedisMultiQueryCachingEngine {

    private Map<RedisQuery, RedisQuery> queries = new HashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConverter converter;


    /**
     * Construct a new RedisMultiQueryCachingEngine.
     *
     * Usage:
     *
     * Register queries using the "get" methods.
     * Resolve queries using {@link #execute(boolean)}
     * Instances should be discarded or {@link #clear()}ed after each call to {@link #execute(boolean)}
     *
     * @param redisTemplate
     * @param converter
     */
    public RedisMultiQueryCachingEngine(RedisTemplate<String, Object> redisTemplate,
                                 RedisConverter converter) {
        this.redisTemplate = redisTemplate;
        this.converter = converter;
    }

    /**
     * Utility method for converting a list of completed queries to a list of results
     *
     * @param queries List of queries
     * @param <T> Resolved objects of each of those queries.
     * @return
     */
    public static <T> List<T> resolveRedisQueries(List<RedisQuery<T>> queries) {
        List<T> results = new ArrayList<>();
        for (RedisQuery<T> query : queries) {
            results.add(query.get());
        }
        return results;
    }
    ////////////////////////// Catalog-like Queries ///////////////////////////

    /**
     * Get a Info object by id
     * @param id
     * @return Info object matching id
     */
    public <T extends Info> RedisValueQuery<T> get(RedisQueryKey id, CacheVisitor<?> visitor) {
        RedisValueQuery<T> query = new RedisValueQuery<>(id, visitor);
        return registerQuery(query);
    }
    /**
     * Get a list of Info objects by id
     * @param ids
     * @return CatalogInfo object matching id
     */
    public <T extends Info> RedisValuesQuery<T> getAll(RedisQueryKeysProvider<T> ids, CacheVisitor<?> visitor) {
        RedisValuesQuery<T> query = new RedisValuesQuery<>(ids, visitor);
        return registerQuery(query);
    }

    public RedisValueQuery<WorkspaceInfo> getWorkspaceByName(String name) {
        return get(getWorkspaceIdByName(name), new GetMethodCatalogCacheVisitor("getWorkspaceByName", Collections.singletonList(name)));
    }

    public RedisKeyQuery<WorkspaceInfo> getWorkspaceIdByName(String name) {
        //TODO: Can we just set a class for key; will everything in the chain have the same clazz?
        RedisKeyQuery<WorkspaceInfo> query = new RedisKeyQuery<>(new DefaultRedisQueryKey<>(WorkspaceInfo.class, "name", name));
        return registerQuery(query);
    }

    public RedisValueQuery<NamespaceInfo> getNamespaceByPrefix(String prefix) {
        return get(getNamespaceIdByPrefix(prefix), new GetMethodCatalogCacheVisitor("getNamespaceByPrefix", Collections.singletonList(prefix)));
    }

    public RedisKeyQuery<NamespaceInfo> getNamespaceIdByPrefix(String prefix) {
        RedisKeyQuery<NamespaceInfo> query = new RedisKeyQuery<>(new DefaultRedisQueryKey<>(NamespaceInfo.class, "prefix", prefix));
        return registerQuery(query);
    }

    public <T extends StoreInfo> RedisValueQuery<T> getStoreByName(RedisValueQuery workspace, String name, Class<T> clazz) {
        return get(getStoreIdByName(workspace.getKey(), name, clazz), new GetMethodCatalogCacheVisitor("getStoreByName", Arrays.asList(workspace, name, clazz)));
    }

    public <T extends StoreInfo> RedisKeyQuery<T> getStoreIdByName(RedisQueryKey workspaceId, String name, Class<T> clazz) {
        RedisKeyQuery<T> query = new RedisKeyQuery<>(Arrays.asList(
                new RedisQueryValueQueryKey<>(clazz, "workspaceId", workspaceId),
                new DefaultRedisQueryKey<>(clazz, "name", name)), clazz);
        return registerQuery(query);
    }

    public <K extends ResourceInfo, V extends StoreInfo> RedisValueQuery<V> getStoreByResource(RedisValueQuery<K> resource, Class<V> clazz) {
        return get(new RedisDelegatingIdQueryKey<>(resource, (query) -> {
            if (query.get() == null) {
                return null;
            }
            InvocationHandler h = Proxy.getInvocationHandler(query.get().getStore());
            if (h instanceof ResolvingProxy) {
                return ((ResolvingProxy) h).getRef();
            }
            return query.get().getStore().getId();
        }, clazz), new EmptyCatalogCacheVisitor());
    }

    public <T extends ResourceInfo> RedisValueQuery<T> getResourceByName(RedisValueQuery namespace, String name, Class<T> clazz) {
        return get(getResourceIdByName(namespace.getKey(), name, clazz), new GetMethodCatalogCacheVisitor("getResourceByName", Arrays.asList(namespace, name, clazz)));
    }

    public <T extends ResourceInfo> RedisKeyQuery<T> getResourceIdByName(RedisQueryKey namespaceId, String name, Class<T> clazz) {
        RedisKeyQuery<T> query = new RedisKeyQuery<>(Arrays.asList(
                new RedisQueryValueQueryKey<>(clazz, "namespaceId", namespaceId),
                new DefaultRedisQueryKey<>(clazz, "name", name)), clazz);
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValueQuery<LayerInfo> getLayerByName(RedisValueQuery<NamespaceInfo> namespace, String name) {
        return get(getLayerIdByName(namespace.getKey(), name), new GetMethodCatalogCacheVisitor("getLayerByName", Arrays.asList(
                new RedisValueQueryAccessor<NamespaceInfo>(namespace) {
                    public Object get() {
                        return (delegate.get()).getPrefix();
                    }
                },
                name)));
    }

    public RedisValueQuery<StyleInfo> getStyleByName(RedisValueQuery workspace, String name) {
        return get(getStyleIdByName(workspace.getKey(), name), new GetMethodCatalogCacheVisitor("getStyleByName", Arrays.asList(workspace, name)));
    }

    public RedisKeyQuery<StyleInfo> getStyleIdByName(RedisQueryKey workspaceId, String name) {
        RedisKeyQuery<StyleInfo> query = new RedisKeyQuery<>(Arrays.asList(
                new RedisQueryValueQueryKey<>(StyleInfo.class, "workspaceId", workspaceId),
                new DefaultRedisQueryKey<>(StyleInfo.class, "name", name)), StyleInfo.class);
        return registerQuery(query);
    }

    public RedisValueQuery<StyleInfo> getStyleByName(String name) {
        return get(getStyleIdByName(name), new GetMethodCatalogCacheVisitor("getStyleByName", Collections.singletonList(name)));
    }

    public RedisKeyQuery<StyleInfo> getStyleIdByName(String name) {
        RedisKeyQuery<StyleInfo> query = new RedisKeyQuery<>(new DefaultRedisQueryKey<>(StyleInfo.class, "name", name));
        return registerQuery(query);
    }

    public RedisKeyQuery<LayerInfo> getLayerIdByName(RedisQueryKey namespaceId, String name) {
        RedisKeyQuery<LayerInfo> query = new RedisKeyQuery<>(Arrays.asList(
                new RedisQueryValueQueryKey<>(LayerInfo.class, "namespaceId", namespaceId),
                new DefaultRedisQueryKey<>(LayerInfo.class, "name", name)), LayerInfo.class);
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValueQuery<T> getResourceByStore(RedisValueQuery store, String name, Class<T> clazz) {
        return get(getResourceIdByStore(store.getKey(), name, clazz), new GetMethodCatalogCacheVisitor("getResourceByStore", Arrays.asList(store, name, clazz)));
    }

    public <T extends ResourceInfo> RedisKeyQuery<T> getResourceIdByStore(RedisQueryKey storeId, String name, Class<T> clazz) {
        RedisKeyQuery<T> query = new RedisKeyQuery<>(Arrays.asList(
                new RedisQueryValueQueryKey<>(clazz, "storeId", storeId),
                new DefaultRedisQueryKey<>(clazz, "name", name)), clazz);
        return registerQuery(query);
    }

    public RedisValuesQuery<WorkspaceInfo> getWorkspaces() {
        return getAll(getWorkspaceIds(), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache) cache).setWorkspacesCached(true);
            }
        });
    }

    public RedisKeysQuery<WorkspaceInfo> getWorkspaceIds() {
        RedisKeysQuery<WorkspaceInfo> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(WorkspaceInfo.class));
        return registerQuery(query);
    }

    public RedisValuesQuery<NamespaceInfo> getNamespaces() {
        return getAll(getNamespaceIds(), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache) cache).setNamespacesCached(true);
            }
        });
    }

    public RedisKeysQuery<NamespaceInfo> getNamespaceIds() {
        RedisKeysQuery<NamespaceInfo> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(NamespaceInfo.class));
        return registerQuery(query);
    }

    public <T extends StoreInfo> RedisValuesQuery<T> getStores(Class<T> clazz) {
        return getAll(getStoreIds(clazz), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache) cache).setStoresCached(clazz, true);
            }
        });
    }

    public <T extends StoreInfo> RedisKeysQuery<T> getStoreIds(Class<T> clazz) {
        RedisKeysQuery<T> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(clazz));
        return registerQuery(query);
    }

    public <T extends StoreInfo> RedisValuesQuery<T> getStores(RedisValueQuery<? extends WorkspaceInfo> workspace, Class<T> clazz) {
        return getAll(getStoreIds(workspace.getKey(), clazz), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                if (workspace.get() != null) {
                    ((CatalogCache) cache).setStoresCached(workspace.get(), clazz, true);
                }
            }
        });
    }

    public <T extends StoreInfo> RedisKeysQuery<T> getStoreIds(RedisQueryKey workspaceId, Class<T> clazz) {
        RedisKeysQuery<T> query = new RedisKeysQuery<>(new RedisQueryValueQueryKey<>(clazz, "workspaceId", workspaceId));
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValuesQuery<T> getResources(Class<T> clazz) {
        return getAll(getResourceIds(clazz), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache)cache).setResourcesCached(clazz, true);
            }
        });
    }

    public <T extends ResourceInfo> RedisKeysQuery<T> getResourceIds(Class<T> clazz) {
        RedisKeysQuery<T> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(clazz));
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValuesQuery<T> getResourcesByStore(RedisValueQuery<? extends StoreInfo> store, Class<T> clazz) {
        return getAll(getResourceIdsByStore(store.getKey(), clazz), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                if (store.get() != null) {
                    ((CatalogCache) cache).setResourcesCached(store.get(), clazz, true);
                }
            }
        });
    }

    public <T extends ResourceInfo> RedisKeysQuery<T> getResourceIdsByStore(RedisQueryKey storeId, Class<T> clazz) {
        RedisKeysQuery<T> query = new RedisKeysQuery<>(new RedisQueryValueQueryKey<>(clazz, "storeId", storeId));
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValuesQuery<T> getResourcesByNamespace(RedisValueQuery<? extends NamespaceInfo> namespace, Class<T> clazz) {
        return getAll(getResourceIdsByNamespace(namespace.getKey(), clazz), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                if (namespace.get() != null) {
                    ((CatalogCache) cache).setResourcesCached(namespace.get(), clazz, true);
                }
            }
        });
    }

    public <T extends ResourceInfo> RedisKeysQuery<T> getResourceIdsByNamespace(RedisQueryKey namespaceId, Class<T> clazz) {
        RedisKeysQuery<T> query = new RedisKeysQuery<>(new RedisQueryValueQueryKey<>(clazz, "namespaceId", namespaceId));
        return registerQuery(query);
    }

    public RedisValuesQuery<LayerInfo> getLayers() {
        return getAll(getLayerIds(), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache) cache).setLayersCached(true);
            }
        });
    }

    public RedisKeysQuery<LayerInfo> getLayerIds() {
        RedisKeysQuery<LayerInfo> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(LayerInfo.class));
        return registerQuery(query);
    }

    public RedisValuesQuery<LayerInfo> getLayers(RedisValueQuery<? extends ResourceInfo> resource) {
        return getAll(getLayerIds(resource.getKey()), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                if (resource.get() != null) {
                    ((CatalogCache) cache).setLayersCached(resource.get(), true);
                }
            }
        });
    }

    public RedisKeysQuery<LayerInfo> getLayerIds(RedisQueryKey resourceId) {
        RedisKeysQuery<LayerInfo> query = new RedisKeysQuery<>(new RedisQueryValueQueryKey<>(LayerInfo.class, "resourceId", resourceId));
        return registerQuery(query);
    }

    public RedisValuesQuery<LayerGroupInfo> getLayerGroups() {
        return getAll(getLayerGroupIds(), (cache, value) -> {
            if (cache instanceof CatalogCache) {
                ((CatalogCache) cache).setLayerGroupsCached(true);
            }
        });
    }

    public RedisKeysQuery<LayerGroupInfo> getLayerGroupIds() {
        RedisKeysQuery<LayerGroupInfo> query = new RedisMembersQuery<>(new DefaultRedisMembersKey<>(LayerGroupInfo.class));
        return registerQuery(query);
    }

    public <T extends ResourceInfo> RedisValuesQuery getResourcesByLayers(RedisValuesQuery<LayerInfo> layers, Class<T> clazz) {
        return getAll(new RedisDelegatingQueryKeys<>(layers, (query) -> {
            Set<String> resourceIds = new HashSet<>();
            for (LayerInfo layer : query.get()) {
                ResourceInfo resource = layer.getResource();
                //TODO: This throws java.lang.IllegalArgumentException: not a proxy instance if layer is not a proxy instance.
                //(Not sure if this can ever actually happen)
                InvocationHandler h = Proxy.getInvocationHandler(resource);
                if (h instanceof ResolvingProxy) {
                    resourceIds.add(((ResolvingProxy) h).getRef());
                } else {
                    resourceIds.add(resource.getId());
                }
            }
            return new ArrayList<>(resourceIds);
        }, clazz), new EmptyCatalogCacheVisitor());
    }

    public RedisValuesQuery<LayerInfo> getLayersByLayerGroups(RedisValuesQuery<LayerGroupInfo> layerGroups) {
        return getAll(new RedisDelegatingQueryKeys<>(layerGroups, (query) -> {
            Set<String> layerIds = new HashSet<>();
            for (LayerGroupInfo layerGroup : query.get()) {
                for (PublishedInfo layer : layerGroup.getLayers()) {
                    if (layer instanceof LayerInfo) {
                        //TODO: This throws java.lang.IllegalArgumentException: not a proxy instance if layer is not a proxy instance.
                        //(Not sure if this can ever actually happen)
                        InvocationHandler h = Proxy.getInvocationHandler(layer);
                        if (h instanceof ResolvingProxy) {
                            layerIds.add(((ResolvingProxy) h).getRef());
                        } else {
                            layerIds.add(layer.getId());
                        }
                    }
                }
            }
            return new ArrayList<>(layerIds);
        }, LayerInfo.class), new EmptyCatalogCacheVisitor());
    }

    public <T extends ResourceInfo> RedisValuesQuery<LayerInfo> getLayersByResources(RedisValuesQuery<T> resources) {
        return getAll(getLayerIdsByResources(resources), new EmptyCatalogCacheVisitor());
    }

    public <T extends ResourceInfo> RedisKeysFromValuesQuery<LayerInfo> getLayerIdsByResources(RedisValuesQuery<T> resources) {
        RedisKeysFromValuesQuery<LayerInfo> layersQuery = new RedisKeysFromValuesQuery<>(new RedisDelegatingQueryKeys<>(resources, query -> {
            Set<String> resourceIds = new HashSet<>();
            for (T resource : query.get()) {
                resourceIds.add("resourceId:"+resource.getId());
            }
            return new ArrayList<>(resourceIds);
        }, LayerInfo.class), LayerInfo.class);
        return registerQuery(layersQuery);
    }

    public RedisValuesQuery<StyleInfo> getStyles() {
        return getAll(getStyleIds(), (CacheVisitor<CatalogCache>) (cache, value) -> cache.setStylesCached(true));
    }

    public RedisKeysQuery<StyleInfo> getStyleIds() {
        RedisKeysQuery<StyleInfo> query = new RedisKeysQuery<>(new DefaultRedisMembersKey<>(StyleInfo.class));
        return registerQuery(query);
    }

    public RedisValuesQuery<StyleInfo> getStylesByWorkspace(RedisValueQuery<WorkspaceInfo> workspace) {
        return getAll(getStyleIdsByWorkspace(workspace.getKey()), (CacheVisitor<CatalogCache>) (cache, value) ->
                cache.setStylesCached(workspace.get(), true));
    }

    public RedisKeysQuery<StyleInfo> getStyleIdsByWorkspace(RedisQueryKey<WorkspaceInfo> workspaceId) {
        RedisKeysQuery<StyleInfo> query = new RedisKeysQuery<>(new RedisQueryValueQueryKey<>(StyleInfo.class, "workspaceId", workspaceId));
        return registerQuery(query);
    }

    public RedisValuesQuery<StyleInfo> getStyles(RedisValuesQuery<LayerInfo> layers) {
        return getAll(new RedisDelegatingQueryKeys<>(layers, (query) -> {
            Set<String> styleIds = new HashSet<>();

            for (LayerInfo layer : query.get()) {
                List<StyleInfo> styles = new ArrayList<>(layer.getStyles());
                StyleInfo defaultStyle = layer.getDefaultStyle();
                if (defaultStyle != null) {
                    styles.add(layer.getDefaultStyle());
                }
                for (StyleInfo style : styles) {
                    InvocationHandler h = Proxy.getInvocationHandler(style);
                    if (h instanceof ResolvingProxy) {
                        styleIds.add(((ResolvingProxy) h).getRef());
                    } else {
                        styleIds.add(style.getId());
                    }
                }
            }
            return new ArrayList<>(styleIds);
        }, StyleInfo.class), new EmptyCatalogCacheVisitor());
    }

    public RedisValueQuery<DataStoreInfo> getDefaultStore(RedisValueQuery<WorkspaceInfo> workspace) {
        RedisIndexQuery<DataStoreInfo> index = new RedisIndexQuery<>(new RedisDelegatingValueQueryKey<>("default", workspace,
                (query) -> query.get() == null ? "null" : query.get().getId(), DataStoreInfo.class));
        return get(registerQuery(index), (CacheVisitor<CatalogCache>) (cache, value) -> {
            if (workspace.get() != null && value.get() != null) {
                cache.setDefaultDataStore(workspace.get(), (DataStoreInfo) value.get());
            }
        });
    }

    public RedisValueQuery<WorkspaceInfo> getDefaultWorkspace() {
        RedisIndexQuery<WorkspaceInfo> index = new RedisIndexQuery<>(new DefaultRedisQueryKey<>(WorkspaceInfo.class, "default"));
        return get(registerQuery(index), (CacheVisitor<CatalogCache>) (cache, value) -> cache.setDefaultWorkspace((WorkspaceInfo) value.get()));
    }

    public RedisValueQuery<NamespaceInfo> getDefaultNamespace() {
        RedisIndexQuery<NamespaceInfo> index = new RedisIndexQuery<>(new DefaultRedisQueryKey<>(NamespaceInfo.class, "default"));
        return get(registerQuery(index), (CacheVisitor<CatalogCache>) (cache, value) -> cache.setDefaultNamespace((NamespaceInfo) value.get()));
    }

    public void getDefaultStyles() {
        getStyleByName(StyleInfo.DEFAULT_GENERIC);
        getStyleByName(StyleInfo.DEFAULT_POINT);
        getStyleByName(StyleInfo.DEFAULT_LINE);
        getStyleByName(StyleInfo.DEFAULT_POLYGON);
        getStyleByName(StyleInfo.DEFAULT_RASTER);
    }

    public void getDefaultStyles(RedisValueQuery<WorkspaceInfo> workspace) {
        getStyleByName(workspace, StyleInfo.DEFAULT_GENERIC);
        getStyleByName(workspace, StyleInfo.DEFAULT_POINT);
        getStyleByName(workspace, StyleInfo.DEFAULT_LINE);
        getStyleByName(workspace, StyleInfo.DEFAULT_POLYGON);
        getStyleByName(workspace, StyleInfo.DEFAULT_RASTER);
    }

    public RedisQuery getGeoServerInfo() {
        // need to interact with geoserver cache (not catalog cache)
        RedisQueryKey<GeoServerInfo> key=new DefaultRedisQueryKey<>(GeoServerInfo.class,"GeoserverInfo");
        return registerQuery(new RedisValueQuery(key,  new GetMethodCatalogCacheVisitor("getGlobal", null)));
    }
    //TODO: Add more as applicable

    ////////////////////////// End Queries ////////////////////////////////////

    /**
     * Execute all queries
     *
     * @param useParallelQueries If true, execute groups of queries in parallel. Otherwise, use MULTI ... EXEC blocks.
     */
    public synchronized void execute(boolean useParallelQueries) {
        //Order queries
        int i = 0;
        List<Set<RedisQuery>> executionGroups = stage();
        for (Set<RedisQuery> executionGroup : executionGroups) {
            i++;
            List<RedisQuery> resolvedExecutionGroup = new ArrayList<>();
            for (RedisQuery query : executionGroup) {
                if (query!=null) {
                    //Split RedisValuesQueries into individual queries for execution
                    if (query instanceof RedisValuesQuery) {
                        resolvedExecutionGroup.addAll(((RedisValuesQuery<?>) query).getResolvedQueries());
                    } else if (query instanceof RedisKeysFromValuesQuery) {
                        resolvedExecutionGroup.addAll(((RedisKeysFromValuesQuery<?>) query).getResolvedQueries());
                    } else {
                        resolvedExecutionGroup.add(query);
                    }
                }
            }
            if (useParallelQueries) {
                if (log.isDebugEnabled()) {
                    log.debug("Executing parallel query group " + i + "/" +executionGroups.size());
                }
                executeParallel(resolvedExecutionGroup);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Executing multi query group " + i + "/" +executionGroups.size());
                }
                executeMulti(resolvedExecutionGroup);
            }
        }
    }

    /**
     * Loads the results of all completed queries into the provided {@link CachingCatalogFacade}
     * Only call after calling {@link #execute(boolean)}
     * @param cachingCatalogFacade
     * @param cachingGeoServerFacade
     * @return List of the catalog info objects loaded into the cache.
     */
    public synchronized List<Info> loadIntoCache(CachingCatalogFacade cachingCatalogFacade, CachingGeoServerFacade cachingGeoServerFacade) {
        return loadIntoCache(cachingCatalogFacade, cachingGeoServerFacade, false);
    }

    /**
     * Loads the results of all completed queries into the provided {@link CachingCatalogFacade}
     * Only call after calling {@link #execute(boolean)}
     *
     * @param cachingCatalogFacade
     * @param cachingGeoServerFacade
     * @param isComplete If true, set the cache to be the authoritative source for the catalog; the delegate will be
     *                   ignored. Also, all {@link CacheVisitor}s will be skipped, since there should no longer
     *                   be relevant. Otherwise, loads the cache as normal.
     * @return List of the catalog info objects loaded into the cache.
     */
    public synchronized List<Info> loadIntoCache(CachingCatalogFacade cachingCatalogFacade, CachingGeoServerFacade cachingGeoServerFacade, boolean isComplete) {
        Cache catalogCache=null;
        if (cachingGeoServerFacade !=null) {
            catalogCache=cachingCatalogFacade.getCache();
        }
        Cache geoServerCache=null;
        if (cachingGeoServerFacade !=null) {
            geoServerCache = cachingGeoServerFacade.getCache();
        }
        List<CatalogInfo> resolvedCatalogValues = new ArrayList<>();
        List<Info> resolvedGeoServerValues = new ArrayList<>();

        for (RedisQuery query : queries.values()) {
            if (query instanceof RedisValueQuery) {
                RedisValueQuery<?> valueQuery = (RedisValueQuery) query;

                if (!isComplete) {
                    if (catalogCache!=null && CatalogInfo.class.isAssignableFrom(valueQuery.getKey().getQueryClass())) {
                        valueQuery.getCacheVisitor().apply(catalogCache, valueQuery);
                    }
                    else if (geoServerCache !=null && !CatalogInfo.class.isAssignableFrom(valueQuery.getKey().getQueryClass())) {
                        valueQuery.getCacheVisitor().apply(geoServerCache, valueQuery);
                    }
                }

                if (valueQuery.get() != null && valueQuery.get() instanceof CatalogInfo) {
                    resolvedCatalogValues.add((CatalogInfo) valueQuery.get());
                } else if (valueQuery.get() != null && !(valueQuery.get() instanceof CatalogInfo)) {
                    resolvedGeoServerValues.add(valueQuery.get());
                }
            } else if (query instanceof RedisValuesQuery) {
                RedisValuesQuery<?> valuesQuery = (RedisValuesQuery) query;
                if (!isComplete) {
                    if (catalogCache!=null && CatalogInfo.class.isAssignableFrom(valuesQuery.getKey().getQueryClass())) {
                        valuesQuery.getCacheVisitor().apply(catalogCache, valuesQuery);
                        resolvedCatalogValues.addAll((Collection<? extends CatalogInfo>) valuesQuery.get());
                    }
                    else if (geoServerCache!=null && !CatalogInfo.class.isAssignableFrom(valuesQuery.getKey().getQueryClass())) {
                        valuesQuery.getCacheVisitor().apply(geoServerCache, valuesQuery);
                        resolvedGeoServerValues.addAll(valuesQuery.get());
                    }
                }
            }
        }
        List<Info> infos=new ArrayList<>();
        if (cachingCatalogFacade!=null) {
            cachingCatalogFacade.loadCache(resolvedCatalogValues, isComplete);
            infos.addAll(resolvedCatalogValues);
        }
        if (cachingGeoServerFacade!=null) {
            cachingGeoServerFacade.loadCache(resolvedGeoServerValues, isComplete);
            infos.addAll(resolvedGeoServerValues);
        }
        return infos;
    }

    /**
     * Clears all registered queries from this {@link RedisMultiQueryCachingEngine}.
     */
    public synchronized void clear() {
        queries = new HashMap<>();
    }

    /**
     * Execute a group of independent queries using a MULTI ... EXEC block
     */
    protected void executeMulti(Collection<RedisQuery> executionGroup) {


        //Don't even run the query if everything returns false.
        boolean anythingToExecute = false;
        List<RedisQuery> invalidQueries = new ArrayList<>();
        for (RedisQuery query : executionGroup) {
            //If any RedisKeyQueries returned null, don't include them
            if (query.canExecute()) {
                anythingToExecute = true;
            } else {
                //remove from execution group so parsing still works
                invalidQueries.add(query);
            }
        }
        executionGroup.removeAll(invalidQueries);
        List results = null;
        if (anythingToExecute) {
            if (log.isDebugEnabled()) {
                StringBuilder queryLog = new StringBuilder();
                queryLog.append("Executing redis query for cache preloading:\n");
                queryLog.append("    MULTI\n");
                try {
                    for (RedisQuery query : executionGroup) {
                        queryLog.append("    ").append(query.toStringInternal()).append("\n");
                    }
                } catch (Exception e) {
                    log.debug("Error while logging redis query", e);
                }
                queryLog.append("    EXEC\n");
                log.debug(queryLog.toString());
            }

            RedisCallback callback = (RedisCallback<List<Object>>) connection -> {
                connection.multi();

                for (RedisQuery query : executionGroup) {
                    if (query.canExecute()) {
                        query.execute(connection);
                    }
                }
                List<Object> resultsInternal;
                try {
                    resultsInternal = connection.exec();
                } catch (Exception e) {
                    throw new RuntimeException("Failure executing redis MULTI query during cache preloading", e);
                }
                return resultsInternal;
            };

            results = redisTemplate.execute(new SessionCallback<List<Object>>() {
                @Override
                public List<Object> execute(RedisOperations redisOperations) throws DataAccessException {
                    redisOperations.multi();
                    return (List) redisOperations.execute(callback);
                }
            });
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Skipping empty redis MULTI query during cache preloading");
            }
        }
        if (results == null) {
            results = new ArrayList();
        }
        if (executionGroup.size() != results.size()) {
            throw new IllegalStateException("Error fetching catalog objects. Results list ("+results.size()+") should be the same size as id list ("+executionGroup.size()+")!");
        }
        RedisQuery[] queryArray = executionGroup.toArray(new RedisQuery[0]);
        for (int i = 0; i < queryArray.length; i++) {
            if (queryArray[i].canExecute()) {
                queryArray[i].handleResult(results.get(i), converter);
            }
        }

    }

    /**
     * Execute a group of independent queries individually in parallel
     */
    protected void executeParallel(Collection<RedisQuery> executionGroup) {
        if (log.isDebugEnabled()) {
            StringBuilder queryLog = new StringBuilder();
            queryLog.append("Executing parallel redis queries for cache preloading:\n");
            try {
                for (RedisQuery query : executionGroup) {
                    //Use query.toStringInternal() to just get the query value
                    queryLog.append("    ").append(query.toStringInternal()).append("\n");
                }
            } catch (Exception e) {
                log.debug("Error while logging redis query", e);
            }
            log.debug(queryLog.toString());
        }
        executionGroup.parallelStream().forEach((query) -> {
            //If any RedisKeyQueries returned null, don't include them
            if (query.canExecute()) {
                try {
                    RedisCallback callback = query::execute;
                    Object result = redisTemplate.execute(callback);
                    query.handleResult(result, converter);
                } catch (Exception e) {
                    throw new RuntimeException("Failure executing parallel redis queries during cache preloading", e);
                }
            }
        });
    }


    private List<Set<RedisQuery>> stage() {
        List<Set<RedisQuery>> executionGroups = new ArrayList<>();
        for (RedisQuery query : queries.values()) {
            int depth = getNestedQueries(query);
            if (depth < 0) {
                continue;
            }
            while (executionGroups.size() <= depth) {
                executionGroups.add(null);
            }
            Set<RedisQuery> executionGroup = executionGroups.get(depth);
            if (executionGroup == null) {
                executionGroup = new HashSet<>();
                executionGroups.set(depth, executionGroup);
            }
            executionGroup.add(query);
        }
        return executionGroups;
    }
    private <T extends RedisQuery> T registerQuery(T value) {
        return registerQuery(queries, value);
    }
    private <T extends RedisQuery> T registerQuery(Map<RedisQuery, RedisQuery> queries, T value) {
        if (queries.containsKey(value)) {
            //Avoid registering duplicate queries
            return (T) queries.get(value);
        } else {
            queries.put(value, value);
            return value;
        }
    }

    private int getNestedQueries(RedisQuery<?> query) {
        if (query.isDone()) {
            //Query has already been executed, should not be counted in the execution hierarchy.
            //This also negates recursive calls for nested queries that are already complete, resulting in the correct
            //level of 0.
            return -1;
        }
        int level = 0;
        for (RedisQueryKeyLike key : query.getKeys()) {
            //If this key is a query for a key, need to executeMulti the query before resolving the key
            if (key instanceof RedisQueryContainer) {
                level = Math.max(level, getNestedQueries(((RedisQueryContainer) key).getQuery()) + 1);
            //If this key contains a query for a key, need to executeMulti the query before resolving the key
            } else if (key instanceof RedisQueryValueQueryKey) {
                if (((RedisQueryValueQueryKey) key).getQuery() instanceof RedisQueryContainer) {
                    level = Math.max(level, getNestedQueries(((RedisQueryContainer) ((RedisQueryValueQueryKey) key).getQuery()).getQuery()) + 1);
                }
            }
        }
        return level;
    }


    /**
     * Implementation of {@link CacheVisitor} which does nothing
     */
    public static class EmptyCatalogCacheVisitor implements CacheVisitor<Cache> {
        @Override
        public void apply(Cache cache, RedisQuery<?> value) { }
    }

    /**
     * Implementation of {@link CacheVisitor} which, in the cases where a query returns null, registers this
     * failure with the cache, to avoid future lookups.
     */
    public static class GetMethodCatalogCacheVisitor implements CacheVisitor<Cache> {

        String methodName;
        List<Object> methodArgs;

        public GetMethodCatalogCacheVisitor(String methodName, List<Object> methodArgs) {
            this.methodName = methodName;
            this.methodArgs = methodArgs;
        }
        @Override
        public void apply(Cache cache, RedisQuery<?> valueQuery) {
            if (valueQuery.get() == null) {
                //If the value query didn't return, mark the failure as cached.
                if (methodName != null && methodArgs != null) {
                    //Resolve any RedisQuery's in the argument list
                    for (int i = 0; i < methodArgs.size(); i++) {
                        if (methodArgs.get(i) instanceof RedisQuery) {
                            methodArgs.set(i, ((RedisQuery) methodArgs.get(i)).get());
                        }
                        if (methodArgs.get(i) instanceof RedisValueQueryAccessor) {
                            methodArgs.set(i, ((RedisValueQueryAccessor) methodArgs.get(i)).get());
                        }
                    }
                    cache.setCached(methodName, methodArgs, true);
                }
            }
        }
    }

    /**
     * Utility class for accessing parameters of a {@link RedisValueQuery}s value.
     * TODO: This is very similar to {@link DelegatingKey}. Combine them?
     * @param <T>
     */
    protected abstract static class RedisValueQueryAccessor<T extends CatalogInfo> {

        RedisValueQuery<T> delegate;

        public RedisValueQueryAccessor(RedisValueQuery<T> delegate) {
            this.delegate = delegate;
        }

        protected abstract Object get();
    }
}
