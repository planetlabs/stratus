/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms.redis.geoserver.info;

import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.wms.redis.repository.WMSInfoRepository;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WMSInfoImpl;
import org.springframework.stereotype.Component;

/**
 * Registers the {@link WMSInfoRepository} with the {@link org.geoserver.config.GeoServerFacade}
 */
@Component
public class WMSInfoClassRegisteringBean implements ServiceInfoRegisteringBean {

    @Override
    public void register(RedisGeoServerFacade redisGeoServerFacade) {
        redisGeoServerFacade.registerServiceInfoMapping(WMSInfo.class, WMSInfoImpl.class, WMSInfoRedisImpl.class, WMSInfoRepository.class);
    }
}
