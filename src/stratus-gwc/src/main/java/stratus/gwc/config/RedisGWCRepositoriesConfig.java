/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Instantiates the repositories defined at {@link stratus.gwc.redis.repository}
 */
@Configuration
@EnableRedisRepositories(basePackages = {"stratus.gwc.redis.repository"})
public class RedisGWCRepositoriesConfig {
}
