/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import stratus.redis.config.RedisTemplateConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author joshfix
 * Created on 11/3/17
 */

@Configuration
@EnableRedisHttpSession
@ConditionalOnProperty(prefix = "stratus.web", name = "enable-redis-sessions", havingValue = "true")
@AutoConfigureAfter({RedisTemplateConfig.class})
public class RedisHttpSessionConfig {}
