/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

/**
 * @author joshfix
 * Created on 2/27/19
 */
@Configuration
public class DispatcherServletConfig {

    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistration(
            DispatcherServlet dispatcherServlet, ObjectProvider<MultipartConfigElement> multipartConfig) {
        DispatcherServletRegistrationBean registration =
                new DispatcherServletRegistrationBean(dispatcherServlet, "/*");
        registration.setName("dispatcherServlet");
        registration.setLoadOnStartup(-1);
        multipartConfig.ifAvailable(registration::setMultipartConfig);
        return registration;
    }
}
