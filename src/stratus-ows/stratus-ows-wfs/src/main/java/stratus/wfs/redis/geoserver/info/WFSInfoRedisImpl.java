/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.redis.geoserver.info;

import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;
import lombok.Data;
import org.geoserver.config.ServiceInfo;
import org.geoserver.wfs.GMLInfo;
import org.geoserver.wfs.WFSInfo;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link WFSInfo} used for serialization to Redis
 */
@Data
@RedisHash("WFSInfo")
public class WFSInfoRedisImpl extends AbstractServiceInfoRedisImpl implements ServiceInfo, WFSInfo {

    protected HashMap<Version, GMLInfo> gml = new HashMap<>();
    protected ServiceLevel serviceLevel = ServiceLevel.COMPLETE;
    protected int maxFeatures = Integer.MAX_VALUE;
    protected boolean featureBounding = true;
    protected boolean canonicalSchemaLocation = false;
    protected boolean encodeFeatureMember = false;
    protected boolean hitsIgnoreMaxFeatures = false;
    protected ArrayList<String> srs = new ArrayList<String>();

    public Map<Version, GMLInfo> getGML() {
        return gml == null ? new HashMap<>() : gml;
    }
    public void setGML(Map<Version, GMLInfo> gml) {
        this.gml.putAll(gml);
    }



    @Override
    public Integer getMaxNumberOfFeaturesForPreview() {
        Integer i = getMetadata().get("maxNumberOfFeaturesForPreview", Integer.class);
        return i != null ? i : 50;
    }

    @Override
    public void setMaxNumberOfFeaturesForPreview(Integer maxNumberOfFeaturesForPreview) {
        getMetadata().put("maxNumberOfFeaturesForPreview", maxNumberOfFeaturesForPreview);
    }

    public List<String> getSRS() {
        return srs;
    }
}
