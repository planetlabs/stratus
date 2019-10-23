/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;
import org.springframework.data.redis.connection.RedisConnection;

/**
 * Special case of {@link RedisKeysQuery} using a SMEMBERS instead of a SINTER
 * @param <T>
 */
public class RedisMembersQuery<T extends Info> extends RedisKeysQuery<T> {

    public RedisMembersQuery(RedisQueryKey<T> key) {
        super(key);
    }
    @Override
    public Object execute(RedisConnection connection) {
        return connection.sMembers(keys.get(0).getQueryKey().getBytes());
    }

    @Override
    protected String toStringInternal() {
        StringBuilder query = new StringBuilder("SMEMBERS");
        for (int i = 0; i < keys.size(); i++) {
            query.append(" ").append(keys.get(i).getQueryKey());
        }
        return query.toString();
    }
}
