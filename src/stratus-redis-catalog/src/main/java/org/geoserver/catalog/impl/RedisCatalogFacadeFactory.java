/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;

import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.catalog.RedisCatalogFacade;
import org.geoserver.catalog.CatalogFacade;

public class RedisCatalogFacadeFactory {
    /**
     * Given a configured {@link RedisCatalogFacade}, wraps the facade in {@link IsolatedCatalogFacade}
     * and {@link CachingCatalogFacade} and returns it.
     *
     * @param catalog The {@link CatalogImpl}
     * @param facade The redis catalog facade
     * @return The wrapped catalog facade
     */
    public static CatalogFacade create(CatalogImpl catalog, RedisCatalogFacade facade) {
        return new CachingCatalogFacade(catalog, new IsolatedCatalogFacade(facade));
    }
}
