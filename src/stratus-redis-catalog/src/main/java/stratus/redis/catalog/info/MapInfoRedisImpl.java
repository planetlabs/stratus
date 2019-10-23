/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Data;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MapInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshfix on 9/22/16.
 */
@Data
@RedisHash("MapInfo")
public class MapInfoRedisImpl implements MapInfo, Serializable {

	private static final long serialVersionUID = -7651773989939858796L;
	
	@Id
    private String id;
    @Indexed
    private String name;
    private boolean enabled;
    
    private List<String> layerIds = new ArrayList<>();
    @Transient
    private transient List<LayerInfo> layers = new ArrayList<>();

    public void accept(CatalogVisitor visitor) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<LayerInfo> getLayers() {
    	if (layers == null && layerIds != null) {
    		layers = new ArrayList<>();
    		for (String layerId : layerIds) {
    			layers.add(ResolvingProxy.create(layerId, LayerInfo.class));
    		}
        }
        return layers;
    }
    
    public void setLayers(List<LayerInfo> layers) {
        this.layers = layers;
        layerIds = new ArrayList<>();
		for (PublishedInfo layer : layers) {
			layerIds.add(layer.getId());
		}
    }

}
