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
import org.geoserver.catalog.WMTSLayerInfo;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.ows.wms.Layer;
import org.opengis.util.ProgressListener;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author joshfix
 * Created on 9/26/17
 */
@Getter
@Setter
@ToString
@RedisHash("WMTSLayerInfo")
public class WMTSLayerInfoRedisImpl extends ResourceInfoRedisImpl implements WMTSLayerInfo, Serializable {

    private static final long serialVersionUID = -7261544029245319798L;

    @Transient
    private transient WMTSStoreInfo store;

    @Override
    public Layer getWMTSLayer(ProgressListener listener) throws IOException {
        return catalog.getResourcePool().getWMTSLayer(this);
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit( this);
    }

    @Override
    public WMTSStoreInfo getStore() {
        if (store == null && storeId != null) {
            store = ResolvingProxy.create(storeId, WMTSStoreInfo.class);
        }
        return store;
    }

    @Override
    public void setStore(StoreInfo store) {
        if (store instanceof WMTSStoreInfo) {
            storeId = store.getId();
            this.store = (WMTSStoreInfo) store;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof WMTSLayerInfo) ) {
            return false;
        }

        return super.equals( obj );
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
