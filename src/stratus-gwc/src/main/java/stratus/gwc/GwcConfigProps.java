/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GWC Properties object, populated by application.yml
 */
@Data
@Component
@ConfigurationProperties(prefix = "stratus.gwc")
public class GwcConfigProps {

    private boolean defaultFileBlobStore = false;
    private TransientCache transientCache = new TransientCache();

    @Data
    public static class TransientCache {

        private int maxTiles = 100;
        private int maxStorageKB = 1024;
        private long expireDelay = 2000;

    }
}
