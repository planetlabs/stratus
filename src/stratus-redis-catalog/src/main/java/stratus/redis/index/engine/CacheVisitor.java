/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import stratus.redis.cache.Cache;

/**
 * Used to construct lambdas which apply the value of a redis query to the catalog cache
 */
public interface CacheVisitor<T extends Cache> {
    void apply(T cache, RedisQuery<?> value);
}
