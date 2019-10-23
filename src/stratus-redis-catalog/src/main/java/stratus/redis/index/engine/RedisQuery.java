/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.convert.RedisConverter;

import java.util.List;

/**
 * Representation of a Redis Query to be executed at some future point
 *
 * @param <T> The type of object returned by the query.
 */
public abstract class RedisQuery<T> implements RedisQueryContainer/*implements Future<T>*/ {

    T responseObject = null;
    boolean isDone = false;

    protected abstract Object execute(RedisConnection connection);
    protected abstract T handleResultInternal(Object result, RedisConverter converter);

    /**
     * If all keys are resolvable, return true.
     *
     * If some keys returned null, return false.
     *
     * @return true if all key values are non null. false otherwise. Also returns false if already executed.
     */
    protected abstract boolean canExecute();

    public T handleResult(Object result, RedisConverter converter) {
        this.responseObject = handleResultInternal(result, converter);
        isDone = true;
        return responseObject;
    }

    public abstract List<? extends RedisQueryKeyLike> getKeys();

    public boolean isDone() {
        return isDone;
    }

    public T get() {
        if (isDone) {
            return responseObject;
        }
        //TODO - properly implement Future
        throw new IllegalStateException("Query not yet completed");
    }
    public RedisQuery<T> getQuery() {
        return this;
    }

    @Override
    public final String toString() {
        return getClass().getTypeName()+"{" +
                "query=\"" + toStringInternal() + "\""+
                ", isDone=" + isDone +
                (isDone ? ", responseObject=" + responseObject : "") +
                '}';
    }

    /**
     * @return The string value of the redis query to be executed, e.g. "HGET key"
     */
    protected abstract String toStringInternal();
}
