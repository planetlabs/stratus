package stratus.gwc.config;

import lombok.AllArgsConstructor;
import org.geowebcache.storage.TransientCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stratus.gwc.GwcConfigProps;

/**
 * @author joshfix
 * Created on 11/6/19
 */
@Configuration
@AllArgsConstructor
public class GwcConfig {

    private final GwcConfigProps configProps;

    @Bean
    public TransientCache transientCache() {
        return new TransientCache(
                configProps.getTransientCache().getMaxTiles(),
                configProps.getTransientCache().getMaxStorageKB(),
                configProps.getTransientCache().getExpireDelay()
        );
    }
}
