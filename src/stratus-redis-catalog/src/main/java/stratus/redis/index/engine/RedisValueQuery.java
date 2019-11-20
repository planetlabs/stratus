/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.core.convert.RedisData;
import stratus.redis.catalog.impl.CatalogInfoConvert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * {@link RedisQuery} returning a {@link Info} object
 *
 * Represents an HGET. Supports a single key only.
 *
 * @param <T>
 */
public class RedisValueQuery<T extends Info> extends RedisQuery<T> {

    CacheVisitor visitor;
    RedisQueryKey key;

    public RedisValueQuery(RedisQueryKey key, CacheVisitor visitor) {
        this.key = key;
        this.visitor = visitor;
    }

    public RedisQueryKey<T> getKey() {
        return key;
    }

    public CacheVisitor getCacheVisitor() {
        return visitor;
    }

    public void setVisitor(CacheVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    public Object execute(RedisConnection connection) {
        Object result = connection.hGetAll(getKey().getQueryKey().getBytes());
        connection.close();
        return result;
    }

    @Override
    public T handleResultInternal(Object result, RedisConverter converter) {
        Map<byte[], byte[]> value = (Map<byte[], byte[]>) result;
        if (value.size() > 0) {
            RedisData data = new RedisData(value);
            data.setId(getKey().getId());
            data.setKeyspace(getKey().getKeyspace());

            Object info=converter.read(getKey().getQueryClass(), data);
            //Deserialize to redis info and convert to vanilla info
            if (info instanceof CatalogInfo) {
                return CatalogInfoConvert.toTraditional((CatalogInfo) info);
            } else {
                return (T) info;
            }
        }
        return null;
    }

    @Override
    protected boolean canExecute() {
        if (isDone()) {
            return false;
        }
        if (key.getQueryKey() == null) {
            isDone = true;
            responseObject = null;
            return false;
        }
        return true;
    }

    @Override
    public List<? extends RedisQueryKeyLike> getKeys() {
        return Collections.singletonList(key);
    }

    @Override
    protected String toStringInternal() {
        return "HGETALL " + key.getQueryKey();
    }
}
