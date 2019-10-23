/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link RedisQuery} returning a list of {@link CatalogInfo} objects
 *
 * Represents an series HGET queries. Supports a list of single keys only.
 *
 * @param <T>
 */
public class RedisValuesQuery<T extends Info> extends RedisQuery<List<T>> {

    //TODO: need getQueryClass
    RedisQueryKeysProvider<T> key;
    List<RedisValueQuery<T>> resolvedQueries = null;
    CacheVisitor visitor;

    RedisValuesQuery(RedisQueryKeysProvider<T> key, CacheVisitor visitor) {
        this.key = key;
        this.visitor = visitor;
    }

    public List<RedisValueQuery<T>> getResolvedQueries() {
        if (resolvedQueries == null) {
            resolvedQueries = new ArrayList<>();
            for (String id : key.getKeyIds()) {
                resolvedQueries.add(new RedisValueQuery<>(new DefaultRedisQueryKey<>(key.getQueryClass(), id), new RedisMultiQueryCachingEngine.EmptyCatalogCacheVisitor()));
            }
        }
        return resolvedQueries;
    }

    public CacheVisitor getCacheVisitor() {
        return visitor;
    }

    public void setVisitor(CacheVisitor visitor) {
        this.visitor = visitor;
    }

    @Override
    protected Object execute(RedisConnection connection) {
        List<Object> results = new ArrayList<>();
        for (RedisValueQuery query : getResolvedQueries()) {
            results.add(query.execute(connection));
        }
        return results;
    }

    @Override
    /**
     * While this will work, it is more effective to handle the results of {@link #getResolvedQueries()}
     * individually. See {@link RedisMultiQueryCachingEngine#execute(boolean)}.
     */
    protected List<T> handleResultInternal(Object result, RedisConverter converter) {
        List<T> values = new ArrayList<>();
        List<RedisValueQuery<T>> queries = getResolvedQueries();
        List<Object> results = (List<Object>)result;

        for (int i = 0; i < queries.size(); i++) {
            values.add(queries.get(i).handleResultInternal(results.get(i), converter));
        }
        responseObject = values;
        return values;
    }

    @Override
    /**
     * While this will work, it is more effective to handle the results of {@link #getResolvedQueries()}
     * individually. See {@link RedisMultiQueryCachingEngine#execute(boolean)}.
     */
    public List<T> get() {
        if (responseObject == null) {
            List<T> values = new ArrayList<>();
            for (RedisValueQuery<T> query : getResolvedQueries()) {
                values.add(query.get());
            }
            responseObject = values;
            isDone = true;
        }
        return super.get();
    }

    @Override
    protected boolean canExecute() {
        if (isDone) {
            return false;
        }
        if (key instanceof RedisKeysQuery) {
            return ((RedisKeysQuery)key).isDone();
        }
        if (key instanceof RedisDelegatingQueryKeys) {
            return ((RedisDelegatingQueryKeys)key).getQuery().isDone();
        }
        return true;
    }

    @Override
    public List<? extends RedisQueryKeyLike> getKeys() {
        return Collections.singletonList(key);
    }

    public RedisQueryKeysProvider<T> getKey() {
        return key;
    }

    @Override
    protected String toStringInternal() {
        return getResolvedQueries().toString();
    }

}
