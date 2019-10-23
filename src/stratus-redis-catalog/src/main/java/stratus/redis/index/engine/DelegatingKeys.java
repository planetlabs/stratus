/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;

import java.util.List;

/**
 * Interface for constructing lambdas used by {@link RedisDelegatingQueryKeys} to retrieve a list of query keys from
 * a {@link RedisValuesQuery} upon completion of the latter query.
 *
 * @param <T>
 */
public interface DelegatingKeys<T extends CatalogInfo> {
    List<String> get(RedisValuesQuery<T> query);
}
