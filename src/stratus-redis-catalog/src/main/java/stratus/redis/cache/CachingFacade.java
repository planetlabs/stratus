/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import org.geoserver.catalog.Info;
import stratus.redis.geoserver.CachingGeoServerFacade;

/**
 * Generic CachingFacade interface, defining some basic shared utility methods for
 * {@link CachingCatalogFacade} and
 * {@link CachingGeoServerFacade}
 *
 * Provides utilities for determining if the cache/delegate has been queried for certain values. This allows caching of
 * fail-to-finds.
 */
public interface CachingFacade<T extends Cache, I extends Info> {
    T getCache();

    void loadCache(Iterable<I> infos);

    void loadCache(Iterable<I> infos, boolean isComplete);
}
