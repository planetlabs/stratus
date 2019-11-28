/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.geowebcache.config.BaseConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;

/**
 * Abstract superclass for all redis implementation of {@link BaseConfiguration}.
 */
public abstract class BaseRedisConfiguration implements BaseConfiguration {

    protected RedisRepositoryImpl repository;
    RedisConfigProps configProps;
    protected String identifier;

    protected String location;

    public BaseRedisConfiguration(@Qualifier("redisRepositoryImpl") RedisRepositoryImpl repository, RedisConfigProps configProps, String identifier) {
        this.repository = repository;
        this.configProps = configProps;
        this.identifier = identifier;
    }
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the web location of the redis repository, in the form of <domain>:<port>
     * @return the location of the configuration
     */
    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void deinitialize() throws Exception {
        //do nothing
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //set up location
        //TODO: Determine what location means for Stratus
        RedisConnectionFactory connectionFactory = repository.getRedisTemplate().getConnectionFactory();

        StringBuilder redisLocation = new StringBuilder();
        RedisConfigProps.Manual manual = configProps.getManual();
        redisLocation.append(configProps.getManual().getHost()).append(":");
        redisLocation.append(configProps.getManual().getPort());
        //TODO: Add implementation for  RedisConfigProps.Cluster and  RedisConfigProps.Sentinal configs

        location = redisLocation.toString();
    }
}
