/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;
import stratus.redis.catalog.RedisCatalogUtils;

/**
 * A {@link RedisDelegatingQueryKey} whose id is static, and whose value is obtained from a {@link RedisValueQuery}
 *
 * @param <K>
 * @param <V>
 */
public class RedisDelegatingValueQueryKey<K extends CatalogInfo, V extends Info> extends RedisDelegatingQueryKey<K, V> {

    String id;

    public RedisDelegatingValueQueryKey(String id, RedisValueQuery<K> delegate, DelegatingKey<K> delegateBehavior, Class<V> clazz) {
        super(delegate, delegateBehavior, clazz);
        this.id = id;
    }

    /**
     * @see AbstractRedisQueryKey
     *
     * @return The key constructed from the {@link #delegate}, or null if the delegate query fails or returns null.
     */
    @Override
    public String getQueryKey() {
        if (id == null || getValue() == null) {
            return null;
        }
        return RedisCatalogUtils.buildKey(getQueryClass(), getId(), getValue());
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return delegateBehavior.get(delegate);
    }

    public RedisValueQuery<K> getQuery() {
        return delegate;
    }
}
