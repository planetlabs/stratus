/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.ResourcePool;
import org.geoserver.catalog.WMTSStoreInfo;
import org.geotools.ows.wmts.WebMapTileServer;
import org.opengis.util.ProgressListener;
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
@RedisHash("WMTSStoreInfo")
public class WMTSStoreInfoRedisImpl extends StoreInfoRedisImpl implements WMTSStoreInfo, Serializable {

    private static final long serialVersionUID = -5791893990557840193L;
    String capabilitiesURL;
    private String username;
    private String password;
    private int maxConnections;
    private int readTimeout;
    private int connectTimeout;
    private String headerName;
    private String headerValue;
    public static final int DEFAULT_MAX_CONNECTIONS = 6;
    public static final int DEFAULT_CONNECT_TIMEOUT = 30;
    public static final int DEFAULT_READ_TIMEOUT = 60;

    public void accept(CatalogVisitor visitor) {
        visitor.visit( this);
    }

    public WebMapTileServer getWebMapTileServer(ProgressListener listener) throws IOException {
        Catalog catalog = getCatalog();
        ResourcePool resourcePool = catalog.getResourcePool();
        return resourcePool.getWebMapTileServer(this);
    }

    @Override
    public boolean isUseConnectionPooling() {
        Boolean useConnectionPooling = getMetadata().get("useConnectionPooling", Boolean.class);
        return useConnectionPooling == null ? Boolean.TRUE : useConnectionPooling;
    }

    @Override
    public void setUseConnectionPooling(boolean useHttpConnectionPooling) {
        getMetadata().put("useConnectionPooling", useHttpConnectionPooling);
    }

    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof WMTSStoreInfo) ) {
            return false;
        }

        return super.equals( obj );
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
