/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * @author joshfix
 * Created on 9/28/17
 */
@Configuration
@EnableRedisRepositories(basePackages = {"stratus.redis.catalog.repository",
        "stratus.redis.geoserver.repository"})
public class RedisRepositoriesConfig {}
