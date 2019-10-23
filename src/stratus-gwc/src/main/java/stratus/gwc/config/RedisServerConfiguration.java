/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.ServerConfigurationRedisImpl;
import stratus.gwc.redis.repository.ServerRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.geowebcache.config.ServerConfiguration;
import org.geowebcache.config.meta.ServiceInformation;
import org.geowebcache.locks.LockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

/**
 * Gets/sets {@link ServerConfigurationRedisImpl server configuration} from/to redis
 */
@Slf4j
@Primary
@Service("stratusGWCServerConfiguration")
public class RedisServerConfiguration extends BaseRedisConfiguration implements ServerConfiguration {

    protected ServerRepository serverRepository;

    /**
     * Construct the configuration with the provided repository and th default identifier ("ServerConfiguration:global").
     *
     * @param repository The redis repository
     */
    public RedisServerConfiguration(@Qualifier("redisRepositoryImpl") RedisRepositoryImpl repository, RedisConfigProps configProps, ServerRepository serverRepository) {
        super(repository, configProps, "global");
        this.serverRepository = serverRepository;
    }

    /**
     * Get the global configuration data object from redis
     * @return the configuration
     */
    private Optional<ServerConfigurationRedisImpl> getConfigurationInternal() {
        return serverRepository.findById(identifier);
    }

    /**
     * Get the configuration; if null return a new empty configuration
     * @return the configuration
     */
    protected ServerConfigurationRedisImpl getConfiguration() {
        Optional<ServerConfigurationRedisImpl> configuration = getConfigurationInternal();
        if (configuration.isPresent()) {
            return configuration.get();
        } else {
            ServerConfigurationRedisImpl configurationInternal = new ServerConfigurationRedisImpl(identifier);
            configurationInternal.setIdentifier(identifier);

            return configurationInternal;
        }
    }

    /**
     * Save the configuration to redis, forcing it to have identifier {@link BaseRedisConfiguration#identifier}.
     *
     * @param configuration
     */
    protected void setConfiguration(ServerConfigurationRedisImpl configuration) {
        configuration.setIdentifier(identifier);
        serverRepository.save(configuration);
    }

    @Override
    public ServiceInformation getServiceInformation() {
        return getConfiguration().getServiceInformation();
    }

    @Override
    public void setServiceInformation(ServiceInformation serviceInformation) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setServiceInformation(serviceInformation);
        setConfiguration(configuration);
    }

    /**
     * Runtime statistics run, by default, every three second and provide data about how many requests the system has
     * been serving in the past 3, 15 and 60 seconds, as well as aggregate numbers.
     * @return true if enabled, or false otherwise. Defaults to true.
     */
    @Override
    public Boolean isRuntimeStatsEnabled() {
        Boolean runtimeStatsEnabled = getConfiguration().isRuntimeStatsEnabled();
        //GeoWebCacheDispatcher crashes if this is ever null. GWC assumes true is the default
        return runtimeStatsEnabled == null ? true : runtimeStatsEnabled;
    }

    @Override
    public void setRuntimeStatsEnabled(Boolean runtimeStatsEnabled) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setRuntimeStatsEnabled(runtimeStatsEnabled);
        setConfiguration(configuration);
    }

    @Override
    public LockProvider getLockProvider() {
        return getConfiguration().getLockProvider();
    }

    @Override
    public void setLockProvider(LockProvider lockProvider) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setLockProvider(lockProvider);
        setConfiguration(configuration);
    }

    @Override
    public Boolean isFullWMS() {
        return getConfiguration().isFullWMS();
    }

    @Override
    public void setFullWMS(Boolean fullWMS) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setFullWMS(fullWMS);
        setConfiguration(configuration);
    }

    /**
     * If this method returns NULL CITE strict compliance mode should not be considered for WMTS
     * service implementation.
     *
     * @return true if compliant, false otherwise. Defaults to false.
     */
    @Override
    public Boolean isWmtsCiteCompliant() {
        Boolean wmtsCiteCompliant = getConfiguration().isWmtsCiteCompliant();
        //WMTSService crashes if this is ever null. GWC assumes false is the default
        return wmtsCiteCompliant == null ? false : wmtsCiteCompliant;
    }

    @Override
    public void setWmtsCiteCompliant(Boolean wmtsCiteCompliant) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setWmtsCiteCompliant(wmtsCiteCompliant);
        setConfiguration(configuration);
    }

    @Override
    public Integer getBackendTimeout() {
        return getConfiguration().getBackendTimeout();
    }

    @Override
    public void setBackendTimeout(Integer backendTimeout) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setBackendTimeout(backendTimeout);
        setConfiguration(configuration);
    }

    @Override
    public Boolean isCacheBypassAllowed() {
        return getConfiguration().isCacheBypassAllowed();
    }

    @Override
    public void setCacheBypassAllowed(Boolean cacheBypassAllowed) throws IOException {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setCacheBypassAllowed(cacheBypassAllowed);
        setConfiguration(configuration);
    }

    //TODO: Is this the client version or the configuration version?
    @Override
    public String getVersion() {
        return getConfiguration().getVersion();
    }

    public void setVersion(String version) {
        ServerConfigurationRedisImpl configuration = getConfiguration();
        configuration.setVersion(version);
        setConfiguration(configuration);
    }
    /**
     * Sets up this {@link RedisServerConfiguration}.
     *
     * (Re)sets the version, based on the current specification version of the gwc-core jar, providing any necessary
     * migration steps to update the stored ServerConfiguration.
     *
     * Sets the location, based on the current location of the Redis Repository.
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }
}
