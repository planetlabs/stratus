/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.gwc.redis.repository.WMTSInfoRepository;
import org.geoserver.gwc.wmts.WMTSInfo;
import org.geoserver.gwc.wmts.WMTSInfoImpl;
import org.springframework.stereotype.Component;

/**
 * Registers the {@link WMTSInfoRepository} with the {@link org.geoserver.config.GeoServerFacade}
 */
@Component
public class WMTSInfoClassRegisteringBean implements ServiceInfoRegisteringBean {

    @Override
    public void register(RedisGeoServerFacade redisGeoServerFacade) {
        redisGeoServerFacade.registerServiceInfoMapping(WMTSInfo.class, WMTSInfoImpl.class, WMTSInfoRedisImpl.class, WMTSInfoRepository.class);
    }
}
