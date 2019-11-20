/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import stratus.jndi.JndiSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joshfix
 * Created on 10/26/17
 */
@Data
@Component
@ConfigurationProperties(prefix = "stratus")
public class StratusConfigProps {

    private Web web = new Web();
    private Tomcat tomcat = new Tomcat();
    private Jndi jndi = new Jndi();

    @Data
    public static class Web {
        private boolean requestLoggingFilterEnabled;
        private boolean requestLoggingFilterLogRequestBodies;
        private String theme = "light";
        private boolean enableRedisSessions = false;
    }

    @Data
    public static class Tomcat {
        private boolean useRelativeRedirects = true;
    }

    @Data
    public static class Jndi {
        private List<JndiSource> sources = new ArrayList<>();
    }

}
