/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by aaryno on 1/13/17.
 */
@Slf4j
public class GeoServerInitializedEvent extends ApplicationEvent {

    public GeoServerInitializedEvent(ConfigurableApplicationContext app) {
        super(app);
    }

}
