/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms.redis.geoserver.info;

import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;
import lombok.Data;
import org.geoserver.catalog.AuthorityURLInfo;
import org.geoserver.catalog.DimensionInfo;
import org.geoserver.catalog.LayerIdentifierInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.wms.CacheConfiguration;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WatermarkInfo;
import org.geoserver.wms.WatermarkInfoImpl;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of {@link WMSInfo} used for serialization to Redis
 */
@Data
@RedisHash("WMSInfo")
public class WMSInfoRedisImpl extends AbstractServiceInfoRedisImpl implements ServiceInfo, WMSInfo {
    ArrayList<String> srs = new ArrayList<String>();

    Boolean bboxForEachCRS;

    WatermarkInfo watermark = new WatermarkInfoImpl();

    WMSInterpolation interpolation = WMSInterpolation.Nearest;

    boolean getFeatureInfoMimeTypeCheckingEnabled;
    HashSet<String> getFeatureInfoMimeTypes = new HashSet<String>();

    boolean getMapMimeTypeCheckingEnabled;
    HashSet<String> getMapMimeTypes = new HashSet<String>();

    boolean dynamicStylingDisabled;

    private boolean featuresReprojectionDisabled = false;

    protected List<AuthorityURLInfo> authorityURLs = new ArrayList<AuthorityURLInfo>(2);

    protected List<LayerIdentifierInfo> identifiers = new ArrayList<LayerIdentifierInfo>(2);

    int maxBuffer;

    int maxRequestMemory;

    int maxRenderingTime;

    int maxRenderingErrors;

    private String capabilitiesErrorHandling;

    private String rootLayerTitle;

    private String rootLayerAbstract;

    private Integer maxRequestedDimensionValues;

    private CacheConfiguration cacheConfiguration = new CacheConfiguration();

    public List<String> getSRS() {
        return srs;
    }

    //Required for copyProperties from geoserver to redis impl to work
    public void setSRS(List<String> srs) {
        if (srs instanceof ArrayList) {
            this.srs = (ArrayList<String>) srs;
        } else {
            throw new IllegalArgumentException("WMSInfo SRS list must be an ArrayList, was: " + srs.getClass());
        }
    }

    public Boolean isBBOXForEachCRS() {
        if (bboxForEachCRS != null) {
            return bboxForEachCRS;
        }

        // check the metadata map if upgrading from 2.1.x
        Boolean bool = getMetadata().get("bboxForEachCRS", Boolean.class);
        return bool != null && bool;
    }

    public void setBBOXForEachCRS(Boolean bboxForEachCRS) {
        this.bboxForEachCRS = bboxForEachCRS;
    }

    @Override
    public void setDynamicStylingDisabled(Boolean dynamicStylingDisabled) {
        this.dynamicStylingDisabled = dynamicStylingDisabled;
    }

    @Override
    public Boolean isDynamicStylingDisabled() {
        return dynamicStylingDisabled;
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

    @Override
    public CacheConfiguration getCacheConfiguration() {
        if (cacheConfiguration == null) {
            cacheConfiguration = new CacheConfiguration();
        }
        return cacheConfiguration;
    }

    @Override
    public void setCacheConfiguration(CacheConfiguration cacheCfg) {
        this.cacheConfiguration = cacheCfg;
    }

}
