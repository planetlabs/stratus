/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;

/**
 * Interface for constructing lambdas used by {@link RedisDelegatingQueryKey} to retrieve a query key from a
 * {@link RedisValueQuery} upon completion of the latter query.
 *
 * @param <T>
 */
public interface DelegatingKey<T extends CatalogInfo> {
    String get(RedisValueQuery<T> query);
}
