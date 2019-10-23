/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Custom HashOperations implementation that can handle serialized objects whose class is no longer available. This
 * can happen if you install a GeoServer extension which adds a custom service, and later remove that extension.
 *
 * Fix for EC-98
 *
 * Created by tbarsballe on 2016-12-13.
 */
@Slf4j
public class StratusHashOperations<K, V, HK, HV> implements HashOperations<K, HK, HV> {

    private HashOperations<K, HK, HV> delegate;
    private RedisTemplate<K, V> template;


    public StratusHashOperations(RedisTemplate<K, V> template, HashOperations<K, HK, HV> delegate) {
        this.template = template;
        this.delegate = delegate;
    }

    /* Overriden methods */

    /**
     * Get entire hash stored at {@code key}.
     *
     * If deserialization of any hash would throw a ClassNotFoundException, a warning is logged and the culprit hash is
     * removed from the Redis repository.
     *
     * @param key must not be {@literal null}.
     * @return
     */
    @Override
    public Map<HK, HV> entries(K key) {
        final byte[] rawKey = rawKey(key);

        Map<byte[], byte[]> entries = execute(connection -> connection.hGetAll(rawKey), true);

        return deserializeHashMap(entries, key);
    }

    /* Modified AbstractOperation methods */

    @SuppressWarnings("unchecked")
    <HK, HV> Map<HK, HV> deserializeHashMap(Map<byte[], byte[]> entries, K key) {
        // connection in pipeline/multi mode
        if (entries == null) {
            return null;
        }

        Map<HK, HV> map = new LinkedHashMap<>(entries.size());

        for (Map.Entry<byte[], byte[]> entry : entries.entrySet()) {
            try {
                map.put(deserializeHashKey(entry.getKey()), deserializeHashValue(entry.getValue()));
            } catch (SerializationException e) {

                Throwable cause = e.getRootCause();
                if (cause instanceof ClassNotFoundException) {
                    HK id = deserializeHashKey(entry.getKey());
                    log.warn("ClassNotFoundException while deserializing. Ignoring culprit hash - KEY: " + key + " ID: " + id , cause);

                    //this.delete(key, id);
                } else {
                    throw e;
                }
            }
        }

        return map;
    }

    /* Duplicated AbstractOperation methods */

    <T> T execute(RedisCallback<T> callback, boolean b) {
        return template.execute(callback, b);
    }

    byte[] rawKey(Object key) {
        Assert.notNull(key, "non null key required");
        if (keySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return keySerializer().serialize(key);
    }

    @SuppressWarnings({ "unchecked" })
    RedisSerializer keySerializer() {
        return template.getKeySerializer();
    }

    @SuppressWarnings({ "unchecked" })
    <HK> HK deserializeHashKey(byte[] value) {
        if (hashKeySerializer() == null) {
            return (HK) value;
        }
        return (HK) hashKeySerializer().deserialize(value);
    }

    @SuppressWarnings("unchecked")
    <HV> HV deserializeHashValue(byte[] value) {
        if (hashValueSerializer() == null) {
            return (HV) value;
        }
        return (HV) hashValueSerializer().deserialize(value);
    }

    RedisSerializer hashKeySerializer() {
        return template.getHashKeySerializer();
    }

    RedisSerializer hashValueSerializer() {
        return template.getHashValueSerializer();
    }

    /* Delegate methods */

    @Override
    public Long delete(K key, Object... hashKeys) {
        return delegate.delete(key, hashKeys);
    }

    @Override
    public Boolean hasKey(K key, Object hashKey) {
        return delegate.hasKey(key, hashKey);
    }

    @Override
    public HV get(K key, Object hashKey) {
        return delegate.get(key, hashKey);
    }

    @Override
    public List<HV> multiGet(K key, Collection<HK> hashKeys) {
        return delegate.multiGet(key, hashKeys);
    }

    @Override
    public Long increment(K key, HK hashKey, long delta) {
        return delegate.increment(key, hashKey, delta);
    }

    @Override
    public Double increment(K key, HK hashKey, double delta) {
        return delegate.increment(key, hashKey, delta);
    }

    @Override
    public Set<HK> keys(K key) {
        return delegate.keys(key);
    }

    @Override
    public Long lengthOfValue(K key, HK hashKey) {
        return delegate.lengthOfValue(key, hashKey);
    }

    @Override
    public Long size(K key) {
        return delegate.size(key);
    }

    @Override
    public void putAll(K key, Map<? extends HK, ? extends HV> m) {
        delegate.putAll(key, m);
    }

    @Override
    public void put(K key, HK hashKey, HV value) {
        delegate.put(key, hashKey, value);
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV value) {
        return delegate.putIfAbsent(key, hashKey, value);
    }

    @Override
    public List<HV> values(K key) {
        return delegate.values(key);
    }

    @Override
    public RedisOperations<K, ?> getOperations() {
        return delegate.getOperations();
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(K key, ScanOptions options) {
        return delegate.scan(key, options);
    }
}
