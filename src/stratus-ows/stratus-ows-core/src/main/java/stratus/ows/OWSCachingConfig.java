/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tbarsballe on 2017-03-09.
 */
@Configuration
public class OWSCachingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "stratus.catalog.redis.caching", value = "enable-ows-caching", havingValue = "true")
    public OWSCachingCallback owsCachingCallback() { return new OWSCachingCallback(); }

    @Bean
    public OWSVirtualServiceCallback owsVirtualServiceCallback() { return new OWSVirtualServiceCallback(); }

}
