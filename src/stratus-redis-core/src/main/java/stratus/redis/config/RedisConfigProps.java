/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joshfix
 */
@Data
@Getter
@Component
@ConfigurationProperties(prefix = "stratus.catalog.redis")
public class RedisConfigProps {

    private boolean shareNativeLettuceConnection = true;
    private boolean enableConnectionPooling = true;
    private boolean enableStratus12Upgrade = true;
    private Caching caching = new Caching();
    private Manual manual = new Manual();
    private Sentinel sentinel = new Sentinel();
    private Cluster cluster = new Cluster();
    private Pcf pcf = new Pcf();
    private Pool pool = new Pool();

    @Data
    public static class Caching {
        private boolean enableRestCaching = true;
        private boolean enableOwsCaching = true;
        private boolean useParallelQueries = false;
    }

    @Data
    public static class Sentinel {
        private String master;
        List<String> hosts = new ArrayList<>();
        private boolean useSlaveReads = true;
        private String password;
    }

    @Data
    public static class Cluster {
        private List<String> hosts = new ArrayList<>();
        private String password;
    }

    @Data
    public static class Manual {
        private int port = 6379;
        private int database = 0;
        private String host = "localhost";
        private String password;
    }

    @Data
    public static class Pcf {
        private String serviceId = "stratus-redis";
    }

    @Data
    public static class Pool {
        private boolean LIFO = GenericObjectPoolConfig.DEFAULT_LIFO;
        private boolean testOnBorrow = GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW;
        private boolean testOnReturn = GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN;
        private boolean testWhileIdle = GenericObjectPoolConfig.DEFAULT_TEST_WHILE_IDLE;
        private boolean testOnCreate = GenericObjectPoolConfig.DEFAULT_TEST_ON_CREATE;
        private boolean fairness = GenericObjectPoolConfig.DEFAULT_FAIRNESS;
        private boolean blockWhenExhausted = GenericObjectPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;

        private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
        private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
        private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
        private int numTestsPerEvictionRun = GenericObjectPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

        private long maxWaitMillis = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
        private long timeBetweenEvictionRunsMillis = GenericObjectPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
        private long minEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
        private long softMinEvictableIdleTimeMillis = GenericObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    }

}