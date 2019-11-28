/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServerFacade;
import org.geoserver.config.ServiceInfo;
import stratus.redis.cache.Cache;

/**
 * Extension of {@link GeoServerFacade} intended to support caching.
 *
 * Provides utilities for determining if the cache/delegate has been queried for certain values. This allows caching of
 * fail-to-finds.
 */
public interface GeoServerCache extends GeoServerFacade, Cache {

    /**
     * Determines if all global services have been cached
     *
     * @return True if all global services have been cached
     */
    <T extends ServiceInfo> boolean isServicesCached();

    /**
     * Determines if all services of the provided workspace have been cached
     *
     * @param workspace
     * @return True if all services of the provided workspace have been cached
     */
    <T extends ServiceInfo> boolean isServicesCached(WorkspaceInfo workspace);

    /**
     * Sets whether all global services have been cached
     *
     * @param cached Indicates if the services have been cached or not
     */
    <T extends ServiceInfo> void setServicesCached(boolean cached);

    /**
     * Sets whether all services of the provided workspace have been cached
     *
     * @param workspace
     * @param cached Indicates if the services have been cached or not
     */
    <T extends ServiceInfo> void setServicesCached(WorkspaceInfo workspace, boolean cached);
}
