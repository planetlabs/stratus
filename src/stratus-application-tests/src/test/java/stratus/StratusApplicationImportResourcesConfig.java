/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import stratus.commons.beanfactory.FilteringBeanDefinitionLoader;
import stratus.config.GeoServerSystemImportResourcesConfig;

/**
 * Modified version of {@link GeoServerSystemImportResourcesConfig} modified for use by {@link StratusApplicationTestSupport}
 */
@Slf4j
@Configuration
//@ComponentScan( basePackages = {"stratus", "org.geoserver.rest"})
       // ,excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = LayerController.class) })
@ImportResource(value={"classpath*:/applicationContext.xml,classpath*:/applicationSecurityContext.xml," +
        "!bean:webDispatcherMappingSystem,!bean:webDispatcherMapping,!bean:secureCatalog,!bean:wpsInitializer,!bean:wpstimerFactory," +
        "!file:gs-backup-restore,!file:gs-gwc,!file:gs-rest"}, reader = FilteringBeanDefinitionLoader.class)
public class StratusApplicationImportResourcesConfig extends GeoServerSystemImportResourcesConfig {
}
