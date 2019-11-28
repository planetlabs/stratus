/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import stratus.commons.beanfactory.FilteringBeanDefinitionLoader;

/**
 * Simplified version of {@link GeoServerSystemImportResourcesConfig} for use with catalog requests with less strict
 * context requirements
 *
 * TODO: Can we replace this with the other one...?
 *
 * @author joshfix
 * Created on 9/28/17
 */
@Configuration
@ImportResource(value={"classpath*:/applicationContext.xml,classpath*:/applicationSecurityContext.xml,!file:gs-backup-restore"}, reader = FilteringBeanDefinitionLoader.class)
public class SimpleImportResourcesConfig {
}
