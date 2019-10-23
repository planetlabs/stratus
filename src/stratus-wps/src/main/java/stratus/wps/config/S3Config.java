/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.config;

import com.amazonaws.services.s3.AmazonS3;
import stratus.wps.WPSStorageCleaner;
import stratus.wps.s3.S3Connector;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

@Configuration
@AllArgsConstructor
/**
 * Make Amazon S3 client available in WPS
 *
 * @author Joseph Miller, Boundless
 */
public class S3Config {

    private final WPSConfigurationProperties wpsConfigurationProperties;
    private final WPSStorageCleaner wpsStorageCleaner;

    @Bean
    public AmazonS3 amazonS3() {
        S3Connector s3Connector =
                new S3Connector(
                        wpsConfigurationProperties.getS3Region(),
                        false,
                        wpsConfigurationProperties.accessKey,
                        wpsConfigurationProperties.secretKey);
        return s3Connector.getS3Client();
    }

    @Bean
    ScheduledExecutorTask scheduledExecutorTask() {
        ScheduledExecutorTask scheduledExecutorTask = new ScheduledExecutorTask();
        scheduledExecutorTask.setDelay(10000);
        scheduledExecutorTask.setPeriod(60000);
        scheduledExecutorTask.setRunnable(wpsStorageCleaner);
        return scheduledExecutorTask;
    }

    @Bean
    ScheduledExecutorFactoryBean wpstimerFactory() {
        ScheduledExecutorFactoryBean scheduledExecutorFactoryBean =
                new ScheduledExecutorFactoryBean();
        scheduledExecutorFactoryBean.setScheduledExecutorTasks(scheduledExecutorTask());
        scheduledExecutorFactoryBean.setDaemon(true);
        scheduledExecutorFactoryBean.setRemoveOnCancelPolicy(true);
        return scheduledExecutorFactoryBean;
    }
}
