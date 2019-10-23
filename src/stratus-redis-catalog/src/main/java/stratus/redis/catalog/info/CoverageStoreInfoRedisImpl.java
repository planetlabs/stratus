/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.util.factory.Hints;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.util.ProgressListener;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;

/**
 * Created by joshfix on 9/21/16.
 */
@Getter
@Setter
@ToString
@RedisHash("CoverageStoreInfo")
public class CoverageStoreInfoRedisImpl extends StoreInfoRedisImpl implements CoverageStoreInfo {
	private static final long serialVersionUID = 119773369179145056L;
	
	protected String url;

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void setURL(String url) {
        this.url = url;
    }

    public AbstractGridFormat getFormat() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public GridCoverageReader getGridCoverageReader(ProgressListener listener, Hints hints) throws IOException {
        return null;
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit( this );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof CoverageStoreInfo ) ) {
            return false;
        }
        
        return super.equals( obj );
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }

}
