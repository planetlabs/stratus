/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.platform.resource.ResourceNotificationDispatcher;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.CachingFilter;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.repository.RedisRepository;
import stratus.redis.store.RedisNotificationDispatcher;

/**
 * Configuration for a Stratus instance with an embedded
 * {@link RedisRepository}
 *
 * Created by tbarsballe on 2016-12-08.
 */
@Slf4j
@Configuration
@EnableRedisRepositories(basePackages = {"stratus.redis.catalog.repository",
        "stratus.redis.geoserver.repository"})
public class GeoServerWithEmbeddedRedisConfig extends EmbeddedRedisConfig {

    @Bean(name = "CachingFilter")
    public CachingFilter catalogCachingFilter() {
        return new CachingFilter();
    }

    @Bean
    public FilterRegistrationBean catalogCachingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(catalogCachingFilter());
        registration.setName("Catalog Caching Filter");
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean(name = "LayerIndexListener")
    public LayerIndexListener layerGroupIndexListener() {
        return new LayerIndexListener();
    }


    @Bean
    public ResourceNotificationDispatcher resourceNotificationDispatcher() {
        return new RedisNotificationDispatcher();
    }

    @Bean
    public RedisFacadeTestSupport redisFacadeTestSupport() {
        return new RedisFacadeTestSupport();
    }
}
