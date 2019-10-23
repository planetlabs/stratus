/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import stratus.redis.catalog.RedisCatalogFacade;
import com.google.common.base.Preconditions;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.*;
import org.geoserver.catalog.util.CloseableIterator;
import org.geotools.filter.AndImpl;
import org.geotools.filter.AttributeExpressionImpl;
import org.geotools.filter.IsEqualsToImpl;
import org.geotools.filter.LiteralExpressionImpl;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Catalog Facade which caches a set of CatalogInfo objects, and delegates to the passed facade.
 * Intended to be used when repeatedly resolving references in situations where looking up CatalogInfo objects is
 * expensive, e.g. when doing a getResourcesByStore against a remote catalog.
 *
 * All get methods will first check the context cache, only querying the delegate facade if the requested object is not
 * found.
 * All other methods will just use the delegate. Note that this means if you update a catalog object, the cached version
 * will not be changed. Consequently, this class should generally be used in read-only situations.
 *
 * The cache is stored in a {@link ThreadLocal} ({@link CachingFilter#CATALOG}), and therefore caching operations will
 * only affect the current thread.
 *
 * @author tbarsballe
 */
public class CachingCatalogFacade extends AbstractCatalogFacade  implements CachingFacade<CatalogCache, CatalogInfo> {
    
    CatalogImpl catalog;
    CatalogFacade delegate;

    private static CatalogCache dummyCache = new DummyCatalogCache();

    //TODO: Move method names for setCached to static constants

    /**
     * Retrieves an underlying {@link CachingCatalogFacade} from a {@link Catalog}.
     *
     * @param catalog The Catalog
     * @return The {@link CachingCatalogFacade} underlying the catalog, or null if no such facade exists.
     */
    public static CachingCatalogFacade unwrapCatalog(Catalog catalog) {
        CatalogImpl catalogImpl;

        if (catalog instanceof AbstractCatalogDecorator) {
            catalogImpl = ((AbstractCatalogDecorator) catalog).unwrap(CatalogImpl.class);
        } else {
            catalogImpl = ((CatalogImpl) catalog);
        }
        CatalogFacade facade = ProxyUtils.unwrap(catalogImpl.getFacade(), LockingCatalogFacade.class);
        if (facade instanceof CachingCatalogFacade) {
            return (CachingCatalogFacade) facade;
        }
        return null;
    }

    /**
     * Returns true if the workspace is not null or {@link CatalogFacade#NO_WORKSPACE}
     * 
     * @param workspace the workspace
     * @return
     */
    public static boolean wsPresent(WorkspaceInfo workspace) {
        return !(workspace == null || workspace == NO_WORKSPACE);
    }

    /**
     * If T or T#getId() is null, return null. Otherwise, unwrap any {@link ModificationProxy}s and return the base object
     * 
     * @param info The CatalogInfo to unwrap
     * @param <T>
     * @return The unwrapped info
     */
    public static <T extends CatalogInfo> T unwrapOrNull(@Nullable T info) {
        return (info == null || info.getId() == null ? null : ModificationProxy.unwrap(info));
    }
    
    /**
     * Create a new CachingCatalogFacade, passing in the contents of the context cache.
     *
     * @param catalog The parent catalog.
     * @param delegate The delegating facade to use for objects not in the cache.
     */
    public CachingCatalogFacade(Catalog catalog, CatalogFacade delegate) {
        Preconditions.checkArgument(catalog instanceof CatalogImpl);
        this.delegate = delegate;
        this.catalog = (CatalogImpl) catalog;
        this.delegate.setCatalog(catalog);
    }

    private CatalogCache checkCache() {
        CatalogCache cache = CachingFilter.CATALOG.get();
        if (cache != null) {
            //cache.setCatalog(catalog);
            return cache;
        }
        return dummyCache;
    }

    private boolean isResolvingProxy(Object object) {
        if (object instanceof Proxy) {
            InvocationHandler h = Proxy.getInvocationHandler(object);
            return h instanceof ResolvingProxy;
        }
        return false;
    }

    private void resolveLayerGroup(LayerGroupInfo lg) {
        catalog.resolve(lg);

        List<PublishedInfo> layers = lg.getLayers();
        List<StyleInfo> styles = lg.getStyles();

        for (int i = 0; i < layers.size(); i++) {
            layers.set(i,  ResolvingProxy.resolve(catalog, layers.get(i)));
            styles.set(i,  ResolvingProxy.resolve(catalog, styles.get(i)));
        }
    }

    /**
     * Loads the provided list of CatalogInfo objects into the cache.
     * Future requests to the catalog will use the underlying facade if values are not found in the cache
     *
     * Since the cache is stored in a {@link ThreadLocal}, this will only apply to the current thread.
     * @param infos
     */
    public void loadCache(Iterable<CatalogInfo> infos) {
        loadCache(infos, false);
    }

    /**
     * Loads the provided list of CatalogInfo objects into the cache.
     *
     * Since the cache is stored in a {@link ThreadLocal}, this will only apply to the current thread.
     *
     * @param infos
     * @param isComplete If true, marks the cache as an authoratative representation of the catalog.
     *                   Future requests to the catalog will NOT use the underlying facade if values are not found in
     *                   the cache.
     *                   Otherwise, future requests to the catalog will use the underlying facade if values are not
     *                   found in the cache.
     */
    public void loadCache(Iterable<CatalogInfo> infos, boolean isComplete) {
        CatalogCache cache = checkCache();
        //Load cache in order, such that AbstractCatalogFacade#resolve does not trigger unnecessary lookups
        for (CatalogInfo info : infos) {
            //All these don't depend on anything; resolve just sets the id
            if (info instanceof MapInfo) {
                catalog.resolve(info);
                cache.add((MapInfo) info);
            } else if (info instanceof WorkspaceInfo) {
                catalog.resolve(info);
                cache.add((WorkspaceInfo) info);
            } else if (info instanceof NamespaceInfo) {
                catalog.resolve(info);
                cache.add((NamespaceInfo) info);
            }
        }
        for (CatalogInfo info : infos) {
            //may depend on workspace
            if (info instanceof StyleInfo) {
                catalog.resolve(info);
                //Workaround for resolving style workspace (if set).
                //TODO: remove this when the regular catalog handles this correctly
                WorkspaceInfo resolved = ResolvingProxy.resolve(catalog, ((StyleInfo) info).getWorkspace());
                if (resolved != null) {
                    resolved = unwrap(resolved);
                    ((StyleInfo) info).setWorkspace(resolved);
                }
                cache.add((StyleInfo) info);
            //depends on workspace
            } else if (info instanceof StoreInfo) {
                catalog.resolve(info);
                cache.add((StoreInfo) info);
            }
        }
        for (CatalogInfo info : infos) {
            //depends on store and namespace
            if (info instanceof ResourceInfo) {
                catalog.resolve(info);
                cache.add((ResourceInfo) info);
            }
        }
        for (CatalogInfo info : infos) {
            //depends on resource
            if (info instanceof LayerInfo) {
                catalog.resolve(info);
                cache.add((LayerInfo) info);
            }
        }
        //Layer groups gan be nested inside each other, so we need to be sure to add/resolve them in the right order
        List<LayerGroupInfo> layerGroups = new ArrayList<>();
        for (CatalogInfo info : infos) {
            //depends on layer
            if (info instanceof LayerGroupInfo) {
                layerGroups.add((LayerGroupInfo) info);
            }
        }
        boolean allResolved = false;
        //Make sure we don't get stuck in an infinite loop - deepest possible nesting is the number of layer groups
        int iterations = layerGroups.size();
        while (!allResolved || iterations > 0 ) {
            List<LayerGroupInfo> unresolvedLayerGroups = new ArrayList<>();
            allResolved = true;
            iterations--;

            for (LayerGroupInfo layerGroup : layerGroups) {
                resolveLayerGroup(layerGroup);
                boolean resolved = true;
                for (PublishedInfo published : layerGroup.getLayers()) {
                    //If the lg still contains a resolving proxy, one if its children hasn't been resolved yet
                    if (isResolvingProxy(published)) {
                        resolved = false;
                        break;
                    }
                }
                if (resolved) {
                    cache.add(layerGroup);
                } else {
                    unresolvedLayerGroups.add(layerGroup);
                    allResolved = false;
                }
            }
            //reset list of layer groups to only those that haven't been resolved
            layerGroups = unresolvedLayerGroups;
            if (layerGroups.size() < iterations) {
                iterations = layerGroups.size();
            }
        }

        if (isComplete) {
            //Set flags for list gets
            cache.setStylesCached(true);
            cache.setMapsCached(true);
            cache.setWorkspacesCached(true);
            cache.setNamespacesCached(true);
            cache.setStoresCached(StoreInfo.class, true);
            cache.setResourcesCached(ResourceInfo.class, true);
            cache.setLayersCached(true);
            cache.setLayerGroupsCached(true);
        }
    }

    /**
     * Returns the delegate {@link CatalogFacade}
     * @return delegate
     */
    public CatalogFacade getDelegate() {
        return delegate;
    }

    /**
     * Returns the current cache. Since this is stored in a {@link ThreadLocal}, the same instance of
     * CachingCatalogFacade may return a different value for the cache, depending on the thread this is called from.
     *
     * @return cache
     */
    public CatalogCache getCache() {
        return checkCache();
    }

    @Override
    public void setCatalog(Catalog catalog) {
        Preconditions.checkArgument(catalog instanceof CatalogImpl);
        Preconditions.checkArgument(ProxyUtils.unwrap(catalog.getFacade(), LockingCatalogFacade.class) == this);
        this.catalog = (CatalogImpl)catalog;
        delegate.setCatalog(catalog);
    }

    @Override
    public CatalogCapabilities getCatalogCapabilities() {
        return delegate.getCatalogCapabilities();
    }

    /* cached methods */

    @Override
    public <T extends StoreInfo> T getStore(String id, Class<T> clazz) {
        CatalogCache cache = checkCache();
        T store = cache.getStore(id, clazz);
        if (store != null && Objects.equals(store.getId(), id) && clazz.isAssignableFrom(store.getClass())) {
            return store;
        }
        //If we have loaded all stores, querying delegate won't return anything
        if (cache.isStoresCached(clazz) || cache.isStoresCached(StoreInfo.class)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getStore", Arrays.asList(id, clazz))) {
            return null;
        }
        store = delegate.getStore(id, clazz);
        if (store != null) {
            cache.add(ModificationProxy.unwrap(store));
        } else {
            cache.setCached("getStore", Arrays.asList(id, clazz), true);
        }
        return store;
    }

    @Override
    public <T extends StoreInfo> T getStoreByName(WorkspaceInfo workspace, String name, Class<T> clazz) {
        CatalogCache cache = checkCache();
        T store = cache.getStoreByName(workspace, name, clazz);
        if (store != null &&
                (!wsPresent(workspace) ? !wsPresent(store.getWorkspace()) :
                (store.getWorkspace() != null && Objects.equals(workspace.getId(), store.getWorkspace().getId())) &&
                        Objects.equals(store.getName(), name) && clazz.isAssignableFrom(store.getClass()))) {
            return store;
        }
        //If we have loaded all stores, querying delegate won't return anything
        if (cache.isStoresCached(clazz) || cache.isStoresCached(StoreInfo.class)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getStoreByName", Arrays.asList(unwrapOrNull(workspace), name, clazz))) {
            return null;
        }
        store = delegate.getStoreByName(workspace, name, clazz);
        if (store != null) {
            cache.add(ModificationProxy.unwrap(store));
        } else {
            cache.setCached("getStoreByName", Arrays.asList(unwrapOrNull(workspace), name, clazz), true);
        }
        return store;
    }

    @Override
    public <T extends ResourceInfo> T getResource(String id, Class<T> clazz) {
        CatalogCache cache = checkCache();
        T resource = cache.getResource(id, clazz);
        if (resource != null && Objects.equals(resource.getId(), id) && clazz.isAssignableFrom(resource.getClass())) {
            return resource;
        }
        //If we have loaded all resources, querying delegate won't return anything
        if (cache.isResourcesCached(clazz) || cache.isResourcesCached(ResourceInfo.class)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getResource", Arrays.asList(id, clazz))) {
            return null;
        }
        resource = delegate.getResource(id, clazz);
        if (resource != null) {
            cache.add(ModificationProxy.unwrap(resource));
        } else {
            cache.setCached("getResource", Arrays.asList(id, clazz), true);
        }
        return resource;
    }

    @Override
    public <T extends ResourceInfo> T getResourceByName(NamespaceInfo namespace, String name, Class<T> clazz) {
        CatalogCache cache = checkCache();
        T resource = cache.getResourceByName(namespace, name, clazz);
        if (resource != null &&
                (namespace == null ? resource.getNamespace() == null :
                (resource.getNamespace() != null && Objects.equals(namespace.getId(), resource.getNamespace().getId())) &&
                        Objects.equals(resource.getName(), name) &&
                        clazz.isAssignableFrom(resource.getClass()))) {
            return resource;
        }
        //If we have loaded all resources, querying delegate won't return anything
        if (cache.isResourcesCached(clazz) || cache.isResourcesCached(ResourceInfo.class)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getResourceByName", Arrays.asList(unwrapOrNull(namespace), name, clazz))) {
            return null;
        }
        resource = delegate.getResourceByName(namespace, name, clazz);
        if (resource != null) {
            cache.add(ModificationProxy.unwrap(resource));
        } else {
            cache.setCached("getResourceByName", Arrays.asList(unwrapOrNull(namespace), name, clazz), true);
        }
        return resource;
    }

    @Override
    public <T extends ResourceInfo> T getResourceByStore(StoreInfo store, String name, Class<T> clazz) {
        CatalogCache cache = checkCache();
        T resource = cache.getResourceByStore(store, name, clazz);
        if (resource != null &&
                (store == null ? resource.getStore() == null :
                (resource.getStore() != null && Objects.equals(store.getId(), resource.getStore().getId())) &&
                        Objects.equals(resource.getName(), name) &&
                        clazz.isAssignableFrom(resource.getClass()))) {
            return resource;
        }
        //If we have loaded all resources, querying delegate won't return anything
        if (cache.isResourcesCached(clazz) || cache.isResourcesCached(ResourceInfo.class)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getResourceByStore", Arrays.asList(store, name, clazz))) {
            return null;
        }
        resource = delegate.getResourceByStore(store, name, clazz);
        if (resource != null) {
            cache.add(ModificationProxy.unwrap(resource));
        } else {
            cache.setCached("getResourceByStore", Arrays.asList(store, name, clazz), true);
        }
        return resource;
    }

    @Override
    public LayerInfo getLayer(String id) {
        CatalogCache cache = checkCache();
        LayerInfo layer = cache.getLayer(id);
        if (layer != null && Objects.equals(layer.getId(), id)) {
            return layer;
        }
        //If we have loaded all layers, querying delegate won't return anything
        if (cache.isLayersCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLayer", Collections.singletonList(id))) {
            return null;
        }
        layer = delegate.getLayer(id);
        if (layer != null) {
            cache.add(ModificationProxy.unwrap(layer));
        } else {
            cache.setCached("getLayer", Collections.singletonList(id), true);
        }
        return layer;

    }

    @Override
    public LayerInfo getLayerByName(String name) {
        CatalogCache cache = checkCache();
        LayerInfo layer = cache.getLayerByName(name);
        if (layer != null && Objects.equals(layer.getName(), name)) {
            return layer;
        }
        //If we have loaded all layers, querying delegate won't return anything
        if (cache.isLayersCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLayerByName", Collections.singletonList(name))) {
            return null;
        }
        layer = delegate.getLayerByName(name);
        if (layer != null) {
            cache.add(ModificationProxy.unwrap(layer));
        } else {
            cache.setCached("getLayerByName", Collections.singletonList(name), true);
        }
        return layer;
    }

    @Override
    public MapInfo getMap(String id) {
        CatalogCache cache = checkCache();
        MapInfo map = cache.getMap(id);
        if (map != null && Objects.equals(map.getId(), id)) {
            return map;
        }
        //If we have loaded all maps, querying delegate won't return anything
        if (cache.isMapsCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getMap", Collections.singletonList(id))) {
            return null;
        }
        map = delegate.getMap(id);
        if (map != null) {
            cache.add(ModificationProxy.unwrap(map));
        }  else {
            cache.setCached("getMap", Collections.singletonList(id), true);
        }
        return map;
    }

    @Override
    public MapInfo getMapByName(String name) {
        CatalogCache cache = checkCache();
        MapInfo map = cache.getMapByName(name);
        if (map != null && Objects.equals(map.getName(), name)) {
            return map;
        }
        //If we have loaded all maps, querying delegate won't return anything
        if (cache.isMapsCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getMapByName", Collections.singletonList(name))) {
            return null;
        }
        map = delegate.getMapByName(name);
        if (map != null) {
            cache.add(ModificationProxy.unwrap(map));
        } else {
            cache.setCached("getMapByName", Collections.singletonList(name), true);
        }
        return map;
    }

    @Override
    public LayerGroupInfo getLayerGroup(String id) {
        CatalogCache cache = checkCache();
        LayerGroupInfo layerGroup = cache.getLayerGroup(id);
        if (layerGroup != null && Objects.equals(layerGroup.getId(), id)) {
            return layerGroup;
        }
        //If we have loaded all layer groups, querying delegate won't return anything
        if (cache.isLayerGroupsCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLayerGroup", Collections.singletonList(id))) {
            return null;
        }
        layerGroup = delegate.getLayerGroup(id);
        if (layerGroup != null) {
            cache.add(ModificationProxy.unwrap(layerGroup));
        }  else {
            cache.setCached("getLayerGroup", Collections.singletonList(id), true);
        }
        return layerGroup;
    }

    @Override
    public LayerGroupInfo getLayerGroupByName(String name) {
        CatalogCache cache = checkCache();
        LayerGroupInfo layerGroup = cache.getLayerGroupByName(name);
        if (layerGroup != null && Objects.equals(layerGroup.getName(), name)) {
            return layerGroup;
        }
        //Check if a layer with this name already exists; if so a layer group with the same name is impossible
        if (getLayerByPrefixedName(name) != null) {
            return null;
        }
        //If we have loaded all layer groups, querying delegate won't return anything
        if (cache.isLayerGroupsCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLayerGroupByName", Collections.singletonList(name))) {
            return null;
        }
        //Finally, check delegate
        layerGroup = delegate.getLayerGroupByName(name);
        if (layerGroup != null) {
            cache.add(ModificationProxy.unwrap(layerGroup));
        } else {
            cache.setCached("getLayerGroupByName", Collections.singletonList(name), true);
        }
        return layerGroup;
    }

    @Override
    public LayerGroupInfo getLayerGroupByName(WorkspaceInfo workspace, String name) {
        CatalogCache cache = checkCache();
        LayerGroupInfo layerGroup = cache.getLayerGroupByName(workspace, name);
        if (layerGroup != null &&
                (!wsPresent(workspace) ? !wsPresent(layerGroup.getWorkspace()) :
                (layerGroup.getWorkspace() != null && workspace.getId().equals(layerGroup.getWorkspace().getId())) &&
                        Objects.equals(layerGroup.getName(), name))
                ) {
            return layerGroup;
        }
        //Check if a layer with this name already exists; if so a layer group with the same name is impossible
        LayerInfo layer = (!wsPresent(workspace) || workspace.getName() == null) ? getLayerByName(name) : getLayerByName(workspace.getName(), name);
        if (layer != null) {
            return null;
        }
        if (!wsPresent(workspace)) {
            //If this is a global lg, check if a workspace with this name already exists; if so a layer group with the same name is impossible
            WorkspaceInfo ws = getWorkspaceByName(name);
            if (ws != null) {
                return null;
            }
            if (getLayerByPrefixedName(name) != null) {
                return null;
            }
        }
        //If we have loaded all layer groups, querying delegate won't return anything
        if (cache.isLayerGroupsCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLayerGroupByName", Arrays.asList(unwrapOrNull(workspace), name))) {
            return null;
        }
        //Finally, check delegate
        layerGroup = delegate.getLayerGroupByName(workspace, name);
        if (layerGroup != null) {
            cache.add(ModificationProxy.unwrap(layerGroup));
        } else {
            cache.setCached("getLayerGroupByName", Arrays.asList(unwrapOrNull(workspace), name), true);
        }
        return layerGroup;
    }

    private LayerInfo getLayerByPrefixedName(String name) {
        LayerInfo layer = null;
        int colon = name.indexOf( ':' );
        if ( colon != -1 ) {
            //search by resource name
            String prefix = name.substring( 0, colon );
            String resource = name.substring( colon + 1 );

            layer = getLayerByName(prefix, resource);
        } else {
            // search in default workspace first
            WorkspaceInfo ws = getDefaultWorkspace();
            if ( ws != null ) {
                layer = getLayerByName(ws.getName(), name);
            }
        }
        return layer;
    }
    private LayerInfo getLayerByName(String prefix, String name) {
        CatalogCache cache = checkCache();
        NamespaceInfo ns = cache.getNamespaceByPrefix(prefix);
        ResourceInfo resource = cache.getResourceByName(ns, name, ResourceInfo.class);

        if (resource != null) {
            List<LayerInfo> layers = getLayers(resource);
            if (layers.size() == 1) {
                return layers.get(0);
            }
        }
        return null;
    }

    @Override
    public NamespaceInfo getNamespace(String id) {
        CatalogCache cache = checkCache();
        NamespaceInfo namespace = cache.getNamespace(id);
        if (namespace != null && Objects.equals(namespace.getId(), id)) {
            return namespace;
        }
        //If we have loaded all namespaces, querying delegate won't return anything
        if (cache.isNamespacesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getNamespace", Collections.singletonList(id))) {
            return null;
        }
        namespace = delegate.getNamespace(id);
        if (namespace != null) {
            cache.add(ModificationProxy.unwrap(namespace));
        } else {
            cache.setCached("getNamespace", Collections.singletonList(id), true);
        }
        return namespace;
    }

    @Override
    public NamespaceInfo getNamespaceByPrefix(String prefix) {
        CatalogCache cache = checkCache();
        NamespaceInfo namespace = cache.getNamespaceByPrefix(prefix);
        if (namespace != null && Objects.equals(namespace.getPrefix(), prefix)) {
            return namespace;
        }
        //If we have loaded all namespaces, querying delegate won't return anything
        if (cache.isNamespacesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getNamespaceByPrefix", Collections.singletonList(prefix))) {
            return null;
        }
        namespace = delegate.getNamespaceByPrefix(prefix);
        if (namespace != null) {
            cache.add(ModificationProxy.unwrap(namespace));
        } else {
            cache.setCached("getNamespaceByPrefix", Collections.singletonList(prefix), true);
        }
        return namespace;
    }

    @Override
    public NamespaceInfo getNamespaceByURI(String uri) {
        CatalogCache cache = checkCache();
        NamespaceInfo namespace = cache.getNamespaceByURI(uri);
        if (namespace != null && Objects.equals(namespace.getURI(), uri)) {
            return namespace;
        }
        //If we have loaded all namespaces, querying delegate won't return anything
        if (cache.isNamespacesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getNamespaceByURI", Collections.singletonList(uri))) {
            return null;
        }
        namespace = delegate.getNamespaceByURI(uri);
        if (namespace != null) {
            cache.add(ModificationProxy.unwrap(namespace));
        } else {
            cache.setCached("getNamespaceByURI", Collections.singletonList(uri), true);
        }
        return namespace;
    }

    @Override
    public WorkspaceInfo getWorkspace(String id) {
        CatalogCache cache = checkCache();
        WorkspaceInfo workspace = cache.getWorkspace(id);
        if (workspace != null && Objects.equals(workspace.getId(), id)) {
            return workspace;
        }
        //If we have loaded all workspaces, querying delegate won't return anything
        if (cache.isWorkspacesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getWorkspace", Collections.singletonList(id))) {
            return null;
        }
        workspace = delegate.getWorkspace(id);
        if (workspace != null) {
            cache.add(ModificationProxy.unwrap(workspace));
        } else {
            cache.setCached("getWorkspace", Collections.singletonList(id), true);
        }
        return workspace;
    }

    @Override
    public WorkspaceInfo getWorkspaceByName(String name) {
        CatalogCache cache = checkCache();
        WorkspaceInfo workspace = cache.getWorkspaceByName(name);
        if (workspace != null && Objects.equals(workspace.getName(), name)) {
            return workspace;
        }
        //If we have loaded all workspaces, querying delegate won't return anything
        if (cache.isWorkspacesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getWorkspaceByName", Collections.singletonList(name))) {
            return null;
        }
        workspace = delegate.getWorkspaceByName(name);
        if (workspace != null) {
            cache.add(ModificationProxy.unwrap(workspace));
        } else {
            cache.setCached("getWorkspaceByName", Collections.singletonList(name), true);
        }
        return workspace;
    }

    @Override
    public StyleInfo getStyle(String id) {
        CatalogCache cache = checkCache();
        StyleInfo style = cache.getStyle(id);
        if (style != null && Objects.equals(style.getId(), id)) {
            return style;
        }
        //If we have loaded all styles, querying delegate won't return anything
        if (cache.isStylesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getStyle", Collections.singletonList(id))) {
            return null;
        }
        style = delegate.getStyle(id);
        if (style != null) {
            cache.add(ModificationProxy.unwrap(style));
        } else {
            cache.setCached("getStyle", Collections.singletonList(id), true);
        }
        return style;
    }

    @Override
    public StyleInfo getStyleByName(String name) {
        CatalogCache cache = checkCache();
        StyleInfo style = cache.getStyleByName(name);
        if (style != null && Objects.equals(style.getName(), name)) {
            return style;
        }
        //If we have loaded all styles, querying delegate won't return anything
        if (cache.isStylesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getStyleByName", Collections.singletonList(name))) {
            return null;
        }
        style = delegate.getStyleByName(name);
        if (style != null) {
            cache.add(ModificationProxy.unwrap(style));
        } else {
            cache.setCached("getStyleByName", Collections.singletonList(name), true);
        }
        return style;
    }

    @Override
    public StyleInfo getStyleByName(WorkspaceInfo workspace, String name) {
        CatalogCache cache = checkCache();
        StyleInfo style = cache.getStyleByName(workspace, name);
        if (style != null && (workspace == ANY_WORKSPACE ||
                ((workspace == null || workspace == NO_WORKSPACE) ? style.getWorkspace() == null :
                (style.getWorkspace() != null && workspace.getId().equals(style.getWorkspace().getId())) &&
                        Objects.equals(style.getName(), name)))) {
            return style;
        }
        //If we have loaded all styles, querying delegate won't return anything
        if (cache.isStylesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getStyleByName", Arrays.asList(unwrapOrNull(workspace), name))) {
            return null;
        }
        style = delegate.getStyleByName(workspace, name);
        if (style != null) {
            cache.add(ModificationProxy.unwrap(style));
        } else {
            cache.setCached("getStyleByName", Arrays.asList(unwrapOrNull(workspace), name), true);
        }
        return style;
    }

    /* default gets */

    @Override
    public DataStoreInfo getDefaultDataStore(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        DataStoreInfo store = cache.getDefaultDataStore(workspace);
        if (store != null &&
                (!wsPresent(workspace) ? !wsPresent(store.getWorkspace()) :
                (store.getWorkspace() != null && Objects.equals(workspace.getId(), store.getWorkspace().getId())))) {
            return store;
        }
        store = delegate.getDefaultDataStore(workspace);
        if (store != null) {
            if (cache.getStore(store.getId(), store.getClass()) == null) {
                cache.add(ModificationProxy.unwrap(store));
            }
            cache.setDefaultDataStore(workspace, ModificationProxy.unwrap(store));
        }
        return store;
    }

    @Override
    public void setDefaultDataStore(WorkspaceInfo workspace, DataStoreInfo store) {
        CatalogCache cache = checkCache();
        delegate.setDefaultDataStore(workspace, store);
        cache.setDefaultDataStore(workspace, store);
    }

    @Override
    public NamespaceInfo getDefaultNamespace() {
        CatalogCache cache = checkCache();
        NamespaceInfo namespace = cache.getDefaultNamespace();
        if (namespace != null) {
            return namespace;
        }
        namespace = delegate.getDefaultNamespace();
        if (namespace != null) {
            if (cache.getNamespace(namespace.getId()) == null) {
                cache.add(ModificationProxy.unwrap(namespace));
            }
            cache.setDefaultNamespace(ModificationProxy.unwrap(namespace));
        }

        return namespace;
    }

    @Override
    public void setDefaultNamespace(NamespaceInfo defaultNamespace) {
        CatalogCache cache = checkCache();
        delegate.setDefaultNamespace(defaultNamespace);
        cache.setDefaultNamespace(defaultNamespace);
    }

    @Override
    public WorkspaceInfo getDefaultWorkspace() {
        CatalogCache cache = checkCache();
        WorkspaceInfo workspace = cache.getDefaultWorkspace();
        if (workspace != null) {
            return workspace;
        }
        workspace = delegate.getDefaultWorkspace();
        if (workspace != null) {
            if (cache.getWorkspace(workspace.getId()) == null) {
                cache.add(ModificationProxy.unwrap(workspace));
            }
            cache.setDefaultWorkspace(ModificationProxy.unwrap(workspace));
        }
        return workspace;
    }

    @Override
    public void setDefaultWorkspace(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        delegate.setDefaultWorkspace(workspace);
        cache.setDefaultWorkspace(workspace);
    }

    /* list gets */

    @Override
    public <T extends StoreInfo> List<T> getStores(Class<T> clazz) {
        CatalogCache cache = checkCache();
        if (cache.isStoresCached(clazz) || cache.isStoresCached(StoreInfo.class)) {
            return cache.getStores(clazz);
        }
        //If stores are not cached, load workspaces to speed up resolution
        if (!cache.isWorkspacesCached()) {
            getWorkspaces();
        }
        List<T> stores = delegate.getStores(clazz);
        for (T store : ModificationProxy.unwrap(stores)) {
            if (cache.getStore(store.getId(), clazz) == null) {
                cache.add(ModificationProxy.unwrap(store));
            }
        }
        cache.setStoresCached(clazz, true);
        return stores;
    }

    @Override
    public <T extends StoreInfo> List<T> getStoresByWorkspace(WorkspaceInfo workspace, Class<T> clazz) {
        CatalogCache cache = checkCache();
        if (cache.isStoresCached(workspace, clazz)) {
            return cache.getStoresByWorkspace(workspace, clazz);
        }
        List<T> stores = delegate.getStoresByWorkspace(workspace, clazz);
        for (T store : ModificationProxy.unwrap(stores)) {
            if (cache.getStore(store.getId(), clazz) == null) {
                cache.add(ModificationProxy.unwrap(store));
            }
        }
        cache.setStoresCached(workspace, clazz, true);
        return stores;
    }

    @Override
    public <T extends ResourceInfo> List<T> getResources(Class<T> clazz) {
        CatalogCache cache = checkCache();
        if (cache.isResourcesCached(clazz) || cache.isResourcesCached(ResourceInfo.class)) {
            return cache.getResources(clazz);
        }
        //If resources are not cached, load stores and namespaces to speed up resolution
        if (!cache.isNamespacesCached()) {
            getNamespaces();
        }
        if (!cache.isStoresCached(StoreInfo.class)) {
            if (clazz == FeatureTypeInfo.class) {
                if (!cache.isStoresCached(DataStoreInfo.class)) {
                    getStores(DataStoreInfo.class);
                }
            } else if (clazz == CoverageInfo.class) {
                if (!cache.isStoresCached(CoverageStoreInfo.class)) {
                    getStores(CoverageStoreInfo.class);
                }
            } else if (clazz == WMSLayerInfo.class) {
                if (!cache.isStoresCached(DataStoreInfo.class)) {
                    getStores(CoverageStoreInfo.class);
                }
            } else {
                getStores(StoreInfo.class);
            }
        }
        List<T> resources = delegate.getResources(clazz);
        for (T resource : ModificationProxy.unwrap(resources)) {
            if (cache.getResource(resource.getId(), clazz) == null) {
                cache.add(ModificationProxy.unwrap(resource));
            }
        }
        cache.setResourcesCached(clazz, true);
        return resources;
    }

    @Override
    public <T extends ResourceInfo> List<T> getResourcesByNamespace(NamespaceInfo namespace, Class<T> clazz) {
        CatalogCache cache = checkCache();
        if (cache.isResourcesCached(namespace, clazz)) {
            return cache.getResourcesByNamespace(namespace, clazz);
        }
        List<T> resources = delegate.getResourcesByNamespace(namespace, clazz);
        for (T resource : ModificationProxy.unwrap(resources)) {
            if (cache.getResource(resource.getId(), clazz) == null) {
                cache.add(ModificationProxy.unwrap(resource));
            }
        }
        cache.setResourcesCached(namespace, clazz, true);
        return resources;
    }

    @Override
    public <T extends ResourceInfo> List<T> getResourcesByStore(StoreInfo store, Class<T> clazz) {
        CatalogCache cache = checkCache();
        if (cache.isResourcesCached(store, clazz)) {
            return cache.getResourcesByStore(store, clazz);
        }
        List<T> resources = delegate.getResourcesByStore(store, clazz);
        for (T resource : ModificationProxy.unwrap(resources)) {
            if (cache.getResource(resource.getId(), clazz) == null) {
                cache.add(ModificationProxy.unwrap(resource));
            }
        }
        cache.setResourcesCached(store, clazz, true);
        return resources;
    }

    @Override
    public List<LayerInfo> getLayers(ResourceInfo resource) {
        CatalogCache cache = checkCache();
        //If we have exactly one layer matching this resource, assume that is correct, and return it
        List<LayerInfo> layers = cache.getLayers(resource);
        if (layers != null && layers.size() == 1) {
            return layers;
        }

        if (cache.isLayersCached(resource)) {
            return cache.getLayers(resource);
        }
        layers = delegate.getLayers(resource);
        for (LayerInfo layer : ModificationProxy.unwrap(layers)) {
            if (cache.getLayer(layer.getId()) == null) {
                cache.add(ModificationProxy.unwrap(layer));
            }
        }
        cache.setLayersCached(resource, true);
        return layers;
    }

    @Override
    public List<LayerInfo> getLayers(StyleInfo style) {
        CatalogCache cache = checkCache();
        if (cache.isLayersCached(style)) {
            return cache.getLayers(style);
        }
        List<LayerInfo> layers = delegate.getLayers(style);
        for (LayerInfo layer : ModificationProxy.unwrap(layers)) {
            if (cache.getLayer(layer.getId()) == null) {
                cache.add(ModificationProxy.unwrap(layer));
            }
        }
        cache.setLayersCached(style, true);
        return layers;
    }

    @Override
    public List<LayerInfo> getLayers() {
        CatalogCache cache = checkCache();
        if (cache.isLayersCached()) {
            return cache.getLayers();
        }
        //If layers are not cached, load resources to speed up resolution
        if (!cache.isResourcesCached(ResourceInfo.class)) {
            getResources(ResourceInfo.class);
        }
        List<LayerInfo> layers = delegate.getLayers();
        for (LayerInfo layer : ModificationProxy.unwrap(layers)) {
            if (cache.getLayer(layer.getId()) == null) {
                cache.add(ModificationProxy.unwrap(layer));
            }
        }
        cache.setLayersCached(true);
        return layers;
    }

    @Override
    public List<MapInfo> getMaps() {
        CatalogCache cache = checkCache();
        if (cache.isMapsCached()) {
            return cache.getMaps();
        }
        List<MapInfo> maps = delegate.getMaps();
        for (MapInfo map : ModificationProxy.unwrap(maps)) {
            if (cache.getMap(map.getId()) == null) {
                cache.add(ModificationProxy.unwrap(map));
            }
        }
        cache.setMapsCached(true);
        return maps;
    }

    @Override
    public List<LayerGroupInfo> getLayerGroups() {
        CatalogCache cache = checkCache();
        if (cache.isLayerGroupsCached()) {
            return cache.getLayerGroups();
        }
        List<LayerGroupInfo> layerGroups = delegate.getLayerGroups();
        for (LayerGroupInfo layerGroup : ModificationProxy.unwrap(layerGroups)) {
            if (cache.getLayerGroup(layerGroup.getId()) == null) {
                cache.add(ModificationProxy.unwrap(layerGroup));
            }
        }
        cache.setLayerGroupsCached(true);
        return layerGroups;
    }

    @Override
    public List<LayerGroupInfo> getLayerGroupsByWorkspace(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        if (cache.isLayerGroupsCached(workspace)) {
            return cache.getLayerGroupsByWorkspace(workspace);
        }
        List<LayerGroupInfo> layerGroups = delegate.getLayerGroupsByWorkspace(workspace);
        for (LayerGroupInfo layerGroup : ModificationProxy.unwrap(layerGroups)) {
            if (cache.getLayerGroup(layerGroup.getId()) == null) {
                cache.add(ModificationProxy.unwrap(layerGroup));
            }
        }
        cache.setLayerGroupsCached(workspace, true);
        return layerGroups;
    }

    @Override
    public List<NamespaceInfo> getNamespaces() {
        CatalogCache cache = checkCache();
        if (cache.isNamespacesCached()) {
            return cache.getNamespaces();
        }
        List<NamespaceInfo> namespaces = delegate.getNamespaces();
        for (NamespaceInfo namespace : ModificationProxy.unwrap(namespaces)) {
            if (cache.getNamespace(namespace.getId()) == null) {
                cache.add(ModificationProxy.unwrap(namespace));
            }
        }
        cache.setNamespacesCached(true);
        return namespaces;
    }


    @Override
    public List<WorkspaceInfo> getWorkspaces() {
        CatalogCache cache = checkCache();
        if (cache.isWorkspacesCached()) {
            return cache.getWorkspaces();
        }
        List<WorkspaceInfo> workspaces = delegate.getWorkspaces();
        for (WorkspaceInfo workspace : ModificationProxy.unwrap(workspaces)) {
            if (cache.getWorkspace(workspace.getId()) == null) {
                cache.add(ModificationProxy.unwrap(workspace));
            }
        }
        cache.setWorkspacesCached(true);
        return workspaces;
    }

    @Override
    public List<StyleInfo> getStyles() {
        CatalogCache cache = checkCache();
        if (cache.isStylesCached()) {
            return cache.getStyles();
        }
        List<StyleInfo> styles = delegate.getStyles();
        for (StyleInfo style : ModificationProxy.unwrap(styles)) {
            if (cache.getStyle(style.getId()) == null) {
                cache.add(ModificationProxy.unwrap(style));
            }
        }
        cache.setStylesCached(true);
        return styles;
    }

    @Override
    public List<StyleInfo> getStylesByWorkspace(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        if (cache.isStylesCached(workspace)) {
            return cache.getStylesByWorkspace(workspace);
        }
        List<StyleInfo> styles = delegate.getStylesByWorkspace(workspace);
        for (StyleInfo style : ModificationProxy.unwrap(styles)) {
            if (cache.getStyle(style.getId()) == null) {
                cache.add(ModificationProxy.unwrap(style));
            }
        }
        cache.setStylesCached(workspace, true);
        return styles;
    }

    @Override
    public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy sortOrder) {
        return list(of, filter, offset, count, sortOrder != null ? new SortBy[]{sortOrder}:null);
    }

    /**
     * Get a list of CatalogInfo objects based on a {@link Filter}.
     * Some basic filter parsing is applied to see if we can directly query the cache, or need to back off to the facade.
     *
     * Any changes to this method should be mirrored in {@link RedisCatalogFacade#getList(Class, Filter, Integer, Integer, SortBy...)}
     */
    @Override
    public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy... sortOrder) {
        if (filter == null) {
            throw new IllegalArgumentException();
        }
        CatalogCache cache = checkCache();

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
        } else if  (filter instanceof AndImpl) {
            filtersToTest.addAll(((AndImpl) filter).getChildren());
        }
        for (Filter f : filtersToTest) {
            if (f instanceof IsEqualsToImpl) {
                IsEqualsToImpl fEquals = (IsEqualsToImpl)f;
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

        //get workspace to filter by
        if (wsId != null) {
            wsInfo = cache.getWorkspace(wsId);
            if (wsInfo != null) {
                nsInfo = cache.getNamespaceByPrefix(wsInfo.getName());
            }
        } else if (ws != null) {
            wsInfo = cache.getWorkspaceByName(ws);
            if (wsInfo != null) {
                nsInfo = cache.getNamespaceByPrefix(wsInfo.getName());
            }
        }
        if (rId != null) {
            rInfo = cache.getResource(rId, ResourceInfo.class);
        }
        if (stId != null && ResourceInfo.class.isAssignableFrom(of)) {
            stInfo = cache.getStore(stId, StoreInfo.class);
        }
        if (WorkspaceInfo.class.isAssignableFrom(of) &&
                ((cache.isWorkspacesCached()) || (id != null && cache.getWorkspace(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (NamespaceInfo.class.isAssignableFrom(of) &&
                ((cache.isNamespacesCached()) || (id != null && cache.getNamespace(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (LayerInfo.class.isAssignableFrom(of) &&
                ((cache.isLayersCached()) || (id != null && cache.getLayer(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (LayerGroupInfo.class.isAssignableFrom(of) &&
                ((cache.isLayerGroupsCached()) || (id != null && cache.getLayerGroup(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (StyleInfo.class.isAssignableFrom(of) &&
                ((cache.isStylesCached()) || (id != null && cache.getStyle(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (MapInfo.class.isAssignableFrom(of) &&
                ((cache.isMapsCached()) || (id != null && cache.getMap(id) != null))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (ResourceInfo.class.isAssignableFrom(of) && (cache.isResourcesCached((Class<? extends ResourceInfo>) of) || (
                    (id != null && cache.getResource(id, (Class<? extends ResourceInfo>) of) != null) ||
                    (id == null && ((nsInfo != null && cache.isResourcesCached(nsInfo, (Class<? extends ResourceInfo>) of)))) ||
                    (id == null && ((stInfo != null && cache.isResourcesCached(stInfo, (Class<? extends ResourceInfo>) of))))
                ))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (StoreInfo.class.isAssignableFrom(of) && (cache.isStoresCached((Class<? extends StoreInfo>) of) ||
                ((id != null && cache.getStore(id, (Class<? extends StoreInfo>) of) != null) || (id == null && (
                        (wsInfo != null && cache.isStoresCached(wsInfo, (Class<? extends StoreInfo>) of))))))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }
        if (PublishedInfo.class.isAssignableFrom(of) &&
                ((id != null && cache.getLayer(id) != null) ||
                        (id == null && (
                            ((cache.isResourcesCached(ResourceInfo.class)) || (nsInfo != null && cache.isResourcesCached(nsInfo, ResourceInfo.class))) &&
                            ((cache.isLayerGroupsCached()) || (wsInfo != null && cache.isLayerGroupsCached(wsInfo))) &&
                            (cache.isLayersCached())
                        )) || (rInfo != null && (cache.isLayersCached(rInfo)) || cache.isLayersCached())
                )) {
            return cache.list(of, filter, offset, count, sortOrder);
        }
        //of can be any type of catalogInfo; need everything
        if ((cache.isWorkspacesCached() && cache.isNamespacesCached() && cache.isLayersCached()
                && cache.isLayerGroupsCached() && cache.isStylesCached() && cache.isMapsCached() &&
                (cache.isResourcesCached(ResourceInfo.class) || (cache.isResourcesCached(FeatureTypeInfo.class) &&
                        cache.isResourcesCached(CoverageInfo.class) && cache.isResourcesCached(WMSLayerInfo.class))) &&
                (cache.isStoresCached(StoreInfo.class) || (cache.isStoresCached(DataStoreInfo.class) &&
                        cache.isStoresCached(CoverageStoreInfo.class) && cache.isStoresCached(WMSStoreInfo.class))))) {

            return cache.list(of, filter, offset, count, sortOrder);
        }

        return delegate.list(of, filter, offset, count, sortOrder);
    }

    /* delegating methods */

    @Override
    public Catalog getCatalog() {
        return delegate.getCatalog();
    }

    @Override
    public StoreInfo add(StoreInfo store) {
        CatalogCache cache = checkCache();
        cache.add(store);
        store = delegate.add(store);
        //Since this is a newly added store, it can't contain anything in the cache
        cache.setResourcesCached(store, ResourceInfo.class, true);
        if (store instanceof DataStoreInfo) {
            cache.setResourcesCached(store, FeatureTypeInfo.class, true);
        }
        if (store instanceof CoverageStoreInfo) {
            cache.setResourcesCached(store, CoverageInfo.class, true);
        }
        if (store instanceof WMSStoreInfo) {
            cache.setResourcesCached(store, WMSLayerInfo.class, true);
        }
        return store;
    }

    @Override
    public void remove(StoreInfo store) {
        CatalogCache cache = checkCache();
        StoreInfo info = cache.getStore(store.getId(), StoreInfo.class);
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(store);
    }

    @Override
    public void save(StoreInfo store) {
        delegate.save(store);
        CatalogCache cache = checkCache();
        StoreInfo info = cache.getStore(store.getId(), StoreInfo.class);
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(store));
    }

    @Override
    public <T extends StoreInfo> T detach(T store) {
        return delegate.detach(store);
    }

    @Override
    public ResourceInfo add(ResourceInfo resource) {
        CatalogCache cache = checkCache();
        cache.add(resource);
        resource =  delegate.add(resource);
        //Since this is a newly added resource, it can't contain anything not in the cache
        cache.setLayersCached(resource, true);
        return resource;
    }

    @Override
    public void remove(ResourceInfo resource) {
        CatalogCache cache = checkCache();
        ResourceInfo info = cache.getResource(resource.getId(), ResourceInfo.class);
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(resource);
    }

    @Override
    public void save(ResourceInfo resource) {
        delegate.save(resource);
        CatalogCache cache = checkCache();
        ResourceInfo info = cache.getResource(resource.getId(), ResourceInfo.class);
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(resource));
    }

    @Override
    public <T extends ResourceInfo> T detach(T resource) {
        return delegate.detach(resource);
    }

    @Override
    public LayerInfo add(LayerInfo layer) {
        CatalogCache cache = checkCache();
        cache.add(layer);
        return delegate.add(layer);
    }

    @Override
    public void remove(LayerInfo layer) {
        CatalogCache cache = checkCache();
        LayerInfo info = cache.getLayer(layer.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(layer);
    }

    @Override
    public void save(LayerInfo layer) {
        delegate.save(layer);
        CatalogCache cache = checkCache();
        LayerInfo info = cache.getLayer(layer.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(layer));
    }

    @Override
    public LayerInfo detach(LayerInfo layer) {
        return delegate.detach(layer);
    }

    @Override
    public MapInfo add(MapInfo map) {
        CatalogCache cache = checkCache();
        cache.add(map);
        return delegate.add(map);
    }

    @Override
    public void remove(MapInfo map) {
        CatalogCache cache = checkCache();
        MapInfo info = cache.getMap(map.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(map);
    }

    @Override
    public void save(MapInfo map) {
        delegate.save(map);
        CatalogCache cache = checkCache();
        MapInfo info = cache.getMap(map.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(map));
    }

    @Override
    public MapInfo detach(MapInfo map) {
        return delegate.detach(map);
    }

    @Override
    public LayerGroupInfo add(LayerGroupInfo layerGroup) {
        CatalogCache cache = checkCache();
        cache.add(layerGroup);
        return delegate.add(layerGroup);
    }

    @Override
    public void remove(LayerGroupInfo layerGroup) {
        CatalogCache cache = checkCache();
        LayerGroupInfo info = cache.getLayerGroup(layerGroup.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(layerGroup);
    }

    @Override
    public void save(LayerGroupInfo layerGroup) {
        delegate.save(layerGroup);
        CatalogCache cache = checkCache();
        LayerGroupInfo info = cache.getLayerGroup(layerGroup.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(layerGroup));
    }

    @Override
    public LayerGroupInfo detach(LayerGroupInfo layerGroup) {
        return delegate.detach(layerGroup);
    }

    @Override
    public NamespaceInfo add(NamespaceInfo namespace) {
        CatalogCache cache = checkCache();
        cache.add(namespace);
        return delegate.add(namespace);
    }

    @Override
    public void remove(NamespaceInfo namespace) {
        CatalogCache cache = checkCache();
        NamespaceInfo info = cache.getNamespace(namespace.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(namespace);
    }

    @Override
    public void save(NamespaceInfo namespace) {
        delegate.save(namespace);
        CatalogCache cache = checkCache();
        NamespaceInfo info = cache.getNamespace(namespace.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(namespace));
    }

    @Override
    public NamespaceInfo detach(NamespaceInfo namespace) {
        return delegate.detach(namespace);
    }

    @Override
    public WorkspaceInfo add(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        cache.add(workspace);
        return delegate.add(workspace);
    }

    @Override
    public void remove(WorkspaceInfo workspace) {
        CatalogCache cache = checkCache();
        WorkspaceInfo info = cache.getWorkspace(workspace.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(workspace);
    }

    @Override
    public void save(WorkspaceInfo workspace) {
        delegate.save(workspace);
        CatalogCache cache = checkCache();
        WorkspaceInfo info = cache.getWorkspace(workspace.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(workspace));
    }

    @Override
    public WorkspaceInfo detach(WorkspaceInfo workspace) {
        return delegate.detach(workspace);
    }

    @Override
    public StyleInfo add(StyleInfo style) {
        CatalogCache cache = checkCache();
        cache.add(style);
        return delegate.add(style);
    }

    @Override
    public void remove(StyleInfo style) {
        CatalogCache cache = checkCache();
        StyleInfo info = cache.getStyle(style.getId());
        if (info != null) {
            cache.remove(info);
        }
        delegate.remove(style);
    }

    @Override
    public void save(StyleInfo style) {
        delegate.save(style);
        CatalogCache cache = checkCache();
        StyleInfo info = cache.getStyle(style.getId());
        if (info != null) {
            cache.remove(info);
        }
        cache.add(ModificationProxy.unwrap(style));
    }

    @Override
    public StyleInfo detach(StyleInfo style) {
        return delegate.detach(style);
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    @Override
    public void resolve() {
        delegate.resolve();
    }

    @Override
    public void syncTo(CatalogFacade other) {
        delegate.syncTo(other);
    }

    @Override
    public <T extends CatalogInfo> int count(Class<T> of, Filter filter) {
        return delegate.count(of, filter);
    }

    @Override
    public boolean canSort(Class<? extends CatalogInfo> type, String propertyName) {
        return delegate.canSort(type, propertyName);
    }

    private static class DummyCatalogCache implements CatalogCache {

        @Override
        public Catalog getCatalog() {
            return null;
        }

        @Override
        public void setCatalog(Catalog catalog) {

        }

        @Override
        public StoreInfo add(StoreInfo store) {
            return null;
        }

        @Override
        public void remove(StoreInfo store) {

        }

        @Override
        public void save(StoreInfo store) {

        }

        @Override
        public <T extends StoreInfo> T detach(T store) {
            return null;
        }

        @Override
        public <T extends StoreInfo> T getStore(String id, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends StoreInfo> T getStoreByName(WorkspaceInfo workspace, String name, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends StoreInfo> List<T> getStoresByWorkspace(WorkspaceInfo workspace, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends StoreInfo> List<T> getStores(Class<T> clazz) {
            return null;
        }

        @Override
        public DataStoreInfo getDefaultDataStore(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public void setDefaultDataStore(WorkspaceInfo workspace, DataStoreInfo store) {

        }

        @Override
        public ResourceInfo add(ResourceInfo resource) {
            return null;
        }

        @Override
        public void remove(ResourceInfo resource) {

        }

        @Override
        public void save(ResourceInfo resource) {

        }

        @Override
        public <T extends ResourceInfo> T detach(T resource) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> T getResource(String id, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> T getResourceByName(NamespaceInfo namespace, String name, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> List<T> getResources(Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> List<T> getResourcesByNamespace(NamespaceInfo namespace, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> T getResourceByStore(StoreInfo store, String name, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ResourceInfo> List<T> getResourcesByStore(StoreInfo store, Class<T> clazz) {
            return null;
        }

        @Override
        public LayerInfo add(LayerInfo layer) {
            return null;
        }

        @Override
        public void remove(LayerInfo layer) {

        }

        @Override
        public void save(LayerInfo layer) {

        }

        @Override
        public LayerInfo detach(LayerInfo layer) {
            return null;
        }

        @Override
        public LayerInfo getLayer(String id) {
            return null;
        }

        @Override
        public LayerInfo getLayerByName(String name) {
            return null;
        }

        @Override
        public List<LayerInfo> getLayers(ResourceInfo resource) {
            return null;
        }

        @Override
        public List<LayerInfo> getLayers(StyleInfo style) {
            return null;
        }

        @Override
        public List<LayerInfo> getLayers() {
            return null;
        }

        @Override
        public MapInfo add(MapInfo map) {
            return null;
        }

        @Override
        public void remove(MapInfo map) {

        }

        @Override
        public void save(MapInfo map) {

        }

        @Override
        public MapInfo detach(MapInfo map) {
            return null;
        }

        @Override
        public MapInfo getMap(String id) {
            return null;
        }

        @Override
        public MapInfo getMapByName(String name) {
            return null;
        }

        @Override
        public List<MapInfo> getMaps() {
            return null;
        }

        @Override
        public LayerGroupInfo add(LayerGroupInfo layerGroup) {
            return null;
        }

        @Override
        public void remove(LayerGroupInfo layerGroup) {

        }

        @Override
        public void save(LayerGroupInfo layerGroup) {

        }

        @Override
        public LayerGroupInfo detach(LayerGroupInfo layerGroup) {
            return null;
        }

        @Override
        public LayerGroupInfo getLayerGroup(String id) {
            return null;
        }

        @Override
        public LayerGroupInfo getLayerGroupByName(String name) {
            return null;
        }

        @Override
        public LayerGroupInfo getLayerGroupByName(WorkspaceInfo workspace, String name) {
            return null;
        }

        @Override
        public List<LayerGroupInfo> getLayerGroups() {
            return null;
        }

        @Override
        public List<LayerGroupInfo> getLayerGroupsByWorkspace(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public NamespaceInfo add(NamespaceInfo namespace) {
            return null;
        }

        @Override
        public void remove(NamespaceInfo namespace) {

        }

        @Override
        public void save(NamespaceInfo namespace) {

        }

        @Override
        public NamespaceInfo detach(NamespaceInfo namespace) {
            return null;
        }

        @Override
        public NamespaceInfo getDefaultNamespace() {
            return null;
        }

        @Override
        public void setDefaultNamespace(NamespaceInfo defaultNamespace) {

        }

        @Override
        public NamespaceInfo getNamespace(String id) {
            return null;
        }

        @Override
        public NamespaceInfo getNamespaceByPrefix(String prefix) {
            return null;
        }

        @Override
        public NamespaceInfo getNamespaceByURI(String uri) {
            return null;
        }

        @Override
        public List<NamespaceInfo> getNamespaces() {
            return null;
        }

        @Override
        public WorkspaceInfo add(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public void remove(WorkspaceInfo workspace) {

        }

        @Override
        public void save(WorkspaceInfo workspace) {

        }

        @Override
        public WorkspaceInfo detach(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public WorkspaceInfo getDefaultWorkspace() {
            return null;
        }

        @Override
        public void setDefaultWorkspace(WorkspaceInfo workspace) {

        }

        @Override
        public WorkspaceInfo getWorkspace(String id) {
            return null;
        }

        @Override
        public WorkspaceInfo getWorkspaceByName(String name) {
            return null;
        }

        @Override
        public List<WorkspaceInfo> getWorkspaces() {
            return null;
        }

        @Override
        public StyleInfo add(StyleInfo style) {
            return null;
        }

        @Override
        public void remove(StyleInfo style) {

        }

        @Override
        public void save(StyleInfo style) {

        }

        @Override
        public StyleInfo detach(StyleInfo style) {
            return null;
        }

        @Override
        public StyleInfo getStyle(String id) {
            return null;
        }

        @Override
        public StyleInfo getStyleByName(String name) {
            return null;
        }

        @Override
        public StyleInfo getStyleByName(WorkspaceInfo workspace, String name) {
            return null;
        }

        @Override
        public List<StyleInfo> getStyles() {
            return null;
        }

        @Override
        public List<StyleInfo> getStylesByWorkspace(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void resolve() {

        }

        @Override
        public void syncTo(CatalogFacade other) {

        }

        @Override
        public <T extends CatalogInfo> int count(Class<T> of, Filter filter) {
            return 0;
        }

        @Override
        public boolean canSort(Class<? extends CatalogInfo> type, String propertyName) {
            return false;
        }

        @Override
        public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy sortOrder) {
            return null;
        }

        @Override
        public <T extends CatalogInfo> CloseableIterator<T> list(Class<T> of, Filter filter, Integer offset, Integer count, SortBy... sortOrder) {
            return null;
        }

        @Override
        public boolean isStoresCached(Class clazz) {
            return false;
        }

        @Override
        public boolean isStoresCached(WorkspaceInfo workspace, Class clazz) {
            return false;
        }

        @Override
        public boolean isResourcesCached(Class clazz) {
            return false;
        }

        @Override
        public boolean isResourcesCached(NamespaceInfo namespace, Class clazz) {
            return false;
        }

        @Override
        public boolean isResourcesCached(StoreInfo store, Class clazz) {
            return false;
        }

        @Override
        public boolean isCached(String method, List<Object> arguments) {
            return false;
        }

        @Override
        public void setCached(String method, List<Object> arguments, boolean cached) { }

        @Override
        public boolean isLayersCached() {
            return false;
        }

        @Override
        public boolean isLayersCached(ResourceInfo resource) {
            return false;
        }

        @Override
        public boolean isLayersCached(StyleInfo style) {
            return false;
        }

        @Override
        public boolean isMapsCached() {
            return false;
        }

        @Override
        public boolean isLayerGroupsCached() {
            return false;
        }

        @Override
        public boolean isLayerGroupsCached(WorkspaceInfo workspace) {
            return false;
        }

        @Override
        public boolean isNamespacesCached() {
            return false;
        }

        @Override
        public boolean isWorkspacesCached() {
            return false;
        }

        @Override
        public boolean isStylesCached() {
            return false;
        }

        @Override
        public boolean isStylesCached(WorkspaceInfo workspace) {
            return false;
        }

        @Override
        public void setStoresCached(Class clazz, boolean cached) {

        }

        @Override
        public void setStoresCached(WorkspaceInfo workspace, Class clazz, boolean cached) {

        }

        @Override
        public void setResourcesCached(Class clazz, boolean cached) {

        }

        @Override
        public void setResourcesCached(NamespaceInfo namespace, Class clazz, boolean cached) {

        }

        @Override
        public void setResourcesCached(StoreInfo store, Class clazz, boolean cached) {

        }

        @Override
        public void setLayersCached(boolean cached) {

        }

        @Override
        public void setLayersCached(ResourceInfo resource, boolean cached) {

        }

        @Override
        public void setLayersCached(StyleInfo style, boolean cached) {

        }

        @Override
        public void setMapsCached(boolean cached) {

        }

        @Override
        public void setLayerGroupsCached(boolean cached) {

        }

        @Override
        public void setLayerGroupsCached(WorkspaceInfo workspace, boolean cached) {

        }

        @Override
        public void setNamespacesCached(boolean cached) {

        }

        @Override
        public void setWorkspacesCached(boolean cached) {

        }

        @Override
        public void setStylesCached(boolean cached) {

        }

        @Override
        public void setStylesCached(WorkspaceInfo workspace, boolean cached) {

        }
    }
}
