/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.*;
import org.geoserver.catalog.util.CloseableIterator;
import org.geoserver.catalog.util.CloseableIteratorAdapter;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.ows.Dispatcher;
import org.geoserver.ows.Request;
import org.geotools.filter.AndImpl;
import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.util.Utilities;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.stereotype.Service;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.catalog.impl.CatalogInfoConvert;
import stratus.redis.catalog.info.LayerInfoRedisImpl;
import stratus.redis.catalog.info.NamespaceInfoRedisImpl;
import stratus.redis.catalog.info.WorkspaceInfoRedisImpl;
import stratus.redis.catalog.repository.*;
import stratus.redis.repository.RedisRepository;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;


/**
 * CatalogFacade backed by a {@link RedisRepository}
 * <p>
 * The various <code><a href="{@docRoot}/stratus/redis/repository/package-summary.html">repository</a></code>
 * interfaces are used to query redis, based on the indices and hashes described in the
 * <code><a href="{@docRoot}/stratus/redis/cataloginfo/package-summary.html">cataloginfo</a></code>
 * implementations.
 * <p>
 * All calls that return a list of results utilize {@link CachingCatalogFacade} to cache known CatalogInfo
 * objects for more efficient calls to {@link #resolve()}
 *
 * @author tingold
 * @author Niels Charlier
 * @author Josh Fix
 * @author tbarsballe
 */
@Slf4j
@Primary
@Service("redisCatalogFacade")
public class RedisCatalogFacade extends AbstractCatalogFacade {

    public static final String STR_DEFAULT = ":default";
    public static final String STR_DEFAULT_DATA_STORE = "defaultDataStore";
    public static final String STR_DEFAULT_NAMESPACE = "defaultNamespace";
    public static final String STR_DEFAULT_WORKSPACE = "defaultWorkspace";

    private final RedisRepository repository;
    private final XStreamPersister xStream = new XStreamPersisterFactory().createXMLPersister();
    private Catalog catalog;
    private Map<Class<? extends CatalogInfo>, CatalogInfoCrudRepository<?>> repositories = new HashMap<>();

    //Ensure this gets instantiated before the catalog facade
    @Autowired
    RedisCustomConversions customConversions;

    public RedisCatalogFacade(RedisRepository repository, Catalog catalog,
                              WorkspaceRepository workspaceRepository, StoreRepository storeRepository,
                              DataStoreRepository dataStoreRepository, CoverageStoreRepository coverageStoreRepository,
                              WMSStoreRepository wmsStoreRepository, CoverageResourceRepository coverageResourceRepository,
                              FeatureTypeResourceRepository featureTypeResourceRepository, WMSLayerResourceRepository wmsLayerResourceRepository,
                              NamespaceRepository namespaceRepository, LayerRepository layerRepository, StyleRepository styleRepository,
                              LayerGroupRepository layerGroupRepository, MapRepository mapRepository,
                              WMTSLayerResourceRepository wmtsLayerRepository, WMTSStoreRepository wmtsStoreRepository) {
        log.debug("redisCatalogFacade constructed");
        this.repository = repository;
        this.catalog = catalog;
        this.xStream.setCatalog(catalog);

        repositories.put(WorkspaceInfo.class, workspaceRepository);
        repositories.put(DataStoreInfo.class, dataStoreRepository);
        repositories.put(CoverageStoreInfo.class, coverageStoreRepository);
        repositories.put(WMSStoreInfo.class, wmsStoreRepository);
        repositories.put(CoverageInfo.class, coverageResourceRepository);
        repositories.put(FeatureTypeInfo.class, featureTypeResourceRepository);
        repositories.put(WMSLayerInfo.class, wmsLayerResourceRepository);
        repositories.put(NamespaceInfo.class, namespaceRepository);
        repositories.put(LayerInfo.class, layerRepository);
        repositories.put(StyleInfo.class, styleRepository);
        repositories.put(LayerGroupInfo.class, layerGroupRepository);
        repositories.put(MapInfo.class, mapRepository);
        repositories.put(WMTSLayerInfo.class, wmtsLayerRepository);
        repositories.put(WMTSStoreInfo.class, wmtsStoreRepository);
        repositories.put(StoreInfo.class, storeRepository);
    }

    @Override
    public void setCatalog(Catalog catalog) {
        if (catalog instanceof AbstractCatalogDecorator) {
            catalog = ((AbstractCatalogDecorator) catalog).unwrap(CatalogImpl.class);
        }
        Preconditions.checkArgument(catalog instanceof CatalogImpl);
        //Preconditions.checkArgument(((CatalogImpl)catalog).getFacade() == this);
        this.catalog = catalog;
        this.xStream.setCatalog(catalog);
    }

    @Override
    public Catalog getCatalog() {
        return catalog;
    }

    /****************************************************************/

    private <T extends CatalogInfo> void resolve(T info) {
        resolve(info, null);
    }

    private <T extends CatalogInfo> void resolve(T info, Catalog catalog) {
        ((CatalogImpl) this.catalog).resolve(info);
        if (info instanceof LayerInfo) {
            resolve((LayerInfo) info, catalog);
        } else if (info instanceof ResourceInfo) {
            resolve((ResourceInfo) info, catalog);
        } else if (info instanceof LayerGroupInfo) {
            resolve((LayerGroupInfo) info, catalog);
        } else if (info instanceof StyleInfo) {
            resolve((StyleInfo) info, catalog);
        } else if (info instanceof MapInfo) {
            resolve((MapInfo) info, catalog);
        } else if (info instanceof WorkspaceInfo) {
            resolve((WorkspaceInfo) info, catalog);
        } else if (info instanceof NamespaceInfo) {
            resolve((NamespaceInfo) info, catalog);
        } else if (info instanceof StoreInfo) {
            resolve((StoreInfo) info, catalog);
        }
    }

    @SuppressWarnings("unchecked")
    private final <T extends CatalogInfo, S extends T> CatalogInfoCrudRepository<S> repository(Class<T> clazz) {
        return (CatalogInfoCrudRepository<S>) repositories.get(CatalogInfoConvert.root(clazz));
    }

    private final <T extends CatalogInfo, S extends T> T addInfo(T info, Class<T> clazz) {
        checkChangeCanBeReadTransaction(info, clazz, (redisInfo, classRepo)->{
            try {
                return classRepo.save((S) redisInfo);
            } catch (Exception e) {
                log.error("Error adding info. ", e);
                throw new RuntimeException("Error adding info.", e);
            }
        });
        
        return ModificationProxy.create(info, clazz);
    }
    
