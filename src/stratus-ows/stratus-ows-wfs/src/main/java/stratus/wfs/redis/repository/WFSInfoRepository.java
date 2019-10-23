/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.redis.repository;

import stratus.wfs.redis.geoserver.info.WFSInfoRedisImpl;
import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;

/**
 * Redis repository for {@link WFSInfoRedisImpl}
 */
public interface WFSInfoRepository extends AbstractServiceInfoRepository<WFSInfoRedisImpl> {

}
