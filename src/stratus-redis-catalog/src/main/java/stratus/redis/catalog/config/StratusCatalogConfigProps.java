/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by joshfix on 8/19/16.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stratus")
public class StratusCatalogConfigProps {

    private int minWaitForInitializerCheck;
    private int maxWaitForInitializerCheck;
    private int initializerTimeout;
    private String proxyBaseUrl = "";

}
