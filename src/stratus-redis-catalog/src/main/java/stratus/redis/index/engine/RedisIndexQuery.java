/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

/**
 * Special case of {@link RedisKeyQuery}, for getting keys from indices.
 *
 * Represents a GET query.
 * @param <T>
 */
public class RedisIndexQuery<T extends Info> extends RedisKeyQuery<T> {

    public RedisIndexQuery(RedisQueryKey<T> key) {
        super(key);
    }

    @Override
    public Object execute(RedisConnection connection) {
        return connection.get(keys.get(0).getQueryKey().getBytes());
    }

    @Override
    public String handleResultInternal(Object result, RedisConverter converter) {
        byte[] value = (byte[]) result;
        return value == null ? null : new String(value);
    }

    @Override
    protected String toStringInternal() {
        return "GET " + getQueryKey();
    }
}
