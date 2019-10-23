/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import stratus.redis.geoserver.GeoServerCache;

import java.util.List;

/**
 * Created by aaryno on 7/12/17.
 *
 * Generic Catalog / Configuration caching interface.
 *
 * See {@link GeoServerCache} and
 * {@link CatalogCache}
 */
public interface Cache {

    /**
     * Determines if the result of the passed method call has been saved to the cache. Useful for caching fail-to-finds.
     * Must be set using
     *
     * @param method Name of the method called
     * @param arguments Arguments the method was called with
     * @return If the passed method has already been called
     */
    boolean isCached(String method, List<Object> arguments);

    /**
     * Sets if the result of the passed method call has been saved to the cache. Useful for caching fail-to-finds.
     *
     * @param method Name of the method called
     * @param arguments Arguments the method was called with
     * @param cached Whether or not the method call has been cached
     */
    void setCached(String method, List<Object> arguments, boolean cached);

}
