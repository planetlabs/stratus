/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wcs.redis.geoserver.info;

import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.wcs.redis.repository.WCSInfoRepository;
import org.geoserver.wcs.WCSInfo;
import org.geoserver.wcs.WCSInfoImpl;
import org.springframework.stereotype.Component;

/**
 * Registers the {@link WCSInfoRepository} with the {@link org.geoserver.config.GeoServerFacade}
 */
@Component
public class WCSInfoClassRegisteringBean implements ServiceInfoRegisteringBean {

    @Override
    public void register(RedisGeoServerFacade redisGeoServerFacade) {
        redisGeoServerFacade.registerServiceInfoMapping(WCSInfo.class, WCSInfoImpl.class, WCSInfoRedisImpl.class, WCSInfoRepository.class);
    }
}
