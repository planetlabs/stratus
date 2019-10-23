/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import stratus.redis.catalog.RedisCatalogUtils;
import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.List;
import java.util.Set;

/**
 * {@link RedisQuery} returning a redis key.
 *
 * Represents an SINTER query. Supports multiple keys, as long as the keyspace is shared (?).
 *
 * @param <T> Class of the object the key belongs to
 */
public class RedisKeyQuery<T extends Info> extends SimpleRedisQuery<T, String> implements RedisQueryKey<T> {

    public RedisKeyQuery(RedisQueryKey<T> key) {
        super(key);
    }

    //TODO: Are the keyspaces of the keys required to be the same? If so, add a check and extract clazz from keys
    public RedisKeyQuery(List<RedisQueryKey> keys, Class<T> clazz) {
        super(keys, clazz);
    }

    @Override
    public String getQueryKey() {
        if (!isDone()) {
            return null;
        }
        if (getId() == null) {
            return null;
        }
        return RedisCatalogUtils.buildKey(clazz, getId(), null);
    }

    @Override
    public Class<T> getQueryClass() {
        return clazz;
    }

    @Override
    public String getKeyspace() {
        return clazz.getName();
    }

    @Override
    public String getId() {
        return get();
    }

    @Override
    public String getValue() {
        return null;
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
    public String handleResultInternal(Object result, RedisConverter converter) {
        Set<byte[]> values = (Set<byte[]>) result;
        if (values != null && values.size() > 0) {
            return new String(values.iterator().next());
        }
        return null;
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
