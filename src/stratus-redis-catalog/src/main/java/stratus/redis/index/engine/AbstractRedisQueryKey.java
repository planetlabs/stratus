/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;
import stratus.redis.catalog.RedisCatalogUtils;

/**
 * Default implementations for {@link RedisQueryKey}
 *
 * @param <T> Class of object returned by queries with this key, also used as Keyspace
 */
public abstract class AbstractRedisQueryKey<T extends Info> implements RedisQueryKey<T> {
    Class<T> clazz;

    public AbstractRedisQueryKey(Class<T> clazz) {
        this.clazz = clazz;
    }
    public String getQueryKey() {
        return RedisCatalogUtils.buildKey(getQueryClass(), getId(), getValue());
    }

    public Class<T> getQueryClass() {
        return clazz;
    }

    public String getKeyspace() {
        return clazz.getSimpleName();
    }
}
