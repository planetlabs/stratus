/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wcs.redis.geoserver.info;

import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;
import lombok.Data;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.wcs.WCSInfo;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link WCSInfo} used for serialization to Redis
 */
@Data
@RedisHash("WCSInfo")
public class WCSInfoRedisImpl extends AbstractServiceInfoRedisImpl implements ServiceInfo, WCSInfo {

    ArrayList<String> srs = new ArrayList<String>();

    boolean gmlPrefixing;

    private boolean latLon = false;

    long maxInputMemory = -1;

    long maxOutputMemory = -1;

    Boolean subsamplingEnabled = Boolean.TRUE;

    OverviewPolicy overviewPolicy;

    Integer maxRequestedDimensionValues;

    @Override
    public boolean isGMLPrefixing() {
        return gmlPrefixing;
    }

    @Override
    public void setGMLPrefixing(boolean gmlPrefixing) {
        this.gmlPrefixing = gmlPrefixing;
    }

    public boolean isSubsamplingEnabled() {
        return subsamplingEnabled == null ? true : subsamplingEnabled;
    }

    public void setSubsamplingEnabled(boolean subsamplingEnabled) {
        this.subsamplingEnabled = subsamplingEnabled;
    }

    public List<String> getSRS() {
        return srs;
    }

    @Override
    public int getMaxRequestedDimensionValues() {
        return maxRequestedDimensionValues == null
                ? DimensionInfo.DEFAULT_MAX_REQUESTED_DIMENSION_VALUES
                : maxRequestedDimensionValues;
    }

    @Override
    public void setMaxRequestedDimensionValues(int maxRequestedDimensionValues) {
        this.maxRequestedDimensionValues = maxRequestedDimensionValues;
    }
}
