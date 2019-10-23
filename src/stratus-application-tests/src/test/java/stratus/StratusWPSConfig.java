/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.wps.WPSXStreamLoader;
import org.geoserver.wps.executor.DefaultProcessManager;
import org.geoserver.wps.executor.ProcessStatusTracker;
import org.geoserver.wps.executor.WPSExecutionManager;
import org.geoserver.wps.resource.WPSResourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mimics gs-wps-core applicationContext.xml to get around a circular dependency issue during test context initialization
 *
 * Note: gs-wps-core is excluded in {@link StratusApplicationImportResourcesConfig}
 */
@Configuration
public class StratusWPSConfig {

    @Bean
    public WPSResourceManager wpsResourceManager() {
        return new WPSResourceManager();
    }

    @Bean
    public WPSExecutionManager wpsExecutionManager(GeoServer geoServer, WPSResourceManager wpsResourceManager, ProcessStatusTracker processStatusTracker) {
        return new WPSExecutionManager(geoServer, wpsResourceManager, processStatusTracker);
    }

    @Bean
    public DefaultProcessManager defaultProcessManager(WPSResourceManager wpsResourceManager) {
        return new DefaultProcessManager(wpsResourceManager);
    }

    @Bean
    public WPSXStreamLoader wpsServiceLoader(GeoServerResourceLoader resourceloader) {
        return new WPSXStreamLoader(resourceloader);
    }
}
