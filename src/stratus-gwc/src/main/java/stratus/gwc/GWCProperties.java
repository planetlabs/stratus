/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GWC Properties object, populated by application.yml
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stratus.gwc")
public class GWCProperties {
    private boolean defaultFileBlobStore = false;
}
