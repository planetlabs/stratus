/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.DefaultCatalogFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link CatalogCache}
 */
public class DefaultCatalogCache extends DefaultCatalogFacade implements CatalogCache {

    private HashMap<Class<? extends StoreInfo>,Boolean> allStores = new HashMap<>();
    private HashMap<Class<? extends ResourceInfo>,Boolean> allResources = new HashMap<>();
    private boolean allLayers = false;
    private boolean allMaps = false;
    private boolean allLayerGroups = false;
    private boolean allNamespaces = false;
    private boolean allWorkspaces = false;
    private boolean allStyles = false;
    private HashMap<Class<? extends ResourceInfo>, Set<String>> storesByWorkspace = new HashMap<>();
    private HashMap<Class<? extends ResourceInfo>, Set<String>> resourcesByNamespace = new HashMap<>();
    private HashMap<Class<? extends ResourceInfo>, Set<String>> resourcesByStore = new HashMap<>();
    private Set<String> layersByResource = new HashSet<>();
    private Set<String> layersByStyle = new HashSet<>();
    private Set<String> layerGroupsByWorkspace = new HashSet<>();
    private Set<String> stylesByWorkspace = new HashSet<>();

    private HashMap<String, Set<List<Object>>> cachedMethodCalls = new HashMap<>();

    public DefaultCatalogCache(Catalog catalog) {
        super(catalog);
    }

    @Override
    public boolean isCached(String method, List<Object> arguments) {
        Set<List<Object>> calls = cachedMethodCalls.get(method);
        return calls != null && calls.contains(arguments);
    }

    @Override
    public void setCached(String method, List<Object> arguments, boolean cached) {
        Set<List<Object>> calls = cachedMethodCalls.get(method);

        if (cached) {
            if (calls == null) {
                calls = new HashSet<>();
                cachedMethodCalls.put(method, calls);
            }
            calls.add(arguments);
        } else {
            if (calls != null) {
                calls.remove(arguments);
            }
        }
    }

    @Override
    public boolean isStoresCached(Class clazz) {
        boolean cached = false;
        if (allStores.containsKey(clazz)) {
            cached = cached || allStores.get(clazz);
        }
        if (!StoreInfo.class.equals(clazz) && allStores.containsKey(StoreInfo.class)) {
            cached = cached || allStores.get(StoreInfo.class);
        }
        return cached;
    }

    @Override
    public boolean isStoresCached(WorkspaceInfo workspace, Class clazz) {
        boolean cached = false;
        if (storesByWorkspace.containsKey(clazz)) {
            cached = cached || storesByWorkspace.get(clazz).contains(workspace.getId());
        }
        if (!StoreInfo.class.equals(clazz) && storesByWorkspace.containsKey(StoreInfo.class)) {
            cached = cached || storesByWorkspace.get(StoreInfo.class).contains(workspace.getId());
        }
        return cached;
    }

    @Override
    public boolean isResourcesCached(Class clazz) {
        boolean cached = false;
        if (allResources.containsKey(clazz)) {
            cached = cached || allResources.get(clazz);
        }
        if (!ResourceInfo.class.equals(clazz) && allResources.containsKey(ResourceInfo.class)) {
            cached = cached || allResources.get(ResourceInfo.class);
        }
        return cached;
    }

    @Override
    public boolean isResourcesCached(NamespaceInfo namespace, Class clazz) {
        boolean cached = false;
        if (resourcesByNamespace.containsKey(clazz)) {
            cached = cached || resourcesByNamespace.get(clazz).contains(namespace.getId());
        }
        if (!ResourceInfo.class.equals(clazz) && resourcesByNamespace.containsKey(ResourceInfo.class)) {
            cached = cached || resourcesByNamespace.get(ResourceInfo.class).contains(namespace.getId());
        }
        //are all resources cached?
        return cached || isResourcesCached(clazz);
    }

    @Override
    public boolean isResourcesCached(StoreInfo store, Class clazz) {
        boolean cached = false;
        if (resourcesByStore.containsKey(clazz)) {
            cached = cached || resourcesByStore.get(clazz).contains(store.getId());
        }
        if (!ResourceInfo.class.equals(clazz) && resourcesByStore.containsKey(ResourceInfo.class)) {
            cached = cached || resourcesByStore.get(ResourceInfo.class).contains(store.getId());
        }
        if (store.getWorkspace() != null && store.getWorkspace().getName() != null) {
            //Are all resources in the store's workspace cached?
            NamespaceInfo namespace = getNamespaceByPrefix(store.getWorkspace().getName());
            cached = cached || (namespace != null && isResourcesCached(namespace, clazz));
        }
        return cached;
    }

