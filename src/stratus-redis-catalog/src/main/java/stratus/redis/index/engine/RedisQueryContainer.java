/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

/**
 * Represents a query, or a key which depends upon a query.
 * Typically used when resolving nested queries.
 */
public interface RedisQueryContainer {
    RedisQuery getQuery();
}
