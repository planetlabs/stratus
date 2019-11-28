/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.GeoWebCacheExtensions;
import org.geowebcache.config.ServerConfiguration;
import org.geowebcache.config.meta.ServiceInformation;
import org.geowebcache.locks.LockProvider;
import org.geowebcache.locks.MemoryLockProvider;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import stratus.gwc.config.RedisServerConfiguration;

import java.io.IOException;
import java.io.Serializable;

/**
 * Redis data object for persisting a {@link ServerConfiguration} to Redis
 *
 * @see RedisServerConfiguration
 */
@RedisHash("ServerConfiguration")
public class ServerConfigurationRedisImpl implements ServerConfiguration, Serializable {

    @Id
    protected String identifier;
    protected String version;
    protected String location;

    protected ServiceInformation serviceInformation;
    protected Boolean runtimeStatsEnabled;
    //The bean name for the lock provider
    protected String lockProvider;
    protected Boolean fullWMS;
    protected Boolean wmtsCiteCompliant;
    protected Integer backendTimeout;
    protected Boolean cacheBypassAllowed;


    /**
     * Empty constructor for spring/reflective instantiation
     */
    protected ServerConfigurationRedisImpl() {

    }
    /**
     * Copy-constructor
     *
     * @param template ServerConfiguration to copy properties from
     */
    public ServerConfigurationRedisImpl(ServerConfiguration template) throws IOException {
        this.serviceInformation = template.getServiceInformation();
        this.runtimeStatsEnabled = template.isRuntimeStatsEnabled();

        setLockProvider(template.getLockProvider());

        this.fullWMS = template.isFullWMS();
        this.wmtsCiteCompliant = template.isWmtsCiteCompliant();
        this.backendTimeout = template.getBackendTimeout();
        this.cacheBypassAllowed = template.isCacheBypassAllowed();
        this.version = template.getVersion();
        this.identifier = template.getIdentifier();
        this.location = template.getLocation();
    }

    /**
     * Null constructor
     *
     * @param id The id
     */
    public ServerConfigurationRedisImpl(String id) {
        this.identifier = id;
    }

    @Override
    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }

    @Override
    public void setServiceInformation(ServiceInformation serviceInfo) throws IOException {
        this.serviceInformation = serviceInfo;
    }

    @Override
    public Boolean isRuntimeStatsEnabled() {
        return runtimeStatsEnabled;
    }

    @Override
    public void setRuntimeStatsEnabled(Boolean isEnabled) throws IOException {
        this.runtimeStatsEnabled = isEnabled;
    }

    /**
     * Retrieves the configured {@link LockProvider} bean based on the lock provider bean name ({@link #lockProvider}).
     * @return The LockProvider bean, or a new {@link MemoryLockProvider} if no bean of the given name was found
     */
    @Override
    public LockProvider getLockProvider() {
        if (this.lockProvider != null) {
            LockProvider lockProviderBean = (LockProvider) GeoWebCacheExtensions.bean(this.lockProvider);
            if (lockProviderBean != null) {
                return lockProviderBean;
            }
        }
        return new MemoryLockProvider();
    }

    /**
     * Sets the lock provider bean name ({@link #lockProvider}) based on the provided {@link LockProvider} bean.
     * If the passed lockProvider is not a bean, sets the bean name to null.
     *
     * @param lockProvider The lock provider bean
     * @throws IOException
     */
    @Override
    public void setLockProvider(LockProvider lockProvider) throws IOException {
        //default to null
        this.lockProvider = null;

        if (lockProvider != null) {
            String[] lockProviderNames = GeoWebCacheExtensions.getBeansNamesOrderedByPriority(LockProvider.class);

            for (String beanName : lockProviderNames) {
                if (lockProvider.equals(GeoWebCacheExtensions.bean(beanName))) {
                    this.lockProvider = beanName;
                }
            }
        }
    }

    @Override
    public Boolean isFullWMS() {
        return fullWMS;
    }

    @Override
    public void setFullWMS(Boolean fullWMS) throws IOException {
        this.fullWMS = fullWMS;
    }

    @Override
    public Boolean isWmtsCiteCompliant() {
        return wmtsCiteCompliant;
    }

    @Override
    public void setWmtsCiteCompliant(Boolean wmtsCiteStrictCompliant) throws IOException {
        this.wmtsCiteCompliant = wmtsCiteStrictCompliant;
    }

    @Override
    public Integer getBackendTimeout() {
        return backendTimeout;
    }

    @Override
    public void setBackendTimeout(Integer backendTimeout) throws IOException {
        this.backendTimeout = backendTimeout;
    }

    @Override
    public Boolean isCacheBypassAllowed() {
        return cacheBypassAllowed;
    }

    @Override
    public void setCacheBypassAllowed(Boolean cacheBypassAllowed) throws IOException {
        this.cacheBypassAllowed = cacheBypassAllowed;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getLocation() {
        return location;
    }
    
    @Override
    public void reinitialize() throws GeoWebCacheException {
        throw new UnsupportedOperationException("reinitialize() not supported for " + this.getClass().getSimpleName());
    }

    @Override
    public void deinitialize() throws Exception {
        throw new UnsupportedOperationException("deinitialize() not supported for " + this.getClass().getSimpleName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        throw new UnsupportedOperationException("afterPropertiesSet() not supported for " + this.getClass().getSimpleName());
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