    @Override
    public boolean isLayersCached() {
        return allLayers;
    }

    @Override
    public boolean isLayersCached(ResourceInfo resource) {
        return layersByResource.contains(resource.getId());
    }

    @Override
    public boolean isLayersCached(StyleInfo style) {
        return layersByStyle.contains(style.getId());
    }

    @Override
    public boolean isMapsCached() {
        return allMaps;
    }

    @Override
    public boolean isLayerGroupsCached() {
        return allLayerGroups;
    }

    @Override
    public boolean isLayerGroupsCached(WorkspaceInfo workspace) {
        return isLayerGroupsCached() || layerGroupsByWorkspace.contains(workspace.getId());
    }

    @Override
    public boolean isNamespacesCached() {
        return allNamespaces;
    }

    @Override
    public boolean isWorkspacesCached() {
        return allWorkspaces;
    }

    @Override
    public boolean isStylesCached() {
        return allStyles;
    }

    @Override
    public boolean isStylesCached(WorkspaceInfo workspace) {
        String workspaceId = null;
        if (workspace != null && workspace != NO_WORKSPACE) {
            workspaceId = workspace.getId();
        }
        return isStylesCached() || stylesByWorkspace.contains(workspaceId);
    }

    @Override
    public void setStoresCached(Class clazz, boolean cached) {
        allStores.put(clazz, cached);
    }

    @Override
    public void setStoresCached(WorkspaceInfo workspace, Class clazz, boolean cached) {
        if (!storesByWorkspace.containsKey(clazz)) {
            storesByWorkspace.put(clazz, new HashSet<>());
        }
        if (cached) {
            storesByWorkspace.get(clazz).add(workspace.getId());
        } else {
            storesByWorkspace.get(clazz).remove(workspace.getId());
        }
    }

    @Override
    public void setResourcesCached(Class clazz, boolean cached) {
        allResources.put(clazz, cached);
    }

    @Override
    public void setResourcesCached(NamespaceInfo namespace, Class clazz, boolean cached) {
        if (!resourcesByNamespace.containsKey(clazz)) {
            resourcesByNamespace.put(clazz, new HashSet<>());
        }
        if (cached) {
            resourcesByNamespace.get(clazz).add(namespace.getId());
        } else {
            resourcesByNamespace.get(clazz).remove(namespace.getId());
        }
    }

    @Override
    public void setResourcesCached(StoreInfo store, Class clazz, boolean cached) {
        if (!resourcesByStore.containsKey(clazz)) {
            resourcesByStore.put(clazz, new HashSet<>());
        }
        if (cached) {
            resourcesByStore.get(clazz).add(store.getId());
        } else {
            resourcesByStore.get(clazz).remove(store.getId());
        }
    }

    @Override
    public void setLayersCached(boolean cached) {
        allLayers = cached;
    }

    @Override
    public void setLayersCached(ResourceInfo resource, boolean cached) {
        if (cached) {
            layersByResource.add(resource.getId());
        } else {
            layersByResource.remove(resource.getId());
        }
    }

    @Override
    public void setLayersCached(StyleInfo style, boolean cached) {
        if (cached) {
            layersByStyle.add(style.getId());
        } else {
            layersByStyle.remove(style.getId());
        }
    }

    @Override
    public void setMapsCached(boolean cached) {
        allMaps = cached;
    }

    @Override
    public void setLayerGroupsCached(boolean cached) {
        allLayerGroups = cached;
    }

    @Override
    public void setLayerGroupsCached(WorkspaceInfo workspace, boolean cached) {
        if (cached) {
            layerGroupsByWorkspace.add(workspace.getId());
        } else {
            layerGroupsByWorkspace.remove(workspace.getId());
        }
    }

    @Override
    public void setNamespacesCached(boolean cached) {
        allNamespaces = cached;
    }

    @Override
    public void setWorkspacesCached(boolean cached) {
        allWorkspaces = cached;
    }

    @Override
    public void setStylesCached(boolean cached) {
        allStyles = cached;
    }

    @Override
    public void setStylesCached(WorkspaceInfo workspace, boolean cached) {
        String workspaceId = null;
        if (workspace != null && workspace != NO_WORKSPACE) {
            workspaceId = workspace.getId();
        }
        if (cached) {
            stylesByWorkspace.add(workspaceId);
        } else {
            stylesByWorkspace.remove(workspaceId);
        }
    }
}
