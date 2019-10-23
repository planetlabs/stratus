/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.geoserver.info;

import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.wps.redis.repository.WPSInfoRepository;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.WPSInfoImpl;
import org.springframework.stereotype.Component;

/**
 * Registers the {@link WPSInfoRepository} with the {@link org.geoserver.config.GeoServerFacade}
 */
@Component
public class WPSInfoClassRegisteringBean implements ServiceInfoRegisteringBean {

    @Override
    public void register(RedisGeoServerFacade redisGeoServerFacade) {
        redisGeoServerFacade.registerServiceInfoMapping(WPSInfo.class, WPSInfoImpl.class, WPSInfoRedisImpl.class, WPSInfoRepository.class);
    }
}
