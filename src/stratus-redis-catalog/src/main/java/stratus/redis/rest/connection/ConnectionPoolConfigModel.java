/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.connection;

import lombok.Data;

/**
 * Models the data for the redis connection pool configuration
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Data
public class ConnectionPoolConfigModel {
    private String title;
    private boolean LIFO;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean testWhileIdle;
    private boolean testOnCreate;
    private boolean fairness;
    private boolean blockWhenExhausted;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int numTestsPerEvictionRun;
    private long maxWaitMillis;
    private long timeBetweenEvictionRunsMillis;
    private long minEvictableIdleTimeMillis;
    private long softMinEvictableIdleTimeMillis;
}
