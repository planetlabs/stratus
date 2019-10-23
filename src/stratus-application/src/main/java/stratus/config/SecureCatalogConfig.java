/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.security.SecureCatalogImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureCatalogConfig {
    @ConditionalOnProperty(name="auth", havingValue="false", matchIfMissing = true)
    @Bean
    public SecureCatalogImpl secureCatalog(CatalogImpl rawCatalog) throws Exception{
        return new SecureCatalogImpl(rawCatalog);
    }
}
