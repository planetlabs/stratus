/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.redis.geoserver.info;

import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.wfs.redis.repository.WFSInfoRepository;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.WFSInfoImpl;
import org.springframework.stereotype.Component;

/**
 * Registers the {@link WFSInfoRepository} with the {@link org.geoserver.config.GeoServerFacade}
 */
@Component
public class WFSInfoClassRegisteringBean implements ServiceInfoRegisteringBean {

    @Override
    public void register(RedisGeoServerFacade redisGeoServerFacade) {
        redisGeoServerFacade.registerServiceInfoMapping(WFSInfo.class, WFSInfoImpl.class, WFSInfoRedisImpl.class, WFSInfoRepository.class);
    }
}
