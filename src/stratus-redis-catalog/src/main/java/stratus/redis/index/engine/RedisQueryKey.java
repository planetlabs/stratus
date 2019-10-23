/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

/**
 * A key for a {@link RedisQuery}
 *
 * In redis, keys have the form {keyspace}:{id}:{value}
 *
 * {id} and {value} are optional.
 * {keyspace} is typically the class or interface name of the value returned by a query with that key.
 *
 * @param <T> Class or Interface of Object returned by the associated query. Also represents the keyspace of the query
 */
public interface RedisQueryKey<T extends Info> extends RedisQueryKeyLike<T> {
    String getQueryKey();

    Class<T> getQueryClass();

    String getKeyspace();

    String getId();

    String getValue();
}
