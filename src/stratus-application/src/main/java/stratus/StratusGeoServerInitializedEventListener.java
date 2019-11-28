/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import stratus.commons.event.GeoServerInitializedEvent;
import stratus.health.StratusHealthIndicator;

/**
 * Created by aaryno on 1/13/17.
 */
@Slf4j
@Component
@AllArgsConstructor
public class StratusGeoServerInitializedEventListener {

    private final StratusHealthIndicator healthIndicator;

    @EventListener
    public void onApplicationEvent(GeoServerInitializedEvent event) {
        log.debug("GeoServerInitializedEvent received in RedisGeoserverEventListener.");
        healthIndicator.up();
    }

}
