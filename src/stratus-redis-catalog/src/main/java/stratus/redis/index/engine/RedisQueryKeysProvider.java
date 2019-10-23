/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.geoserver.catalog.Info;

import java.util.List;

/**
 * A variation of {@link RedisQueryKeyLike} representing something that provides a list of keys
 * @param <T>
 */
public interface RedisQueryKeysProvider<T extends Info> extends RedisQueryKeyLike<T> {
    List<String> getKeyIds();
}
