/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

/**
 * Variation of {@link RedisValueQuery} representing an already known value.
 * Intended to be used in cases where a value is known, but it still needs to be included in a query chain
 *
 * @param <T>
 */
public class RedisStaticValueQuery<T extends CatalogInfo> extends RedisValueQuery<T> {
    T value;
    public RedisStaticValueQuery(T value, RedisQueryKey<T> key, CacheVisitor visitor) {
        super(key, visitor);
        this.value = value;
    }

    @Override
    public boolean isDone() {
        return true;
    }
    @Override
    public T get() {
        return value;
    }
    @Override
    public Object execute(RedisConnection connection) {
        return null;
    }

    @Override
    public T handleResultInternal(Object result, RedisConverter converter) {
        return value;
    }

}
