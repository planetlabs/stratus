/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class PoolConfigFactory {

    private PoolConfigFactory() {}

    public static GenericObjectPoolConfig lettucePoolConfig(RedisConfigProps configProps) {
        log.debug("Configuring Lettuce connection pool with max pool size " + configProps.getPool().getMaxTotal());

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        configurePool(poolConfig, configProps);
        return poolConfig;
    }

    public static JedisPoolConfig jedisPoolConfig(RedisConfigProps configProps) {
        log.debug("Configuring Jedis connection pool with max pool size " + configProps.getPool().getMaxTotal());
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        configurePool(poolConfig, configProps);
        return poolConfig;
    }

    private static void configurePool(GenericObjectPoolConfig poolConfig, RedisConfigProps configProps) {
        RedisConfigProps.Pool poolConfigProps = configProps.getPool();
        poolConfig.setLifo(poolConfigProps.isLIFO());
        poolConfig.setTestOnBorrow(poolConfigProps.isTestOnBorrow());
        poolConfig.setTestOnCreate(poolConfigProps.isTestOnCreate());
        poolConfig.setTestOnReturn(poolConfigProps.isTestOnReturn());
        poolConfig.setTestWhileIdle(poolConfigProps.isTestWhileIdle());
        poolConfig.setBlockWhenExhausted(poolConfigProps.isBlockWhenExhausted());
        poolConfig.setFairness(poolConfigProps.isFairness());
        poolConfig.setMinIdle(poolConfigProps.getMinIdle());
        poolConfig.setMaxIdle(poolConfigProps.getMaxIdle());
        poolConfig.setMaxTotal(poolConfigProps.getMaxTotal());
        poolConfig.setNumTestsPerEvictionRun(poolConfigProps.getNumTestsPerEvictionRun());
        poolConfig.setMaxWaitMillis(poolConfigProps.getMaxWaitMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(poolConfigProps.getTimeBetweenEvictionRunsMillis());
        poolConfig.setMinEvictableIdleTimeMillis(poolConfigProps.getMinEvictableIdleTimeMillis());
        poolConfig.setSoftMinEvictableIdleTimeMillis(poolConfigProps.getSoftMinEvictableIdleTimeMillis());
    }

}
