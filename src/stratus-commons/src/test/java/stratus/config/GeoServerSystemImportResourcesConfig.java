/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import stratus.commons.beanfactory.FilteringBeanDefinitionLoader;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GeoServerSystemTestSupport loads all GeoServer application context xml files by default.  Our FilteringBeanDefinitionLoader
 * has no effect on the context it builds for itself.  The presence of the backup/restore dependencies seem to break
 * the tests, so we need a way to exclude them.
 *
 * @author joshfix
 * Created on 9/28/17
 */
@Slf4j
@Configuration
@ImportResource(value={"classpath*:/applicationContext.xml,classpath*:/applicationSecurityContext.xml,!bean:webDispatcherMappingSystem,!bean:webDispatcherMapping,!file:gs-backup-restore,!file:gs-gwc,!file:gs-wps"}, reader = FilteringBeanDefinitionLoader.class)
public class GeoServerSystemImportResourcesConfig {

    private static final List<String> BLACKLIST_PATTERNS = Arrays.asList("gs-backup-restore", "backuprestore", "gs-web", "gs-gwc", "gs-wps");

    protected List<String> getBlacklistPatterns() {
        return BLACKLIST_PATTERNS;
    }

    /**
     * GeoServerSystemTestSupport loads all GeoServer application context xml files by default.  Our FilteringBeanDefinitionLoader
     * has no effect on the context it builds for itself.  The presence of the backup/restore dependencies seem to break
     * the tests, so we need a way to exclude them.
     * @param springContextLocations The list of context locations that will be provided to spring
     * @param springContextLocationsToFilter The list of context locations to filter (matching locations will be added to springContextLocations)
     */
    public void buildFilteredApplicationContextXmlResourceList(List<String> springContextLocations, List<String> springContextLocationsToFilter) {
        ClassLoader cl = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);

        Set<Resource> resources = new HashSet<>();
        try {
            for (String location : springContextLocationsToFilter) {
                resources.addAll(Arrays.asList(resolver.getResources(location)));
            }
        } catch (IOException e) {
            log.error("Error locating application context resources", e);
        }
        Set<String> blackListPatterns = new HashSet<>();
        blackListPatterns.addAll(getBlacklistPatterns());

        resources = FilteringBeanDefinitionLoader.filterResources(resources, blackListPatterns);

        Set<String> clearedLocations = new HashSet<>();
        resources.forEach(resource -> getBlacklistPatterns().forEach(pattern -> {
            try {
                String uri = resource.getURI().toString();
                if (!uri.contains(pattern)) {
                    clearedLocations.add(uri);
                }
            } catch (IOException e) {
                log.error("Error obtaining URI from classpath resource. ", e);
            }
        }));
        springContextLocations.addAll(clearedLocations);
    }

    /**
     * {@link #buildFilteredApplicationContextXmlResourceList(List)} with default include/exclude paths.
     *
     * @param springContextLocations
     */
    public void buildFilteredApplicationContextXmlResourceList(List<String> springContextLocations) {
        buildFilteredApplicationContextXmlResourceList(springContextLocations, Arrays.asList("classpath*:/applicationContext.xml", "classpath*:/applicationSecurityContext.xml"));
    }

}
