/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

/**
 * An object that is a {@link RedisQueryKey}, or similar to one (such as a list of keys)
 *
 * @param <T> Class or Interface of Object returned by the associated query. Also represents the keyspace of the query
 */
public interface RedisQueryKeyLike<T extends Info> {
    Class<T> getQueryClass();
}
