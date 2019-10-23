/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms.redis.repository;

import stratus.wms.redis.geoserver.info.WMSInfoRedisImpl;
import stratus.redis.geoserver.repository.AbstractServiceInfoRepository;

/**
 * Redis repository for {@link WMSInfoRedisImpl}
 */
public interface WMSInfoRepository  extends AbstractServiceInfoRepository<WMSInfoRedisImpl> {

}