    /**
     * Converts the given info to a Redis implementation and finds the appropriate repository. 
     * Passes them to the given handler to store it in the repo. Then attempts to read the info from
     * the repository to confirm it was not corrupted.  If this fails, it attempts to roll back the
     * change.
     * @param info
     * @param clazz
     * @param handler
     * @return
     */
    private final <T extends CatalogInfo, S extends T> T checkChangeCanBeReadTransaction(T info, Class<? extends T> clazz, BiFunction<S, CatalogInfoCrudRepository<S>, S> handler) {
        S redisInfo = CatalogInfoConvert.toRedis(info);
        @SuppressWarnings("unchecked")
        CatalogInfoCrudRepository<S> classRepo = repository((Class<S>) info.getClass());
        // Remember what was there before
        final S oldInfo;
        if(Objects.nonNull(info.getId())) {
            oldInfo = classRepo.findById(info.getId()).orElse(null);
        } else {
            oldInfo = null;
        }
        
        redisInfo = handler.apply(redisInfo, classRepo);
        
        // Check that we can read it back
        try {
            classRepo.findById(redisInfo.getId());
        } catch (Exception e) {
            // Roll back the change
            log.error("Error reading saved info, attempting to roll back to previous state", e);
            try {
                if(Objects.nonNull(oldInfo)) {
                    log.info("Restoring "+oldInfo.getId()+" to "+classRepo);
                    classRepo.save(oldInfo);
                } else {
                    log.info("Deleting "+redisInfo.getId());
                    // Because classRepo.delete returns the deleted object, it will fail due to the 
                    // same error, so delete via lower level redis commands. 
                    String indexKey = RedisCatalogUtils.buildKey(CatalogInfoConvert.root(info.getClass()), redisInfo.getId(), "idx");
                    String primaryKey = RedisCatalogUtils.buildKey(CatalogInfoConvert.root(info.getClass()), redisInfo.getId(), null);
                    String rootKey = RedisCatalogUtils.buildKey(CatalogInfoConvert.root(info.getClass()), null, null);
                    
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    Set<String> keys = (Set)repository.getRedisSetRepository().getSetMembers(indexKey);
                    for (String key: keys) {
                        log.debug("Deleting index entry "+key);
                        repository.deleteKey(key);
                    }
                    log.debug("Deleting index list "+indexKey);
                    repository.deleteKey(indexKey);
                    log.debug("Deleting id "+redisInfo.getId()+" from type root list "+rootKey);
                    repository.getRedisSetRepository().removeFromSet(rootKey, redisInfo.getId());
                    log.debug("Deleting primary entry "+primaryKey);
                    repository.deleteKey(primaryKey);
                }
            } catch (Exception e2) {
                log.error("Failed to roll back to previous state, catalog may have been corrupted", e2);
                e2.addSuppressed(e);
                e=e2;
            }
            throw new RuntimeException("Error reading saved info", e);
        }
        
        return redisInfo;
    }
    
    private final <T extends CatalogInfo, S extends CatalogInfo> void removeInfo(T info) {
        CatalogInfoCrudRepository<S> repository = (CatalogInfoCrudRepository<S>) repository(info.getClass());

        repository.findById(info.getId()).ifPresent(repository::delete);
    }

    private final <T extends CatalogInfo, S extends T> void saveInfo(T info) {
        ModificationProxy h = (ModificationProxy) Proxy.getInvocationHandler(info);
        // fire out what changed
        List<String> propertyNames = h.getPropertyNames();
        List<Object> newValues = h.getNewValues();
        List<Object> oldValues = h.getOldValues();

        beforeSaved(info, propertyNames, oldValues, newValues);
        info = commitProxy(info);
        resolve(info);
        saveRedisInfo(info);
        afterSaved(info, propertyNames, oldValues, newValues);
    }

    @SuppressWarnings("unchecked")
    private final <T extends CatalogInfo, S extends T> void saveRedisInfo(T info) {
        checkChangeCanBeReadTransaction(info, (Class<? extends T>)info.getClass(), (redisInfo, classRepo)->{
            return classRepo.save(redisInfo);
        });
    }

    protected <T> T resolveProxy(Catalog catalog, T info) {
        T resolvedInfo;
        if (Dispatcher.REQUEST.get() != null) {
            Request req = Dispatcher.REQUEST.get();
            Dispatcher.REQUEST.set(null);
            resolvedInfo = ResolvingProxy.resolve(catalog, info);
            Dispatcher.REQUEST.set(req);
        } else {
            resolvedInfo = ResolvingProxy.resolve(catalog, info);
        }
        return resolvedInfo;
    }

    private final <T extends CatalogInfo> T resolveAndProxy(T info, Class<T> clazz) {
        return resolveAndProxy(info, clazz, null);
    }

    private final <T extends CatalogInfo> List<T> resolveAndProxy(Iterable<? extends T> infos, Class<T> clazz) {
        return resolveAndProxy(infos, clazz, null);
    }

    private final <T extends CatalogInfo> T resolveAndProxy(T info, Class<T> clazz, Catalog catalog) {
        if (info == null) {
            return null;
        }
        info = CatalogInfoConvert.toTraditional(info);
        resolve(info, catalog);
        return ModificationProxy.create(info, clazz);
    }

    private final <T extends CatalogInfo> List<T> resolveAndProxy(Iterable<? extends T> infos, Class<T> clazz, Catalog catalog) {
        if (infos == null) {
            return null;
        }
        List<T> collection = new ArrayList<>();
        for (T info : infos) {
            if (info == null) {
                return null;
            }
            info = CatalogInfoConvert.toTraditional(info);
            resolve(info, catalog);
            collection.add(info);
        }
        return ModificationProxy.createList(collection, clazz);
    }

