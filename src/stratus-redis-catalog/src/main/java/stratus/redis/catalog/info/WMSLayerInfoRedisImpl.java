/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.ows.wms.Layer;
import org.geotools.styling.Style;
import org.opengis.util.ProgressListener;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private List<StyleInfo> allAvailableRemoteStyles = new ArrayList<>();

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
    public List<String> remoteStyles() {
        return null;
    }

    @Override
    public String getForcedRemoteStyle() {
        return null;
    }

    @Override
    public void setForcedRemoteStyle(String s) {

    }

    @Override
    public List<String> availableFormats() {
        return null;
    }

    @Override
    public Optional<Style> findRemoteStyleByName(String s) {
        return Optional.empty();
    }

    @Override
    public boolean isSelectedRemoteStyles(String s) {
        return false;
    }

    @Override
    public Set<StyleInfo> getRemoteStyleInfos() {
        return null;
    }

    @Override
    public List<String> getSelectedRemoteFormats() {
        return null;
    }

    @Override
    public void setSelectedRemoteFormats(List<String> list) {

    }

    @Override
    public List<String> getSelectedRemoteStyles() {
        return null;
    }

    @Override
    public void setSelectedRemoteStyles(List<String> list) {

    }

    @Override
    public boolean isFormatValid(String s) {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public String getPreferredFormat() {
        return null;
    }

    @Override
    public void setPreferredFormat(String s) {

    }

    @Override
    public Set<StyleInfo> getStyles() {
        return null;
    }

    @Override
    public StyleInfo getDefaultStyle() {
        return null;
    }

    @Override
    public Double getMinScale() {
        return null;
    }

    @Override
    public void setMinScale(Double aDouble) {

    }

    @Override
    public Double getMaxScale() {
        return null;
    }

    @Override
    public void setMaxScale(Double aDouble) {

    }

    @Override
    public boolean isMetadataBBoxRespected() {
        return false;
    }

    @Override
    public void setMetadataBBoxRespected(boolean b) {

    }

    public List<StyleInfo> getAllAvailableRemoteStyles() {
        if (allAvailableRemoteStyles == null) allAvailableRemoteStyles = new ArrayList<StyleInfo>();
        return allAvailableRemoteStyles;
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
