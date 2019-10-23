/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import org.geoserver.security.GeoServerSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.accept.ContentNegotiationStrategy;

/**
 * Overrides {@link org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration.DefaultConfigurerAdapter}
 * in order to specify which content negotiation strategy to use
 */
@Configuration
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class StratusRestSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    GeoServerSecurityManager manager;

    //Add a qualifier so the right strategy is used
    @Autowired(required = false)
    public void setContentNegotationStrategy(@Qualifier("mvcContentNegotiationManager")
                                                     ContentNegotiationStrategy contentNegotiationStrategy) {
        super.setContentNegotationStrategy(contentNegotiationStrategy);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable();
        http.anonymous().disable();
        http.csrf().disable();
        //Use header defaults, except allow same origin in frames so that the OL preview works
        http.headers()
                .cacheControl().and()
                .contentTypeOptions().and()
                .frameOptions().sameOrigin()
                .httpStrictTransportSecurity().and()
                .xssProtection();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return manager.authenticationManager();
    }
}
