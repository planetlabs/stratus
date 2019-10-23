/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.repository;

import stratus.gwc.redis.data.WMTSInfoRedisImpl;
import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;

/**
 * Redis repository for {@link WMTSInfoRedisImpl}
 */
public interface WMTSInfoRepository extends AbstractServiceInfoRepository<WMTSInfoRedisImpl> {

}
