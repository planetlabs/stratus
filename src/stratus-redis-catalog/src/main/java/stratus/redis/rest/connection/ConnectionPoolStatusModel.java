/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.connection;

import lombok.Data;

/**
 * Models the data for the redis connection pool status
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Data
public class ConnectionPoolStatusModel {
    private String title;
    private Integer activeConnections;
    private Integer maxTotalConnections;
    private Integer idleConnections;
    private Integer minIdleConnections;
    private Integer maxIdleConnections;
}
