/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.config;

import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.converter.BytesToSerializableConverter;
import stratus.redis.converter.BytesToVirtualTableConverter;
import stratus.redis.converter.SerializableToBytesConverter;
import stratus.redis.converter.VirtualTableToBytesConverter;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.rest.CatalogRestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.Arrays;

/**
 * @author joshfix
 * Created on 11/2/17
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class StratusCatalogConfiguration {

    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(Arrays.asList(
                new BytesToVirtualTableConverter(), new VirtualTableToBytesConverter(),
                new BytesToSerializableConverter(), new SerializableToBytesConverter()));
    }

    @Bean
    public FilterRegistrationBean catalogCachingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(catalogCachingFilter());
        registration.setName("Catalog Caching Filter");
        registration.addUrlPatterns("/*");
        registration.setOrder(6);
        return registration;
    }

    @Bean("restCachingMappedInterceptor")
    @ConditionalOnProperty(prefix = "stratus.catalog.redis.caching", value = "enable-rest-caching", havingValue = "true")
    public MappedInterceptor restCachingMappedInterceptor(RestCachingInterceptor restCachingInterceptor) {
        return new MappedInterceptor(RestCachingInterceptor.PATHS, restCachingInterceptor);
    }

    @Bean("redisManagerMappedInterceptor")
    public MappedInterceptor redisManagerInterceptor(CatalogRestInterceptor catalogRestInterceptor) {
        return new MappedInterceptor(CatalogRestInterceptor.PATHS, catalogRestInterceptor);
    }

    @Bean(name = "CachingFilter")
    public CachingFilter catalogCachingFilter() {
        return new CachingFilter();
    }

    @Bean(name = "LayerIndexListener")
    public LayerIndexListener layerGroupIndexListener() {
        return new LayerIndexListener();
    }

}
