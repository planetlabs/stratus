/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.repository;

import stratus.wps.redis.geoserver.info.WPSInfoRedisImpl;
import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;

/**
 * Redis repository for {@link WPSInfoRedisImpl}
 */
public interface WPSInfoRepository extends AbstractServiceInfoRepository<WPSInfoRedisImpl> {

}
