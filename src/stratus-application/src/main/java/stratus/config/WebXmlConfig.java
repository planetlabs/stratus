/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.GeoserverInitStartupListener;
import org.geoserver.filters.*;
import org.geoserver.platform.GeoServerHttpSessionListenerProxy;
import org.geoserver.web.HeaderContribution;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stratus.web.StratusTheme;

import javax.servlet.http.HttpSessionListener;
import java.util.Set;

/**
 * @author Josh Fix
 * @author tingold
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class WebXmlConfig {

    private final StratusConfigProps configProps;

    /* Filters */

    @Bean(name = "FlushSafeFilter")
    public FlushSafeFilter flushSafeFilter() {
        return new FlushSafeFilter();
    }

    @Bean(name = "SessionDebugger")
    public SessionDebugFilter sessionDebugFilter() {
        return new SessionDebugFilter();
    }

    @Bean(name = "Request Logging Filter")
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean(name = "Spring Delegating Filter")
    public SpringDelegatingFilter springDelegatingFilter() {
        return new SpringDelegatingFilter();
    }

    @Bean(name = "Thread locals cleanup filter")
    public ThreadLocalsCleanupFilter threadLocalsCleanupFilter() {
        return new ThreadLocalsCleanupFilter();
    }

    @Bean
    public FilterRegistrationBean flushSafeFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(flushSafeFilter());
        registration.setName("FlushSafeFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean sessionDebugFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(sessionDebugFilter());
        registration.setName("SessionDebugFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean requestLoggingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(loggingFilter());
        registration.setEnabled(configProps.getWeb().isRequestLoggingFilterEnabled());
        registration.addInitParameter("log-request-bodies",
                Boolean.toString(configProps.getWeb().isRequestLoggingFilterLogRequestBodies()));
        registration.setName("Request Logging Filter");
        registration.addUrlPatterns("/*");
        registration.setOrder(3);
        return registration;
    }

    @Bean
    public FilterRegistrationBean springDelegatingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(springDelegatingFilter());
        registration.setName("Spring Delegating Filter");
        registration.addUrlPatterns("/*");
        registration.setOrder(4);
        return registration;
    }

    @Bean
    public FilterRegistrationBean threadLocalsCleanupFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(threadLocalsCleanupFilter());
        registration.setName("Thread locals cleanup filter");
        registration.addUrlPatterns("/*");
        registration.setOrder(5);
        return registration;
    }

    /* Listeners */

    @Bean
    @ConditionalOnMissingBean(GeoserverInitStartupListener.class)
    public GeoserverInitStartupListener geoserverInitStartupListener() {
        return new GeoserverInitStartupListener();
    }

    @Bean
    public GeoServerHttpSessionListenerProxy geoServerHttpSessionListenerProxy() {
        return new GeoServerHttpSessionListenerProxy() {
            /*
             * When configured through web.xml, it appears that this listener is not included in the list of listeners returned.
             * This is not the case for this annotation-based configuration, so we need to explicitly exclude it.
             */
            protected Set<HttpSessionListener> listeners() {
                listeners = super.listeners();
                listeners.remove(this);
                return listeners;
            }
        };
    }

    @Bean
    public HeaderContribution stratusTheme() {
        HeaderContribution themeContribution = new HeaderContribution();
        themeContribution.setScope(StratusTheme.class);

        themeContribution.setCSSFilename("css/stratus.css");
        if (configProps.getWeb().getTheme() != null) {
            String cssFilename = "css/stratus-" + configProps.getWeb().getTheme() + ".css";
            if (StratusTheme.class.getResource(cssFilename) != null) {
                themeContribution.setCSSFilename(cssFilename);
            }
        }
        themeContribution.setFaviconFilename("favicon/favicon.ico");
        return themeContribution;
    }

}