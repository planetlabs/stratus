/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

/**
 * A simple, static query key.
 * @param <T>
 */
public class DefaultRedisQueryKey<T extends Info> extends AbstractRedisQueryKey<T> {
    String id;
    String value;

    public DefaultRedisQueryKey(Class<T> clazz, String id) {
        this(clazz, id, null);
    }

    public DefaultRedisQueryKey(Class<T> clazz, String id, String value) {
        super(clazz);
        this.id = id;
        this.value = value;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getValue() {
        return value;
    }
}
