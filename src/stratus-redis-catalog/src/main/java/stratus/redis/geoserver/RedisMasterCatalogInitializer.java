/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

/**
 * Extension hook for initializing the redis catalog on initial startup
 *
 * Called by {@link RedisGeoServerLoader} during catalog initialization on the master node only. Is NOT called upon
 * subsequent restarts.
 */
public interface RedisMasterCatalogInitializer {
    void init();
}