    private <T extends CatalogInfo> T get(String id, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            return (T) getResource(id, ResourceInfo.class);
        }
        return resolveAndProxy(repository(clazz).findById(id).orElse(null), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> T getByName(String name, Class<T> clazz) {
        return resolveAndProxy(((CatalogInfoByName<T>) repository(clazz)).findByName(name), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> List<T> getListByName(String name, Class<T> clazz) {
        return resolveAndProxy(((CatalogInfoByName<T>) repository(clazz)).findListByName(name), clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> List<T> getByWorkspace(WorkspaceInfo workspace, Class<T> clazz) {
        if (workspace == null) {
            workspace = getDefaultWorkspace();
        }
        if (workspace == ANY_WORKSPACE || workspace == null) {
            return getAll(clazz);
        } else if (workspace == NO_WORKSPACE) {
            return resolveAndProxy(((CatalogInfoByWorkspace<T>) repository(clazz)).findListByWorkspaceId(WorkspaceInfoRedisImpl.NO_WORKSPACE_ID), clazz);
        } else {
            return resolveAndProxy(((CatalogInfoByWorkspace<T>) repository(clazz)).findListByWorkspaceId(workspace.getId()),
                    clazz, catalog);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> T getByWorkspaceAndName(WorkspaceInfo workspace, String name, Class<T> clazz) {
        if (workspace == null) {
            workspace = getDefaultWorkspace();
        }
        if (workspace == ANY_WORKSPACE || workspace == null) {
            return resolveAndProxy(((CatalogInfoByWorkspace<T>) repository(clazz)).findByName(name), clazz);
        } else if (workspace == NO_WORKSPACE) {
            return resolveAndProxy(((CatalogInfoByWorkspace<T>) repository(clazz)).findByWorkspaceIdAndName(WorkspaceInfoRedisImpl.NO_WORKSPACE_ID, name), clazz);
        } else {
            return resolveAndProxy(((CatalogInfoByWorkspace<T>) repository(clazz)).findByWorkspaceIdAndName(workspace.getId(), name),
                    clazz, catalog);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> List<T> getByNamespace(NamespaceInfo namespace, Class<T> clazz) {
        if (namespace == null) {
            namespace = getDefaultNamespace();
        }
        if (namespace == ANY_NAMESPACE || namespace == null) {
            return getAll(clazz);
        } else {
            return resolveAndProxy(((CatalogInfoByNamespace<T>) repository(clazz)).findListByNamespaceId(namespace.getId()),
                    clazz, catalog);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> T getByNamespaceAndName(NamespaceInfo namespace, String name, Class<T> clazz) {
        if (namespace == null) {
            namespace = getDefaultNamespace();
        }
        if (namespace == ANY_NAMESPACE || namespace == null) {
            return resolveAndProxy(((CatalogInfoByNamespace<T>) repository(clazz)).findByName(name), clazz);
        } else {
            return resolveAndProxy(((CatalogInfoByNamespace<T>) repository(clazz)).findByNamespaceIdAndName(namespace.getId(), name),
                    clazz, catalog);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> T getByStoreAndName(StoreInfo store, String name, Class<T> clazz) {
        return resolveAndProxy(((CatalogInfoByStore<T>) repository(clazz)).findByStoreIdAndName(store.getId(), name),
                clazz, catalog);
    }

    @SuppressWarnings("unchecked")
    public <T extends CatalogInfo> List<T> getByStore(StoreInfo store, Class<T> clazz) {
        return resolveAndProxy(((CatalogInfoByStore<T>) repository(clazz)).findListByStoreId(store.getId()), clazz,
                catalog);
    }

    private <T extends CatalogInfo> List<T> getAll(Class<T> clazz) {
        return resolveAndProxy(repository(clazz).findAll(), clazz);
    }

    private <T extends CatalogInfo> T getDefault(Class<T> clazz, CatalogInfo parent) {
        StringBuilder key = new StringBuilder().append(clazz.getSimpleName())
                .append(STR_DEFAULT);
        if (parent != null) {
            key = key.append(":").append(parent.getId());
        }
        String id = repository.getRedisValueRepository().getValue(key.toString());
        if (null == id) {
            return null;
        }
        return get(id, clazz);
    }

    private <T extends CatalogInfo> T getDefault(Class<T> clazz) {
        return getDefault(clazz, null);
    }

    private <T extends CatalogInfo> void setDefault(Class<T> clazz, String strMessage, T newDefault, CatalogInfo parent) {
        T old = getDefault(clazz, parent);
        StringBuilder key = new StringBuilder().append(clazz.getSimpleName())
                .append(STR_DEFAULT);
        if (parent != null) {
            key = key.append(":").append(parent.getId());
        }

        if (!Utilities.equals(old, newDefault)) {
            // fire change event
            catalog.fireModified(catalog, Collections.singletonList(strMessage), Collections.singletonList(old),
                    Collections.singletonList(newDefault));
        }

        if (newDefault == null) {
            repository.deleteKey(key.toString());
        } else {
            repository.getRedisValueRepository().setValue(key.toString(), newDefault.getId());
        }

        if (!Utilities.equals(old, newDefault)) {
            // fire change event
            catalog.firePostModified(catalog, Collections.singletonList(strMessage), Collections.singletonList(old),
                    Collections.singletonList(newDefault));
        }
    }

    private <T extends CatalogInfo> void setDefault(Class<T> clazz, String strMessage, T newDefault) {
        setDefault(clazz, strMessage, newDefault, null);
    }

    /***************************************************************************************/

    @Override
    public StoreInfo add(StoreInfo store) {
        store = unwrap(store);
        resolve(store);
        return addInfo(store, StoreInfo.class);
    }

    @Override
    public void remove(StoreInfo store) {
        removeInfo(store);
    }

    @Override
    public void save(StoreInfo store) {
        saveInfo(store);
    }

    @Override
    public <T extends StoreInfo> T detach(T store) {
        return store;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoreInfo> T getStore(String id, Class<T> clazz) {
        if (clazz.equals(StoreInfo.class)) {
            DataStoreInfo dataStore = get(id, DataStoreInfo.class);
            if (null != dataStore) {
                return (T) dataStore;
            }
            CoverageStoreInfo coverageStore = get(id, CoverageStoreInfo.class);
            if (null != coverageStore) {
                return (T) coverageStore;
            }
            WMSStoreInfo wmsStore = get(id, WMSStoreInfo.class);
            if (null != wmsStore) {
                return (T) wmsStore;
            }
            WMTSStoreInfo wmtsStore = get(id, WMTSStoreInfo.class);
            if (null != wmtsStore) {
                return (T) wmtsStore;
            }
            return null;
        }
        return get(id, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoreInfo> T getStoreByName(WorkspaceInfo workspace, String name, Class<T> clazz) {
        if (clazz.equals(StoreInfo.class)) {
            DataStoreInfo dataStore = getByWorkspaceAndName(workspace, name, DataStoreInfo.class);
            if (null != dataStore) {
                return (T) dataStore;
            }
            CoverageStoreInfo coverageStore = getByWorkspaceAndName(workspace, name, CoverageStoreInfo.class);
            if (null != coverageStore) {
                return (T) coverageStore;
            }
            WMSStoreInfo wmsStore = getByWorkspaceAndName(workspace, name, WMSStoreInfo.class);
            if (null != wmsStore) {
                return (T) wmsStore;
            }
            WMTSStoreInfo wmtsStore = getByWorkspaceAndName(workspace, name, WMTSStoreInfo.class);
            if (null != wmtsStore) {
                return (T) wmtsStore;
            }
            return null;
        }
        return getByWorkspaceAndName(workspace, name, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoreInfo> List<T> getStoresByWorkspace(WorkspaceInfo workspace, Class<T> clazz) {
        if (clazz.equals(StoreInfo.class)) {
            List<T> list = new ArrayList<>();
            list.addAll((Collection<? extends T>) getByWorkspace(workspace, DataStoreInfo.class));
            list.addAll((Collection<? extends T>) getByWorkspace(workspace, CoverageStoreInfo.class));
            list.addAll((Collection<? extends T>) getByWorkspace(workspace, WMSStoreInfo.class));
            list.addAll((Collection<? extends T>) getByWorkspace(workspace, WMTSStoreInfo.class));
            return Collections.unmodifiableList(list);
        }
        return getByWorkspace(workspace, clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends StoreInfo> List<T> getStores(Class<T> clazz) {
        if (clazz.equals(StoreInfo.class)) {
            List<StoreInfo> stores = new ArrayList<>();
            stores.addAll(getAll(DataStoreInfo.class));
            stores.addAll(getAll(CoverageStoreInfo.class));
            stores.addAll(getAll(WMSStoreInfo.class));
            stores.addAll(getAll(WMTSStoreInfo.class));
            return Collections.unmodifiableList((List<T>) stores);
        }
        return getAll(clazz);
    }

    @Override
    public DataStoreInfo getDefaultDataStore(WorkspaceInfo workspace) {
        return getDefault(DataStoreInfo.class, workspace);
    }

    @Override
    public void setDefaultDataStore(WorkspaceInfo workspace, DataStoreInfo store) {
        setDefault(DataStoreInfo.class, STR_DEFAULT_DATA_STORE, store, workspace);
    }

    /****************************************************************/

    @Override
    public ResourceInfo add(ResourceInfo resource) {
        resource = unwrap(resource);
        resolve(resource);
        return addInfo(resource, ResourceInfo.class);
    }

    @Override
    public void remove(ResourceInfo resource) {
        removeInfo(resource);
    }

    @Override
    public void save(ResourceInfo resource) {
        saveInfo(resource);
    }

    @Override
    public <T extends ResourceInfo> T detach(T resource) {
        return resource;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> T getResource(String id, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            CoverageInfo coverageResource = get(id, CoverageInfo.class);
            if (null != coverageResource) {
                return (T) coverageResource;
            }
            FeatureTypeInfo featureTypeResource = get(id, FeatureTypeInfo.class);
            if (null != featureTypeResource) {
                return (T) featureTypeResource;
            }
            WMSLayerInfo wmsLayerResource = get(id, WMSLayerInfo.class);
            if (null != wmsLayerResource) {
                return (T) wmsLayerResource;
            }
            WMTSLayerInfo wmtsLayerResource = get(id, WMTSLayerInfo.class);
            if (null != wmtsLayerResource) {
                return (T) wmtsLayerResource;
            }
            return null;
        }
        return get(id, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> T getResourceByName(NamespaceInfo namespace, String name, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            CoverageInfo coverageResource = getByNamespaceAndName(namespace, name, CoverageInfo.class);
            if (null != coverageResource) {
                return (T) coverageResource;
            }
            FeatureTypeInfo featureTypeResource = getByNamespaceAndName(namespace, name, FeatureTypeInfo.class);
            if (null != featureTypeResource) {
                return (T) featureTypeResource;
            }
            WMSLayerInfo wmsLayerResource = getByNamespaceAndName(namespace, name, WMSLayerInfo.class);
            if (null != wmsLayerResource) {
                return (T) wmsLayerResource;
            }
            WMTSLayerInfo wmtsLayerResource = getByNamespaceAndName(namespace, name, WMTSLayerInfo.class);
            if (null != wmtsLayerResource) {
                return (T) wmtsLayerResource;
            }
            return null;
        }
        return getByNamespaceAndName(namespace, name, clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends ResourceInfo> List<T> getResourcesByName(String name, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            List<ResourceInfo> resources = new ArrayList<>();
            resources.addAll(getListByName(name, CoverageInfo.class));
            resources.addAll(getListByName(name, FeatureTypeInfo.class));
            resources.addAll(getListByName(name, WMSLayerInfo.class));
            resources.addAll(getListByName(name, WMTSLayerInfo.class));
            return Collections.unmodifiableList((List<T>) resources);
        }
        return getListByName(name, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> List<T> getResources(Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            List<ResourceInfo> resources = new ArrayList<>();
            resources.addAll(getAll(CoverageInfo.class));
            resources.addAll(getAll(FeatureTypeInfo.class));
            resources.addAll(getAll(WMSLayerInfo.class));
            resources.addAll(getAll(WMTSLayerInfo.class));
            return Collections.unmodifiableList((List<T>) resources);
        }
        return getAll(clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> List<T> getResourcesByNamespace(NamespaceInfo namespace, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            List<T> list = new ArrayList<>();
            list.addAll((Collection<? extends T>) getByNamespace(namespace, FeatureTypeInfo.class));
            list.addAll((Collection<? extends T>) getByNamespace(namespace, CoverageInfo.class));
            list.addAll((Collection<? extends T>) getByNamespace(namespace, WMSLayerInfo.class));
            list.addAll((Collection<? extends T>) getByNamespace(namespace, WMTSLayerInfo.class));
            return Collections.unmodifiableList(list);
        }
        return getByNamespace(namespace, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> T getResourceByStore(StoreInfo store, String name, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            if (store instanceof CoverageStoreInfo) {
                CoverageInfo coverageResource = getByStoreAndName(store, name, CoverageInfo.class);
                if (null != coverageResource) {
                    return (T) coverageResource;
                }
            }
            if (store instanceof DataStoreInfo) {
                FeatureTypeInfo featureTypeResource = getByStoreAndName(store, name, FeatureTypeInfo.class);
                if (null != featureTypeResource) {
                    return (T) featureTypeResource;
                }
            }
            if (store instanceof WMSStoreInfo) {
                WMSLayerInfo wmsLayerResource = getByStoreAndName(store, name, WMSLayerInfo.class);
                if (null != wmsLayerResource) {
                    return (T) wmsLayerResource;
                }
            }
            if (store instanceof WMTSStoreInfo) {
                WMTSLayerInfo wmtsLayerResource = getByStoreAndName(store, name, WMTSLayerInfo.class);
                if (null != wmtsLayerResource) {
                    return (T) wmtsLayerResource;
                }
            }
            return null;
        }
        return getByStoreAndName(store, name, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResourceInfo> List<T> getResourcesByStore(StoreInfo store, Class<T> clazz) {
        if (clazz.equals(ResourceInfo.class)) {
            List<ResourceInfo> resources = new ArrayList<>();
            resources.addAll(getByStore(store, FeatureTypeInfo.class));
            resources.addAll(getByStore(store, CoverageInfo.class));
            resources.addAll(getByStore(store, WMSLayerInfo.class));
            resources.addAll(getByStore(store, WMTSLayerInfo.class));
            return Collections.unmodifiableList((List<T>) resources);
        }
        return getByStore(store, clazz);
    }

    /****************************************************************/

    @Override
    public LayerInfo add(LayerInfo layer) {
        layer = unwrap(layer);
        resolve(layer);
        log.debug("Adding LayerInfo. CLass: " + layer.getClass().getSimpleName() + " Path: " + layer.getPath() + " Name: " + layer.getName() + " ID: " + layer.getId());
        log.debug("ResourceInfo for layer.  Class: " + layer.getResource().getClass().getSimpleName() + " Name: " + layer.getResource().getName() + " Title: " + layer.getResource().getTitle() + " ID: " + layer.getResource().getTitle());
        return addInfo(layer, LayerInfo.class);
    }

    @Override
    public void remove(LayerInfo layer) {
        removeInfo(layer);
    }

    @Override
    public void save(LayerInfo layer) {
        saveInfo(layer);
    }

    @Override
    public LayerInfo detach(LayerInfo layer) {
        return layer;
    }

    @Override
    public LayerInfo getLayer(String id) {
        return get(id, LayerInfo.class);
    }

    @Override
    public LayerInfo getLayerByName(String name) {
        ResourceInfo res = getResourceByName(getDefaultNamespace(), name, ResourceInfo.class);
        if (res == null) {
            List<ResourceInfo> list = getResourcesByName(name, ResourceInfo.class);
            if (list.size() == 1) {
                res = list.get(0);
            } else {
                return null;
            }
        }
        return resolveAndProxy(((LayerRepository) repository(LayerInfoRedisImpl.class)).findByResourceId(res.getId()), LayerInfo.class);
    }

    @Override
    public List<LayerInfo> getLayers(ResourceInfo resource) {
        return resolveAndProxy(((LayerRepository) repository(LayerInfoRedisImpl.class)).findListByResourceId(resource.getId()), LayerInfo.class);
    }

    @Override
    public List<LayerInfo> getLayers(StyleInfo style) {
        return resolveAndProxy(((LayerRepository) repository(LayerInfoRedisImpl.class)).findListByStyleIdsOrDefaultStyleId(style.getId(), style.getId()), LayerInfo.class);
    }

    @Override
    public List<LayerInfo> getLayers() {
        return getAll(LayerInfo.class);
    }


    /****************************************************************/

    @Override
    public MapInfo add(MapInfo map) {
        map = unwrap(map);
        resolve(map);
        return addInfo(map, MapInfo.class);
    }

    @Override
    public void remove(MapInfo map) {
        removeInfo(map);
    }

    @Override
    public void save(MapInfo map) {
        saveInfo(map);
    }

    @Override
    public MapInfo detach(MapInfo map) {
        return map;
    }

    @Override
    public MapInfo getMap(String id) {
        return get(id, MapInfo.class);
    }

    @Override
    public MapInfo getMapByName(String name) {
        return getByName(name, MapInfo.class);
    }

    @Override
    public List<MapInfo> getMaps() {
        return getAll(MapInfo.class);
    }

    /****************************************************************/

    @Override
    public LayerGroupInfo add(LayerGroupInfo layerGroup) {
        layerGroup = unwrap(layerGroup);
        resolve(layerGroup);
        return addInfo(layerGroup, LayerGroupInfo.class);
    }

    @Override
    public void remove(LayerGroupInfo layerGroup) {
        removeInfo(layerGroup);
    }

    @Override
    public void save(LayerGroupInfo layerGroup) {
        saveInfo(layerGroup);
    }

    @Override
    public LayerGroupInfo detach(LayerGroupInfo layerGroup) {
        return layerGroup;
    }

    @Override
    public LayerGroupInfo getLayerGroup(String id) {
        return get(id, LayerGroupInfo.class);
    }

    @Override
    public LayerGroupInfo getLayerGroupByName(String name) {
        return getByName(name, LayerGroupInfo.class);
    }

    @Override
    public LayerGroupInfo getLayerGroupByName(WorkspaceInfo workspace, String name) {
        return getByWorkspaceAndName(workspace, name, LayerGroupInfo.class);
    }

    @Override
    public List<LayerGroupInfo> getLayerGroups() {
        return getAll(LayerGroupInfo.class);
    }

    @Override
    public List<LayerGroupInfo> getLayerGroupsByWorkspace(WorkspaceInfo workspace) {
        return getByWorkspace(workspace, LayerGroupInfo.class);
    }

    /****************************************************************/

    @Override
    public NamespaceInfo add(NamespaceInfo namespace) {
        namespace = unwrap(namespace);
        resolve(namespace);
        return addInfo(namespace, NamespaceInfo.class);
    }

    @Override
    public void remove(NamespaceInfo namespace) {
        removeInfo(namespace);
    }

    @Override
    public void save(NamespaceInfo namespace) {
        saveInfo(namespace);
    }

    @Override
    public NamespaceInfo detach(NamespaceInfo namespace) {
        return namespace;
    }

    @Override
    public NamespaceInfo getDefaultNamespace() {
        return getDefault(NamespaceInfo.class);
    }

    @Override
    public void setDefaultNamespace(NamespaceInfo defaultNamespace) {
        NamespaceInfo ns = defaultNamespace != null ? getNamespaceByPrefix(defaultNamespace.getPrefix()) : null;
        setDefault(NamespaceInfo.class, STR_DEFAULT_NAMESPACE, ns);
    }

    @Override
    public NamespaceInfo getNamespace(String id) {
        return get(id, NamespaceInfo.class);
    }

    @Override
    public NamespaceInfo getNamespaceByPrefix(String prefix) {
        return resolveAndProxy(((NamespaceRepository) repository(NamespaceInfoRedisImpl.class)).findByPrefix(prefix), NamespaceInfo.class);
    }

    @Override
    public NamespaceInfo getNamespaceByURI(String uri) {
        return resolveAndProxy(((NamespaceRepository) repository(NamespaceInfoRedisImpl.class)).findByUri(uri), NamespaceInfo.class);
    }

    @Override
    public List<NamespaceInfo> getNamespaces() {
        return getAll(NamespaceInfo.class);
    }

    /****************************************************************/

    @Override
    public WorkspaceInfo add(WorkspaceInfo workspace) {
        workspace = unwrap(workspace);
        resolve(workspace);
        return addInfo(workspace, WorkspaceInfo.class);
    }

    @Override
    public void remove(WorkspaceInfo workspace) {
        removeInfo(workspace);
    }

    @Override
    public void save(WorkspaceInfo workspace) {
        saveInfo(workspace);
    }

    @Override
    public WorkspaceInfo detach(WorkspaceInfo workspace) {
        return workspace;
    }

    @Override
    public WorkspaceInfo getDefaultWorkspace() {
        return getDefault(WorkspaceInfo.class);
    }

    @Override
    public void setDefaultWorkspace(WorkspaceInfo workspace) {
        WorkspaceInfo ws = workspace != null ? getWorkspaceByName(workspace.getName()) : null;
        setDefault(WorkspaceInfo.class, STR_DEFAULT_WORKSPACE, ws);
    }

    @Override
    public WorkspaceInfo getWorkspace(String id) {
        return get(id, WorkspaceInfo.class);
    }

    @Override
    public WorkspaceInfo getWorkspaceByName(String name) {
        return getByName(name, WorkspaceInfo.class);
    }

    @Override
    public List<WorkspaceInfo> getWorkspaces() {
        return getAll(WorkspaceInfo.class);
    }

    /****************************************************************/

    @Override
    public StyleInfo add(StyleInfo style) {
        style = unwrap(style);
        resolve(style);
        return addInfo(style, StyleInfo.class);
    }

    @Override
    public void remove(StyleInfo style) {
        removeInfo(style);
    }

    @Override
    public void save(StyleInfo style) {
        saveInfo(style);
    }

    @Override
    public StyleInfo detach(StyleInfo style) {
        return style;
    }

    @Override
    public StyleInfo getStyle(String id) {
        return get(id, StyleInfo.class);
    }

    @Override
    public StyleInfo getStyleByName(String name) {
        return getByName(name, StyleInfo.class);
    }

    @Override
    public StyleInfo getStyleByName(WorkspaceInfo workspace, String name) {
        return getByWorkspaceAndName(workspace, name, StyleInfo.class);
    }

    @Override
    public List<StyleInfo> getStyles() {
        return getAll(StyleInfo.class);
    }

    @Override
    public List<StyleInfo> getStylesByWorkspace(WorkspaceInfo workspace) {
        return getByWorkspace(workspace, StyleInfo.class);
    }

    /****************************************************************/

    @Override
    public void dispose() {
        //do nothing
    }

    @Override
    public void resolve() {
        //do nothing
    }

    @Override
    public void syncTo(CatalogFacade other) {
        getWorkspaces().forEach(other::add);
        getNamespaces().forEach(other::add);
        getStores(StoreInfo.class).forEach(other::add);
        getResources(ResourceInfo.class).forEach(other::add);
        getStyles().forEach(other::add);
        getLayers().forEach(other::add);
        getLayerGroups().forEach(other::add);
        getMaps().forEach(other::add);

        other.setDefaultWorkspace(getDefaultWorkspace());
        other.setDefaultNamespace(getDefaultNamespace());

        getWorkspaces().stream().forEach((ws) -> {
            DataStoreInfo defaultDataStore = getDefaultDataStore(ws);
            if (defaultDataStore != null) {
                other.setDefaultDataStore(ws, defaultDataStore);
            }
        });
    }

    /**
     * Get a list of CatalogInfo objects based on a {@link Filter}.
     * Some basic filter parsing is applied to see if we can simplify the query to something that can use an existing
     * redis index, or if we have to get all objects and filter them afterwards.
     * <p>
     * Any changes to this method should be mirrored in {@link CachingCatalogFacade#list(Class, Filter, Integer, Integer, SortBy...)}
     */
    protected <T extends CatalogInfo> List<T> getList(Class<T> of, Filter filter, Integer offset, Integer count, SortBy... sortOrder) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }

        CatalogFacade facade = this;
        if (catalog.getFacade() instanceof CachingCatalogFacade) {
            //Update the cache if applicable, so that we can cache calls to list()
            facade = catalog.getFacade();
        }

        final SortBy[] actualSortOrder = sortOrder == null ? new SortBy[]{SortBy.NATURAL_ORDER} : sortOrder;

        boolean simple = false;
        String id = null;
        String ws = null;
        String wsId = null;
        String rId = null;
        String stId = null;
        WorkspaceInfo wsInfo = null;
        NamespaceInfo nsInfo = null;
        ResourceInfo rInfo = null;
        StoreInfo stInfo = null;


        //Handle some basic special cases for the filter
        List<Filter> filtersToTest = new ArrayList<>();
        if (filter instanceof IsEqualsToImpl) {
            filtersToTest.add(filter);
        } else if (filter instanceof AndImpl) {
            filtersToTest.addAll(((AndImpl) filter).getChildren());
        }
        for (Filter f : filtersToTest) {
            if (f instanceof IsEqualsToImpl) {
                IsEqualsToImpl fEquals = (IsEqualsToImpl) f;
                AttributeExpressionImpl e1 = null;
                LiteralExpressionImpl e2 = null;
                if (fEquals.getExpression1() instanceof AttributeExpressionImpl && fEquals.getExpression2() instanceof LiteralExpressionImpl) {
                    e1 = (AttributeExpressionImpl) fEquals.getExpression1();
                    e2 = (LiteralExpressionImpl) fEquals.getExpression2();
                } else if (fEquals.getExpression2() instanceof AttributeExpressionImpl && fEquals.getExpression1() instanceof LiteralExpressionImpl) {
                    e1 = (AttributeExpressionImpl) fEquals.getExpression2();
                    e2 = (LiteralExpressionImpl) fEquals.getExpression1();
                }
                //If a valid filter was found, save it
                if (e1 != null && e2 != null) {
                    if (e1.getPropertyName().equals("resource.namespace.prefix")) {
                        //filter by workspace via name
                        ws = (String) e2.getValue();
                    }
                    if (e1.getPropertyName().equals("resource.id")) {
                        //filter by resource via id
                        rId = (String) e2.getValue();
                    }
                    if (e1.getPropertyName().equals("workspace.id")) {
                        //filter by workspace via id
                        wsId = (String) e2.getValue();
                    }
                    if (e1.getPropertyName().equals("store.id")) {
                        //filter by store via id
                        stId = (String) e2.getValue();
                    }
                    if (e1.getPropertyName().equals("id")) {
                        //filter by id
                        id = (String) e2.getValue();
                    }
                }
            }
        }

        List<T> results = new ArrayList<>();

        //get workspace to filter by
        if (wsId != null) {
            wsInfo = facade.getWorkspace(wsId);
            if (wsInfo != null) {
                nsInfo = facade.getNamespaceByPrefix(wsInfo.getName());
                simple = true;
            }
        } else if (ws != null) {
            wsInfo = facade.getWorkspaceByName(ws);
            if (wsInfo != null) {
                nsInfo = facade.getNamespaceByPrefix(wsInfo.getName());
                simple = true;
            }
        }
        if (rId != null) {
            rInfo = facade.getResource(rId, ResourceInfo.class);
            if (rInfo != null) {
                simple = true;
            }
        }
        if (stId != null && ResourceInfo.class.isAssignableFrom(of)) {
            stInfo = facade.getStore(stId, StoreInfo.class);
            if (stInfo != null) {
                simple = true;
            }
        }

        //filter by id
        if (id != null) {
            T result = get(id, of);
            if (result != null) {
                results.add(result);
                simple = true;
            }
        }

        //filter by workspace
        if (simple) {
            //Only check ws if we didn't get anything from id
            if (id == null || results.size() == 0) {
                if (ResourceInfo.class.isAssignableFrom(of)) {
                    if (nsInfo != null) {
                        facade.getResourcesByNamespace(nsInfo, (Class<? extends ResourceInfo>) of).forEach(info -> {
                            if (filter.evaluate(info)) {
                                results.add((T) info);
                            }
                        });
                    } else if (stInfo != null) {
                        facade.getResourcesByStore(stInfo, (Class<? extends ResourceInfo>) of).forEach(info -> {
                            if (filter.evaluate(info)) {
                                results.add((T) info);
                            }
                        });
                    }
                } else if (StoreInfo.class.isAssignableFrom(of)) {
                    facade.getStoresByWorkspace(wsInfo, (Class<? extends StoreInfo>) of).forEach(info -> {
                        if (filter.evaluate(info)) {
                            results.add((T) info);
                        }
                    });
                } else if (PublishedInfo.class.isAssignableFrom(of)) {
                    //Can't get layer by ws, only resource
                    if (rInfo != null) {
                        for (LayerInfo info : facade.getLayers(rInfo)) {
                            if (filter.evaluate(info)) {
                                results.add((T) info);
                            }
                        }
                    }
                    if (nsInfo != null) {
                        for (ResourceInfo resource : facade.getResourcesByNamespace(nsInfo, ResourceInfo.class)) {
                            for (LayerInfo info : facade.getLayers(resource)) {
                                if (filter.evaluate(info)) {
                                    results.add((T) info);
                                }
                            }
                        }
                    }
                    if (wsInfo != null) {
                        facade.getLayerGroupsByWorkspace(wsInfo).forEach(info -> {
                            if (filter.evaluate(info)) {
                                results.add((T) info);
                            }
                        });
                    }
                } else {
                    //Failed to find a simple search
                    simple = false;
                }
            }

            //Do a full catalog search, then apply the filter
        }
        if (!simple) {
            if (LayerInfo.class.isAssignableFrom(of) || of.isAssignableFrom(LayerInfo.class)) {
                addResults((Iterable<? extends T>) facade.getLayers(), results, of, filter);
            }
            if (ResourceInfo.class.isAssignableFrom(of) || of.isAssignableFrom(ResourceInfo.class)) {
                addResults((Iterable<? extends T>) facade.getResources((Class<? extends ResourceInfo>) of), results, of, filter);
            }
            if (LayerGroupInfo.class.isAssignableFrom(of) || of.isAssignableFrom(LayerGroupInfo.class)) {
                addResults((Iterable<? extends T>) facade.getLayerGroups(), results, of, filter);
            }
            if (StyleInfo.class.isAssignableFrom(of) || of.isAssignableFrom(StyleInfo.class)) {
                addResults((Iterable<? extends T>) facade.getStyles(), results, of, filter);
            }
            if (MapInfo.class.isAssignableFrom(of) || of.isAssignableFrom(MapInfo.class)) {
                addResults((Iterable<? extends T>) facade.getMaps(), results, of, filter);
            }
            if (WorkspaceInfo.class.isAssignableFrom(of) || of.isAssignableFrom(WorkspaceInfo.class)) {
                addResults((Iterable<? extends T>) facade.getWorkspaces(), results, of, filter);
            }
            if (NamespaceInfo.class.isAssignableFrom(of) || of.isAssignableFrom(NamespaceInfo.class)) {
                addResults((Iterable<? extends T>) facade.getNamespaces(), results, of, filter);
            }
            if (StoreInfo.class.isAssignableFrom(of) || of.isAssignableFrom(StoreInfo.class)) {
                addResults((Iterable<? extends T>) facade.getStores((Class<? extends StoreInfo>) of), results, of, filter);
            }
        }

        //Sort results
        results.sort((o1, o2) -> {
            for (SortBy sb : actualSortOrder) {
                Comparable val1 = sb.getPropertyName() == null ? o1.getId() : (Comparable) sb.getPropertyName().evaluate(o1);
                Comparable val2 = sb.getPropertyName() == null ? o2.getId() : (Comparable) sb.getPropertyName().evaluate(o2);
                int i = val1.compareTo(val2);
                if (i != 0) {
                    return sb.getSortOrder() == SortOrder.DESCENDING ? -i : i;
                }
            }
            return 0;
        });
        if (offset != null || count != null) {
            if (offset == null) {
                offset = 0;
            }
            return results.subList(offset, count == null ? results.size() :
                    Math.min(offset + count, results.size()));
        } else {
            return results;
        }
    }

    private <T extends CatalogInfo> void addResults(Iterable<? extends T> infos, List<T> results, Class<T> of, Filter filter) {
        if (infos != null) {
            infos.forEach(info -> {
                T newInfo = resolveAndProxy(info, of);
                if (filter.evaluate(newInfo)) {
                    results.add(newInfo);
                }
            });
        }
    }

    @Override
    public <T extends CatalogInfo> int count(Class<T> of, Filter filter) {
        return getList(of, filter, null, null).size();
    }

    @Override
    public boolean canSort(Class<? extends CatalogInfo> type, String propertyName) {
        return true;
    }

    @Override
    public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy... sortOrder) {
        return new CloseableIteratorAdapter<>(getList(of, filter, offset, count, sortOrder).iterator());
    }

    /****************************************************************/


    @Override
    protected void resolve(LayerInfo layer) {
        resolve(layer, null);
    }

    @Override
    protected void resolve(LayerGroupInfo layerGroup) {
        resolve(layerGroup, null);
    }

    @Override
    protected void resolve(StyleInfo style) {
        resolve(style, null);
    }

    @Override
    protected void resolve(MapInfo map) {
        resolve(map, null);
    }

    @Override
    protected void resolve(WorkspaceInfo workspace) {
        resolve(workspace, null);
    }

    @Override
    protected void resolve(NamespaceInfo namespace) {
        resolve(namespace, null);
    }

    @Override
    protected void resolve(StoreInfo store) {
        resolve(store, null);
    }

    @Override
    protected void resolve(ResourceInfo resource) {
        resolve(resource, null);
    }

    /****************************************************************/

    protected void resolve(LayerInfo layer, Catalog catalog) {
        if (catalog == null) {
            catalog = getCatalog();
        }
        setId(layer);

        ResourceInfo resource = resolveProxy(catalog, layer.getResource());
        if (resource != null) {
            resource = unwrap(resource);
            layer.setResource(resource);
        } else if (layer.getResource() != null) {
            log.error("Cannot resolve resource for layer \"" + layer.getName() + "\". Catalog is in an inconsistent state!");
            layer.setResource(null);
        }

        StyleInfo style = resolveProxy(catalog, layer.getDefaultStyle());
        if (style != null) {
            style = unwrap(style);
            layer.setDefaultStyle(style);
        } else if (layer.getDefaultStyle() != null) {
            log.error("Cannot resolve style for layer \"" + layer.getName() + "\". Catalog is in an inconsistent state!");
            layer.setDefaultStyle(null);
        }

        LinkedHashSet<StyleInfo> styles = new LinkedHashSet<>();
        for (StyleInfo s : layer.getStyles()) {
            s = resolveProxy(catalog, s);
            s = unwrap(s);
            styles.add(s);
        }
        ((LayerInfoImpl) layer).setStyles(styles);

        AttributionInfo attribution = layer.getAttribution();
        if (attribution != null) {
            attribution = unwrap(attribution);
            layer.setAttribution(attribution);
        } else if (layer.getAttribution() != null) {
            log.error("Cannot resolve attribution for layer \"" + layer.getName() + "\". Catalog is in an inconsistent state!");
            layer.setAttribution(null);
        }
        LegendInfo legend = layer.getLegend();
        if (legend != null) {
            legend = unwrap(legend);
            layer.setLegend(legend);
        } else if (layer.getLegend() != null) {
            log.error("Cannot resolve legend for layer \"" + layer.getName() + "\". Catalog is in an inconsistent state!");
            layer.setLegend(null);
        }
    }

    protected void resolve(LayerGroupInfo layerGroup, Catalog catalog) {
        if (catalog == null) {
            catalog = getCatalog();
        }
        setId(layerGroup);

        LayerGroupInfoImpl lg = (LayerGroupInfoImpl) layerGroup;

        for (int i = 0; i < lg.getLayers().size(); i++) {
            PublishedInfo l = lg.getLayers().get(i);
            PublishedInfo resolved;
            if (l instanceof LayerGroupInfo) {
                resolved = unwrap(resolveProxy(catalog, (LayerGroupInfo) l));
            } else {
                resolved = unwrap(resolveProxy(catalog, (LayerInfo) l));
            }

            lg.getLayers().set(i, resolved != null ? resolved : l);
        }

        for (int i = 0; i < lg.getStyles().size(); i++) {
            StyleInfo s = lg.getStyles().get(i);
            if (s != null) {
                StyleInfo resolved = unwrap(resolveProxy(catalog, s));
                lg.getStyles().set(i, resolved);
            }
        }

        // resolve the workspace -- not sure why this isn't happening in supermethod?
        WorkspaceInfo resolvedWI = resolveProxy(catalog, layerGroup.getWorkspace());
        if (resolvedWI != null) {
            resolvedWI = unwrap(resolvedWI);
            layerGroup.setWorkspace(resolvedWI);
        } else if (layerGroup.getWorkspace() != null) {
            log.error("Cannot resolve workspace for layer group \"" + layerGroup.getName() + "\". Catalog is in an inconsistent state!");
            layerGroup.setWorkspace(null);
        }

        // resolve the root layer -- not sure why this isn't happening in supermethod?
        LayerInfo resolvedRl = resolveProxy(catalog, layerGroup.getRootLayer());
        if (resolvedRl != null) {
            resolvedRl = unwrap(resolvedRl);
            layerGroup.setRootLayer(resolvedRl);
        } else if (layerGroup.getRootLayer() != null) {
            log.error("Cannot resolve root layer for layer group \"" + lg.getName() + "\". Catalog is in an inconsistent state!");
            lg.setRootLayer(null);
        }

        // resolve the root layer style -- not sure why this isn't happening in supermethod?
        StyleInfo resolvedRls = resolveProxy(catalog, layerGroup.getRootLayerStyle());
        if (resolvedRls != null) {
            resolvedRls = unwrap(resolvedRls);
            layerGroup.setRootLayerStyle(resolvedRls);
        } else if (layerGroup.getRootLayerStyle() != null) {
            log.error("Cannot resolve root layer style for layer group \"" + lg.getName() + "\". Catalog is in an inconsistent state!");
            lg.setRootLayerStyle(null);
        }

        AttributionInfo attribution = layerGroup.getAttribution();
        if (attribution != null) {
            attribution = unwrap(attribution);
            layerGroup.setAttribution(attribution);
        } else if (layerGroup.getAttribution() != null) {
            log.error("Cannot resolve attribution for layer group \"" + lg.getName() + "\". Catalog is in an inconsistent state!");
            lg.setAttribution(null);
        }
        List<MetadataLinkInfo> metadataLinks = layerGroup.getMetadataLinks();
        if (metadataLinks != null) {
            for (int i = 0; i < metadataLinks.size(); i++) {
                metadataLinks.set(i, unwrap(metadataLinks.get(i)));
            }
        }
    }

    protected void resolve(StyleInfo style, Catalog catalog) {
        if (catalog == null) {
            catalog = getCatalog();
        }
        setId(style);

        // resolve the workspace -- not sure why this isn't happening in supermethod?
        WorkspaceInfo resolved = resolveProxy(catalog, style.getWorkspace());
        if (resolved != null) {
            resolved = unwrap(resolved);
            style.setWorkspace(resolved);
        } else if (style.getWorkspace() != null) {
            log.error("Cannot resolve workspace for style \"" + style.getName() + "\". Catalog is in an inconsistent state!");
            style.setWorkspace(null);
        }
    }

    protected void resolve(MapInfo map, Catalog catalog) {
        setId(map);
    }

    protected void resolve(WorkspaceInfo workspace, Catalog catalog) {
        setId(workspace);
    }

    protected void resolve(NamespaceInfo namespace, Catalog catalog) {
        setId(namespace);
    }

    protected void resolve(StoreInfo store, Catalog catalog) {
        if (catalog == null) {
            catalog = getCatalog();
        }
        setId(store);
        StoreInfoImpl s = (StoreInfoImpl) store;

        // resolve the workspace
        WorkspaceInfo resolved = resolveProxy(catalog, s.getWorkspace());
        if (resolved != null) {
            resolved = unwrap(resolved);
            s.setWorkspace(resolved);
        } else if (s.getWorkspace() != null) {
            // This may also mean the workspace has not yet been added to the catalog, and we should keep the proxy around
            // However, when it comes to catalog corruption, it is best to err on the side of caution
            log.error("Cannot resolve workspace for store \"" + store.getName() + "\". Catalog is in an inconsistent state!");

            store.setWorkspace(null);
        }
    }

    protected void resolve(ResourceInfo resource, Catalog catalog) {
        if (catalog == null) {
            catalog = getCatalog();
        }
        setId(resource);
        ResourceInfoImpl r = (ResourceInfoImpl) resource;

        // resolve the store
        StoreInfo store = resolveProxy(catalog, r.getStore());
        if (store != null) {
            store = unwrap(store);
            r.setStore(store);
        } else if (r.getStore() != null) {
            log.error("Cannot resolve store for resource \"" + r.getName() + "\". Catalog is in an inconsistent state!");
            r.setStore(null);
        }

        // resolve the namespace
        NamespaceInfo namespace = resolveProxy(catalog, r.getNamespace());
        if (namespace != null) {
            namespace = unwrap(namespace);
            r.setNamespace(namespace);
        } else if (r.getNamespace() != null) {
            log.error("Cannot resolve namespace for resource \"" + r.getName() + "\". Catalog is in an inconsistent state!");
            r.setNamespace(null);
        }

        List<DataLinkInfo> dataLinks = resource.getDataLinks();
        if (dataLinks != null) {
            for (int i = 0; i < dataLinks.size(); i++) {
                dataLinks.set(i, unwrap(dataLinks.get(i)));
            }
        }

        List<MetadataLinkInfo> metadataLinks = resource.getMetadataLinks();
        if (metadataLinks != null) {
            for (int i = 0; i < metadataLinks.size(); i++) {
                metadataLinks.set(i, unwrap(metadataLinks.get(i)));
            }
        }
    }
}