/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import org.geowebcache.config.XMLGridSet;
import org.geowebcache.grid.GridSet;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Serializable representation of a GridSet annotated for Spring Data
 * @author smithkm
 *
 */
@RedisHash("GridSet")
public class GridSetRedisImpl extends XMLGridSet {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 6413583224286819685L;
    
    @Id
    @Override
    public String getName() {
        return super.getName();
    }
    
    public GridSetRedisImpl() {
        super();
    }
    
    public GridSetRedisImpl(GridSet gset) {
        super(gset);
    }
    
    public GridSetRedisImpl(XMLGridSet orig) {
        super(orig);
    }
    
}
