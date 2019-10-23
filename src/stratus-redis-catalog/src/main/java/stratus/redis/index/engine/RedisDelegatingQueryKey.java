/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;
import org.geoserver.catalog.LayerInfo;

/**
 * A query key that is generated from the result of a {@link RedisValueQuery}. For example, the resource id of a
 * {@link LayerInfo} query.
 *
 * @param <K> Class representing the value of the {@link RedisValueQuery} which the key is generated from.
 * @param <V> Class representing the keyspace of this key.
 */
public abstract class RedisDelegatingQueryKey<K extends CatalogInfo, V extends Info> extends AbstractRedisQueryKey<V> implements RedisQueryContainer {

    RedisValueQuery<K> delegate;
    DelegatingKey<K> delegateBehavior;

    public RedisDelegatingQueryKey(RedisValueQuery<K> delegate, DelegatingKey<K> delegateBehavior, Class<V> clazz) {
        super(clazz);
        this.delegate = delegate;
        this.delegateBehavior = delegateBehavior;
    }

    public RedisValueQuery<K> getQuery() {
        return delegate;
    }
}
