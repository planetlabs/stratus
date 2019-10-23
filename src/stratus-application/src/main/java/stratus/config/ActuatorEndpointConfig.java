/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.ActuatorMappingsConfig;

/**
 * @author joshfix
 * Created on 9/29/17
 */
@Configuration
public class ActuatorEndpointConfig {

    @Bean
    public ActuatorMappingsConfig actuatorMappingsConfig() {
        return new ActuatorMappingsConfig();
    }
}
