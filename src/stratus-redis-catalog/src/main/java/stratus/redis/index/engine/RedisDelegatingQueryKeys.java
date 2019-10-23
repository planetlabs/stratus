/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.LayerGroupInfo;

import java.util.List;

/**
 * A list of query keys that is generated from the result of a {@link RedisValuesQuery}. For example, the layer ids
 * {@link LayerGroupInfo} query.
 *
 * @param <K> Class representing the value of the {@link RedisValuesQuery} which the key is generated from.
 * @param <V> Class representing the keyspace of these keys.
 */
public class RedisDelegatingQueryKeys<K extends CatalogInfo, V extends CatalogInfo> implements RedisQueryKeysProvider<V>, RedisQueryContainer {

    Class<V> clazz;
    RedisValuesQuery<K> delegate;
    DelegatingKeys<K> delegateBehavior;

    public RedisDelegatingQueryKeys(RedisValuesQuery<K> delegate, DelegatingKeys<K> delegateBehavior, Class<V> clazz) {
        this.clazz = clazz;
        this.delegate = delegate;
        this.delegateBehavior = delegateBehavior;
    }

    @Override
    public Class<V> getQueryClass() {
        return clazz;
    }

    @Override
    public List<String> getKeyIds() {
        return delegateBehavior.get(delegate);
    }

    public RedisValuesQuery<K> getQuery() {
        return delegate;
    }
}
