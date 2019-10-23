/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

/**
 * A static query key with no id or value, just a keyspace. Used for simple SMEMBERS queries
 *
 * @param <T>
 */
public class DefaultRedisMembersKey<T extends Info> extends DefaultRedisQueryKey<T> {

    public DefaultRedisMembersKey(Class<T> clazz) {
        super(clazz, null);
    }
    @Override
    public String getQueryKey() {
        return getKeyspace();
    }
}
