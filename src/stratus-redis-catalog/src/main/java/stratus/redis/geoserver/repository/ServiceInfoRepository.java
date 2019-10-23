/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;


import stratus.redis.geoserver.info.ServiceInfoRedisImpl;

/**
 * Default implementation of {@link AbstractServiceInfoRepository}
 *
 * Used to store generic {@link ServiceInfoRedisImpl} objects in Redis
 */
public interface ServiceInfoRepository extends AbstractServiceInfoRepository<ServiceInfoRedisImpl> {

}
