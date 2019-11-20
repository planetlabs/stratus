/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.repository;

import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;
import stratus.wps.redis.geoserver.info.WPSInfoRedisImpl;

/**
 * Redis repository for {@link WPSInfoRedisImpl}
 */
public interface WPSInfoRepository extends AbstractServiceInfoRepository<WPSInfoRedisImpl> {

}
