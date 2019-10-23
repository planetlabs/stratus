/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Variation of {@link RedisKeysQuery} which accepts a RedisQueryKeysProvider
 */
public class RedisKeysFromValuesQuery<T extends Info> extends RedisQuery<List<String>> implements RedisQueryKeysProvider<T> {

    RedisQueryKeysProvider<T> key;
    Class<T> clazz;
    List<RedisKeyQuery> resolvedQueries = null;

    RedisKeysFromValuesQuery(RedisQueryKeysProvider<T> key, Class<T> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    public List<RedisKeyQuery> getResolvedQueries() {
        if (resolvedQueries == null) {
            resolvedQueries = new ArrayList<>();
            for (String id : key.getKeyIds()) {
                resolvedQueries.add(new RedisKeyQuery<>(new DefaultRedisQueryKey<>(clazz, id)));
            }
        }
        return resolvedQueries;
    }

    @Override
    public Object execute(RedisConnection connection) {
        List<Object> results = new ArrayList<>();
        for (RedisKeyQuery query : getResolvedQueries()) {
            results.add(query.execute(connection));
        }
        return results;
    }

    @Override
    /**
     * While this will work, it is more effective to handle the results of {@link #getResolvedQueries()}
     * individually. See {@link RedisMultiQueryCachingEngine#execute(boolean)}.
     */
    protected List<String> handleResultInternal(Object result, RedisConverter converter) {
        List<String> values = new ArrayList<>();
        List<RedisKeyQuery> queries = getResolvedQueries();
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
    public List<String> get() {
        if (responseObject == null) {
            List<String> values = new ArrayList<>();
            for (RedisKeyQuery<T> query : getResolvedQueries()) {
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

    @Override
    public List<String> getKeyIds() {
        return get();
    }

    @Override
    public Class<T> getQueryClass() {
        return clazz;
    }

    @Override
    protected String toStringInternal() {
        return resolvedQueries.toString();
    }
}
