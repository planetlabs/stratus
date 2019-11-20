/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.geoserver.info;

import lombok.Data;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.config.ServiceInfo;
import org.geoserver.security.CatalogMode;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.WPSInfo;
import org.springframework.data.redis.core.RedisHash;
import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;

import java.util.ArrayList;

/**
 * Implementation of {@link WPSInfo} used for serialization to Redis
 */
@Data
@RedisHash("WPSInfo")
public class WPSInfoRedisImpl extends AbstractServiceInfoRedisImpl implements ServiceInfo, WPSInfo {

    static final String KEY_CONNECTION_TIMEOUT = "connectionTimeout";

    static final Double DEFAULT_CONNECTION_TIMEOUT = 30.0;

    static final String KEY_RESOURCE_EXPIRATION_TIMEOUT = "resourceExpirationTimeout";

    static final int DEFAULT_RESOURCE_EXPIRATION_TIMEOUT = 60 * 5;

    static final String KEY_MAX_SYNCH = "maxSynchronousProcesses";

    static final int DEFAULT_MAX_SYNCH = Runtime.getRuntime().availableProcessors();

    static final String KEY_MAX_ASYNCH = "maxAsynchronousProcesses";

    static final int DEFAULT_MAX_ASYNCH = Runtime.getRuntime().availableProcessors();

    Double connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    Integer resourceExpirationTimeout = DEFAULT_RESOURCE_EXPIRATION_TIMEOUT;

    Integer maxSynchronousProcesses = DEFAULT_MAX_SYNCH;

    Integer maxAsynchronousProcesses = DEFAULT_MAX_ASYNCH;

    ArrayList<ProcessGroupInfo> processGroups = new ArrayList<ProcessGroupInfo>();

    String storageDirectory;

    CatalogMode catalogMode;

    int maxComplexInputSize;

    int maxSynchronousExecutionTime;

    Integer maxSynchronousTotalTime;

    int maxAsynchronousExecutionTime;

    Integer maxAsynchronousTotalTime;

    public double getConnectionTimeout() {
        if (connectionTimeout == null) {
            // check the metadata map for backwards compatibility with 2.1.x series
            MetadataMap md = getMetadata();
            if (md == null) {
                return DEFAULT_CONNECTION_TIMEOUT;
            }
            Double timeout = md.get(KEY_CONNECTION_TIMEOUT, Double.class);
            if (timeout == null) {
                return DEFAULT_CONNECTION_TIMEOUT;
            }
            connectionTimeout = timeout;
        }

        return connectionTimeout;
    }

    public void setConnectionTimeout(double connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getResourceExpirationTimeout() {
        if (resourceExpirationTimeout == null) {
            // check the metadata map for backwards compatibility with 2.1.x series
            MetadataMap md = getMetadata();
            if (md == null) {
                return DEFAULT_RESOURCE_EXPIRATION_TIMEOUT;
            }
            Integer timeout = md.get(KEY_RESOURCE_EXPIRATION_TIMEOUT, Integer.class);
            if (timeout == null) {
                return DEFAULT_RESOURCE_EXPIRATION_TIMEOUT;
            }
            resourceExpirationTimeout = timeout;
        }

        return resourceExpirationTimeout;
    }

    public void setResourceExpirationTimeout(int resourceExpirationTimeout) {
        this.resourceExpirationTimeout = resourceExpirationTimeout;
    }

    public int getMaxSynchronousProcesses() {
        if (maxSynchronousProcesses == null) {
            // check the metadata map for backwards compatibility with 2.1.x series
            MetadataMap md = getMetadata();
            if (md == null) {
                return DEFAULT_MAX_SYNCH;
            }
            Integer max = md.get(KEY_MAX_SYNCH, Integer.class);
            if (max == null) {
                return DEFAULT_MAX_SYNCH;
            }
            maxSynchronousProcesses = max;
        }

        return maxSynchronousProcesses;
    }

    public void setMaxSynchronousProcesses(int maxSynchronousProcesses) {
        this.maxSynchronousProcesses = maxSynchronousProcesses;
    }

    public int getMaxAsynchronousProcesses() {
        if (maxAsynchronousProcesses == null) {
            // check the metadata map for backwards compatibility with 2.1.x series
            MetadataMap md = getMetadata();
            if (md == null) {
                return DEFAULT_MAX_ASYNCH;
            }
            Integer max = md.get(KEY_MAX_ASYNCH, Integer.class);
            if (max == null) {
                return DEFAULT_MAX_ASYNCH;
            }
            maxAsynchronousProcesses = max;
        }

        return maxAsynchronousProcesses;
    }

    public void setMaxAsynchronousProcesses(int maxAsynchronousProcesses) {
        this.maxAsynchronousProcesses = maxAsynchronousProcesses;
    }

}
