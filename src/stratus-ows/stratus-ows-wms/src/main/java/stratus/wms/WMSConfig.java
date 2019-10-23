/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Created by tbarsballe on 2017-03-09.
 */
@Configuration
@EnableRedisRepositories(basePackages = {"stratus.wms.redis.repository"})
public class WMSConfig {

    @Bean
    public WMSHandler wmsHandler() {
        return new WMSHandler();
    }
}
