/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import org.geoserver.catalog.*;

/**
 * Extension of {@link CatalogFacade} intended to support caching.
 *
 * Describes a list of getters/setters to determine if certain list queries have been cached.
 * This allows {@link CachingCatalogFacade} to determine if list queries such as {@link CatalogFacade#getLayers()} can
 * use the cache.
 *
 * Provides utilities for determining if the cache/delegate has been queried for certain values. This allows caching of
 * fail-to-finds.
 */
public interface CatalogCache extends CatalogFacade, Cache {

    /**
     * Determines if all stores of the provided class have been cached
     *
     * @param clazz The class of store. One of {@link StoreInfo}, {@link DataStoreInfo}, {@link CoverageStoreInfo}, or {@link WMSStoreInfo}
     * @return True if all stores of the provided class have been cached
     */
    <T extends StoreInfo> boolean isStoresCached(Class<T> clazz);

    /**
     * Determines if all stores of the provided class and workspace have been cached
     *
     * @param workspace
     * @param clazz The class of store. One of {@link StoreInfo}, {@link DataStoreInfo}, {@link CoverageStoreInfo}, or {@link WMSStoreInfo}
     * @return True if all stores of the provided class and workspace have been cached
     */
    <T extends StoreInfo> boolean isStoresCached(WorkspaceInfo workspace, Class<T> clazz);

    /**
     * Determines if all resources of the provided class have been cached
     *
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @return True if all resources of the provided class have been cached
     */
    <T extends ResourceInfo> boolean isResourcesCached(Class<T> clazz);

    /**
     * Determines if all resources of the provided class and namespace have been cached
     *
     * @param namespace
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @return True if all resources of the provided class and namespace have been cached
     */
    <T extends ResourceInfo> boolean isResourcesCached(NamespaceInfo namespace, Class<T> clazz);

    /**
     * Determines if all resources of the provided class and store have been cached
     *
     * @param store
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @return True if all resources of the provided class and store have been cached
     */
    <T extends ResourceInfo> boolean isResourcesCached(StoreInfo store, Class<T> clazz);

    /**
     * Determines if all layers have been cached
     *
     * @return True if all layers have been cached
     */
    boolean isLayersCached();

    /**
     * Determines if all layers of the provided resource have been cached
     *
     * @param resource
     * @return True if all layers of the provided resource have been cached
     */
    boolean isLayersCached(ResourceInfo resource);

    /**
     * Determines if all layers with the provided style have been cached
     *
     * @param style
     * @return True if all layers with the provided style have been cached
     */
    boolean isLayersCached(StyleInfo style);

    /**
     * Determines if all maps have been cached
     *
     * @return True if all maps have been cached
     */
    boolean isMapsCached();

    /**
     * Determines if all layer groups have been cached
     *
     * @return True if all layer groups have been cached
     */
    boolean isLayerGroupsCached();

    /**
     * Determines if all layer groups with the provided workspace have been cached
     *
     * @param workspace
     * @return True if all layer groups with the provided workspace have been cached
     */
    boolean isLayerGroupsCached(WorkspaceInfo workspace);

    /**
     * Determines if all namespaces have been cached
     *
     * @return True if all namespaces have been cached
     */
    boolean isNamespacesCached();

    /**
     * Determines if all workspaces have been cached
     *
     * @return True if all workspaces have been cached
     */
    boolean isWorkspacesCached();

    /**
     * Determines if all styles have been cached
     *
     * @return True if all styles have been cached
     */
    boolean isStylesCached();

    /**
     * Determines if all styles with the provided workspace have been cached
     *
     * @return True if all styles with the provided workspace have been cached
     */
    boolean isStylesCached(WorkspaceInfo workspace);

    /**
     * Sets whether all stores of the provided class have been cached
     *
     * @param clazz The class of store. One of {@link StoreInfo}, {@link DataStoreInfo}, {@link CoverageStoreInfo}, or {@link WMSStoreInfo}
     * @param cached Indicates if the stores have been cached or not
     */
    <T extends StoreInfo> void setStoresCached(Class<T> clazz, boolean cached);

    /**
     * Sets whether all stores of the provided class and workspace have been cached
     *
     * @param workspace
     * @param clazz The class of store. One of {@link StoreInfo}, {@link DataStoreInfo}, {@link CoverageStoreInfo}, or {@link WMSStoreInfo}
     * @param cached Indicates if the stores have been cached or not
     */
    <T extends StoreInfo> void setStoresCached(WorkspaceInfo workspace, Class<T> clazz, boolean cached);

    /**
     * Sets whether all resources of the provided class have been cached
     *
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @param cached Indicates if the resources have been cached or not
     */
    <T extends ResourceInfo> void setResourcesCached(Class<T> clazz, boolean cached);

    /**
     * Sets whether all resources of the provided class and namespace have been cached
     *
     * @param namespace
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @param cached Indicates if the resources have been cached or not
     */
    <T extends ResourceInfo> void setResourcesCached(NamespaceInfo namespace, Class<T> clazz, boolean cached);

    /**
     * Sets whether all resources of the provided class and store have been cached
     *
     * @param store
     * @param clazz The class of resource. One of {@link ResourceInfo}, {@link FeatureTypeInfo}, {@link CoverageInfo}, or {@link WMSLayerInfo}
     * @param cached Indicates if the resources have been cached or not
     */
    <T extends ResourceInfo> void setResourcesCached(StoreInfo store, Class<T> clazz, boolean cached);

    /**
     * Sets whether all layers have been cached
     *
     * @param cached Indicates if the layers have been cached or not
     */
    void setLayersCached(boolean cached);

    /**
     * Sets whether all layers of the provided resource have been cached
     *
     * @param resource
     * @param cached Indicates if the layers have been cached or not
     */
    void setLayersCached(ResourceInfo resource, boolean cached);

    /**
     * Sets whether all layers with the provided style have been cached
     *
     * @param style
     * @param cached Indicates if the layers have been cached or not
     */
    void setLayersCached(StyleInfo style, boolean cached);

    /**
     * Sets whether all maps have been cached
     *
     * @param cached Indicates if the maps have been cached or not
     */
    void setMapsCached(boolean cached);

    /**
     * Sets whether all layer groups have been cached
     *
     * @param cached Indicates if the layer groups have been cached or not
     */
    void setLayerGroupsCached(boolean cached);

    /**
     * Sets whether all layer groups of the provided workspace have been cached
     *
     * @param workspace
     * @param cached Indicates if the layer groups have been cached or not
     */
    void setLayerGroupsCached(WorkspaceInfo workspace, boolean cached);

    /**
     * Sets whether all namespacess have been cached
     *
     * @param cached Indicates if the namespaces have been cached or not
     */
    void setNamespacesCached(boolean cached);

    /**
     * Sets whether all workspaces have been cached
     *
     * @param cached Indicates if the workspaces have been cached or not
     */
    void setWorkspacesCached(boolean cached);

    /**
     * Sets whether all styles have been cached
     *
     * @param cached Indicates if the styles have been cached or not
     */
    void setStylesCached(boolean cached);

    /**
     * Sets whether all styles of the provided workspace have been cached
     *
     * @param cached Indicates if the styles have been cached or not
     */
    void setStylesCached(WorkspaceInfo workspace, boolean cached);
}
