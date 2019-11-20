/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import stratus.commons.beanfactory.FilteringBeanDefinitionLoader;
import stratus.config.GeoServerSystemImportResourcesConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Modified version of {@link GeoServerSystemImportResourcesConfig} for use by {@link AbstractRedisCatalogTest}
 */
@Slf4j
@Configuration
//@ComponentScan( basePackages = {"stratus", "org.geoserver.rest"})
       // ,excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = LayerController.class) })
@ImportResource(value={"classpath*:/applicationContext.xml,classpath*:/applicationSecurityContext.xml," +
        "!bean:webDispatcherMappingSystem,!bean:webDispatcherMapping," +
        "!bean:wpsInitializer,!bean:executionManager,!bean:wpsService,!bean:wpsService-1.0.0,!bean:wpsExceptionHandler,!bean:wpsServiceTarget,!bean:wpstimerFactory," +
        "!file:gs-backup-restore,!file:gs-gwc,!file:gs-wps,!file:gs-wps-core,!file:stratus-wps,!file:gs-rest"}, reader = FilteringBeanDefinitionLoader.class)
public class RedisCatalogImportResourcesConfig extends GeoServerSystemImportResourcesConfig {
    private static final List<String> BLACKLIST_PATTERNS = Arrays.asList(
            "gs-backup-restore", "backuprestore", "gs-web", "gs-gwc", "gs-wps", "gs-wps-core", "stratus-wps");

    @Override
    protected List<String> getBlacklistPatterns() {
        return BLACKLIST_PATTERNS;
    }
}
