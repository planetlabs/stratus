/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.DataStoreInfo;
import org.geotools.data.DataAccess;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.util.ProgressListener;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("DataStoreInfo")
public class DataStoreInfoRedisImpl extends StoreInfoRedisImpl implements DataStoreInfo {

    private static final long serialVersionUID = -8174199009574657820L;

	@Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DataAccess<? extends FeatureType, ? extends Feature> getDataStore(ProgressListener listener) throws IOException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof DataStoreInfo ) ) {
            return false;
        }
        
        return super.equals( obj );
    }
    
    @Override
    public int hashCode() {
    	return super.hashCode();
    }
}
