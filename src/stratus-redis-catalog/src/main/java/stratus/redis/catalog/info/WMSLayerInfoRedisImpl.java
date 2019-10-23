/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.ows.wms.Layer;
import org.opengis.util.ProgressListener;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("WMSLayerInfo")
public class WMSLayerInfoRedisImpl extends ResourceInfoRedisImpl implements WMSLayerInfo {

	private static final long serialVersionUID = 4052483813698993599L;
	@Transient private transient WMSStoreInfo store;

    @Override
    public WMSStoreInfo getStore() {
    	if (store == null && storeId != null) {
    		store = ResolvingProxy.create(storeId, WMSStoreInfo.class);
    	}
        return store;
    }

    @Override
    public void setStore(StoreInfo store) {
    	if (store instanceof WMSStoreInfo) {
    		storeId = store.getId();
    		this.store = (WMSStoreInfo) store;
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @Override
    public Layer getWMSLayer(ProgressListener pl) throws IOException {
        return catalog.getResourcePool().getWMSLayer(this);
    }

	@Override
	public void accept(CatalogVisitor visitor) {
		visitor.visit(this);
	}

    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof WMSLayerInfo ) ) {
            return false;
        }

        return super.equals( obj );
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
