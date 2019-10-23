/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "stratus.wps")
@Data
public class WPSConfigurationProperties {
    String fileStorage="s3";
    String s3Region = "US_EAST_1";
    String s3Bucket="stratus-wps";
    String s3Url = "https://s3.amazonaws.com/";
    String accessKey;
    String secretKey;
}
