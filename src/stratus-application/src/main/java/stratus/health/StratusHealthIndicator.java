/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class StratusHealthIndicator implements HealthIndicator {

    private Health health = Health.down().withDetail("GeoServer not finished initializing.", HttpStatus.SERVICE_UNAVAILABLE).build();

    @Override
    public Health health() {
        return health;
    }

    public void up() {
        health = Health.up().build();
    }

    public void down(String reason, HttpStatus code) {
        health = Health.down().withDetail(reason, code).build();
    }
}
