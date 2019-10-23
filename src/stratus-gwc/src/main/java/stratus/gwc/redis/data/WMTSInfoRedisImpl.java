/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;
import lombok.Data;
import org.geoserver.config.ServiceInfo;
import org.geoserver.gwc.wmts.WMTSInfo;
import org.springframework.data.redis.core.RedisHash;

/**
 * Implementation of {@link WMTSInfo} used for serialization to Redis
 */
@Data
@RedisHash("WMTSInfo")
public class WMTSInfoRedisImpl extends AbstractServiceInfoRedisImpl implements ServiceInfo, WMTSInfo {

}
