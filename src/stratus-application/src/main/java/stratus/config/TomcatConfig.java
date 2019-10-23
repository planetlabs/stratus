/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by joshfix on 09/01/17.
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class TomcatConfig {

    private final StratusConfigProps configProps;

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            public TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                // only enable naming if we have JNDI sources
                if (!configProps.getJndi().getSources().isEmpty()) {
                    tomcat.enableNaming();
                }
                // configure JNDI
                if (!configProps.getJndi().getSources().isEmpty()) {
                    for (Container child : tomcat.getHost().findChildren()) {
                        if (child instanceof Context) {
                            ClassLoader contextClassLoader = ((Context) child).getLoader().getClassLoader();
                            Thread.currentThread().setContextClassLoader(contextClassLoader);
                            break;
                        }
                    }
                }
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                // set whether or not the Location header should use relative or absolute URIs for redirects
                context.setUseRelativeRedirects(configProps.getTomcat().isUseRelativeRedirects());

                // configure JNDI
                if (!configProps.getJndi().getSources().isEmpty()) {
                    configProps.getJndi().getSources().forEach(source -> {
                        ContextResource resource = new ContextResource();
                        log.debug("Configuring JNDI source: " + source.getName());
                        resource.setName(source.getName());
                        resource.setType(source.getType());
                        source.getProperties().keySet().forEach(key ->
                                resource.setProperty(key, source.getProperties().get(key)));
                        context.getNamingResources().addResource(resource);
                    });
                }
            }

        };
    }

}
