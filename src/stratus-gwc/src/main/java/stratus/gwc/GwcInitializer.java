/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geowebcache.config.ServerConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import stratus.gwc.config.RedisServerConfiguration;
import stratus.redis.geoserver.RedisMasterCatalogInitializer;

/**
 * Performs one-time redis initialization the first time a Stratus instance is started
 */
@Slf4j
@Primary
@Service
@AllArgsConstructor
public class GwcInitializer implements RedisMasterCatalogInitializer {

    private final RedisServerConfiguration serverConfiguration;

    @Override
    public void init() {
        //TODO: Support configuring initial values in applicaiton.yml
        //set up version
        String version = serverConfiguration.getVersion();
        String gwcVersion = ServerConfiguration.class.getPackage().getSpecificationVersion();

        if (version == null) {
            serverConfiguration.setVersion(gwcVersion);
        } else if (gwcVersion != null && !version.equals(gwcVersion)) {
            //Add ServerConfiguration migration code as needed
        }
    }
}
