/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.WMSStoreInfo;
import org.geotools.ows.wms.WebMapServer;
import org.opengis.util.ProgressListener;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;

/**
 * Created by joshfix on 9/21/16.
 */
@Getter
@Setter
@ToString
@RedisHash("WMSStoreInfo")
public class WMSStoreInfoRedisImpl extends StoreInfoRedisImpl implements WMSStoreInfo {
	private static final long serialVersionUID = -7948669227044960099L;

	public static final int DEFAULT_MAX_CONNECTIONS = 6;

    public static final int DEFAULT_CONNECT_TIMEOUT = 30;

    public static final int DEFAULT_READ_TIMEOUT = 60;

    String capabilitiesURL;
    private String user;
    private String password;
    private int maxConnections;
    private int readTimeout;
    private int connectTimeout;

    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    public WebMapServer getWebMapServer(ProgressListener listener) throws IOException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public void setUsername(String user) {
        this.user = user;
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
        if (!( obj instanceof WMSStoreInfo ) ) {
            return false;
        }
        
        return super.equals( obj );
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }
}
