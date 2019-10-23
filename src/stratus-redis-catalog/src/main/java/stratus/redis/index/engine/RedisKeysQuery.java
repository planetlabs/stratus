/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Variation of {@link RedisKeyQuery} which can return multiple keys
 */
public class RedisKeysQuery<T extends Info> extends SimpleRedisQuery<T, List<String>> implements RedisQueryKeysProvider<T> {

    public RedisKeysQuery(RedisQueryKey<T> key) {
        super(key);
    }

    public RedisKeysQuery(List<RedisQueryKey> keys, Class<T> clazz) {
        super(keys, clazz);
    }

    @Override
    public Object execute(RedisConnection connection) {
        byte[][] keyBytes = new byte[keys.size()][];
        for (int i = 0; i < keys.size(); i++) {
            keyBytes[i] = keys.get(i).getQueryKey().getBytes();
        }
        return connection.sInter(keyBytes);
    }

    @Override
    public List<String> handleResultInternal(Object result, RedisConverter converter) {
        Set<byte[]> values = (Set<byte[]>) result;

        if (values != null) {
            List<String> results = new ArrayList<>();
            for (byte[] value : values) {
                results.add(new String(value));
            }
            return results;
        }
        return null;
    }

    @Override
    public Class<T> getQueryClass() {
        return clazz;
    }

    @Override
    public List<String> getKeyIds() {
        return get();
    }

    @Override
    protected String toStringInternal() {
        StringBuilder query = new StringBuilder("SINTER");
        for (int i = 0; i < keys.size(); i++) {
            query.append(" ").append(keys.get(i).getQueryKey());
        }
        return query.toString();
    }
}
