/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.beanfactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A custom bean definition loader than allows for filtering out certain definitions based on patterns via a custom
 * resource loader.
 * <p>
 * This class is meant to be used as part of @ImportResource annotation, in order to provide a filter on the application
 * contexts that get imported.
 * <p>
 * The major difference in practical usage is that this loader expects the Location (the value of the ImportResource
 * annotation) to be a *comma separated list instead of an array*! Any element in this list that starts with
 * CONTEXT_FILE_EXCLUSION_PREFIX will be used to remove resources loaded from any contex.xml file.  Any element in this
 * list that starts with BEAN_EXCLUSION_PREFIX will prevent an individual bean defined any any loaded resource from
 * being instantiated and managed by spring.
 */
@Slf4j
public class FilteringBeanDefinitionLoader extends XmlBeanDefinitionReader {

    private final FilteringResourceLoader resourceLoader;
    private FilteringBeanDefinitionDocumentReader reader = new FilteringBeanDefinitionDocumentReader();
    private boolean isAdminInstance = false;

    public static final String BEAN_EXCLUSION_PREFIX = "!bean:";
    public static final String CONTEXT_FILE_EXCLUSION_PREFIX = "!file:";
    public static final String BEAN_CLASS_EXCLUSION_PREFIX = "!beanClass:";

    /**
     * Create new XmlBeanDefinitionReader for the given bean factory.
     *
     * @param registry the BeanFactory to load bean definitions into,
     *                 in the form of a BeanDefinitionRegistry
     */
    public FilteringBeanDefinitionLoader(BeanDefinitionRegistry registry) {
        super(registry);
        this.resourceLoader = new FilteringResourceLoader();
        setDocumentReaderClass(FilteringBeanDefinitionDocumentReader.class);

        try {
            Environment env = ((DefaultListableBeanFactory) registry).getBean(Environment.class);
            isAdminInstance = Boolean.valueOf(env.getProperty("stratus.admin-enabled"));
            if (!isAdminInstance) {
                log.info("Initializing Stratus beans with standard configuration.");
            } else {
                log.info("Initializing Stratus beans with admin configuration.");
            }
        } catch (Exception e) {
            log.error("Error getting environment.", e);
        }
    }

    @Override
    protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
        return reader;
    }

    public void parseLocations(String[] patterns) {
        for (String pattern : patterns) {
            pattern = pattern.trim();
            if (pattern.startsWith(BEAN_EXCLUSION_PREFIX)) {
                String bean = pattern.split(BEAN_EXCLUSION_PREFIX)[1];
                reader.addExclusion(bean);
            } else if (pattern.startsWith(BEAN_CLASS_EXCLUSION_PREFIX)) {
                String bean = pattern.split(BEAN_CLASS_EXCLUSION_PREFIX)[1];
                reader.addClassExclusion(bean);
            }
        }
    }

    @Override
    public FilteringResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public static void blacklistAdminDependencies(Set<String> blacklistPatterns) {
        blacklistPatterns.add("gs-backup-restore");
        blacklistPatterns.add("gs-web");
    }

    public static void blacklistGwcDependencies(Set<String> blacklistPatterns) {
        blacklistPatterns.add("gs-gwc");
    }
    public static Set<Resource> filterResources(Set<Resource> resources, Set<String> blackListPatterns) {
        blackListPatterns.forEach(blackListPattern -> resources.removeIf(resource -> {
            try {
                String resourceURI = resource.getURI().toString();
                if (resourceURI.contains(blackListPattern)) {
                    log.info("Blacklist pattern \"" + blackListPattern + "\" " + " matched.  Ignored resource: "
                            + resourceURI);
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                //can't blacklist for some reason, keep it in
                log.warn("Unable to blacklist resource: " + resource, e);
                return false;
            }
        }));

        return resources;
    }

    /**
     * Load resource as normal, and then filter out unwanted ones specified via CONTEXT_FILE_EXCLUSION_PREFIX. In
     * addition, build a list of bean ids prefixed with BEAN_EXCLUSION_PREFIX that will be used by the
     * FilteringBeanDefinitionDocumentReader to prohibit beans of the given id from being instantiated by spring.
     */
    class FilteringResourceLoader extends PathMatchingResourcePatternResolver {

        @Override
        public Resource[] getResources(String locationPattern) throws IOException {
            Set<Resource> resources = new HashSet<>();
            Set<String> blackListPatterns = new HashSet<>();

            blacklistGwcDependencies(blackListPatterns);

            if (!isAdminInstance) {
                System.setProperty("GEOSERVER_CONSOLE_DISABLED", "true");
                blacklistAdminDependencies(blackListPatterns);
            }

            String[] patterns = locationPattern.trim().split(",");
            //Register bean exclusions
            parseLocations(patterns);


            for (String location : patterns) {
                location = location.trim();
                // Filter out exclusion prefixes from resource list
                if (!location.startsWith(BEAN_EXCLUSION_PREFIX)
                        && !location.startsWith(CONTEXT_FILE_EXCLUSION_PREFIX)
                        && !location.startsWith(BEAN_CLASS_EXCLUSION_PREFIX)) {
                    resources.addAll(Arrays.asList(super.getResources(location)));
                // Construct blacklist patterns from file exclusion prefixes
                } else if (location.startsWith(CONTEXT_FILE_EXCLUSION_PREFIX)) {
                    blackListPatterns.add(location.split(CONTEXT_FILE_EXCLUSION_PREFIX)[1]);
                }
            }

            resources = filterResources(resources, blackListPatterns);
            return resources.toArray(new Resource[resources.size()]);
        }
    }

}
