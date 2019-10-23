/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link RedisQuery}, taking one or more keys, and returning a single result.
 *
 * @param <K> Class representing the keyspace of the key(s)
 * @param <V> Class of the expected response object
 */
public abstract class SimpleRedisQuery<K extends Info, V> extends RedisQuery<V> {
    Class<K> clazz;
    List<RedisQueryKey> keys;

    public SimpleRedisQuery(RedisQueryKey<K> key) {
        this.keys = Collections.singletonList(key);
        this.clazz = key.getQueryClass();
    }

    //TODO: Are the keyspaces of the keys required to be the same? If so, add a check and extract clazz from keys
    public SimpleRedisQuery(List<RedisQueryKey> keys, Class<K> clazz) {
        this.keys = keys;
        this.clazz = clazz;
    }

    @Override
    public List<RedisQueryKey> getKeys() {
        return keys;
    }

    @Override
    protected boolean canExecute() {
        if (isDone) {
            return false;
        }
        for (RedisQueryKey key : keys) {
            if (key.getQueryKey() == null) {
                isDone = true;
                responseObject = null;
                return false;
            }
        }
        return true;
    }
}
