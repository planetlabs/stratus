/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

/**
 * Interface for classes which registers an {@link stratus.redis.geoserver.repository.AbstractServiceInfoRepository}
 * and the corresponding {@link stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl} with the
 * {@link RedisGeoServerFacade}.
 */
public interface ServiceInfoRegisteringBean {
    /**
     * Calls {@link RedisGeoServerFacade#registerServiceInfoMapping(Class, Class, Class, Class)} on the provided facade
     * to register class mappings for upstream ServiceInfo Redis repositories
     * @param geoServerFacade
     */
    void register(RedisGeoServerFacade geoServerFacade);
}
