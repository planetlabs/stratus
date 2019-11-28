/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wcs.redis.repository;

import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;
import stratus.wcs.redis.geoserver.info.WCSInfoRedisImpl;

/**
 * Redis repository for {@link WCSInfoRedisImpl}
 */
public interface WCSInfoRepository extends AbstractServiceInfoRepository<WCSInfoRedisImpl> {

}
