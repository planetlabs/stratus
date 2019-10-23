/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

/**
 * A Query key where either id or value is itself a key, and may depend upon the result of another query.
 *
 * @param <T>
 */
public class RedisQueryValueQueryKey<T extends Info> extends AbstractRedisQueryKey<T> {

    RedisQueryKey query;
    String id;

    public RedisQueryValueQueryKey(Class<T> clazz, String id, RedisQueryKey value) {
        super(clazz);
        this.id = id;
        query = value;
    }

    public RedisQueryKey getQuery() {
        return query;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getValue() {
        if (query != null) {
            return query.getId();
        } else {
            return null;
        }
    }
}
