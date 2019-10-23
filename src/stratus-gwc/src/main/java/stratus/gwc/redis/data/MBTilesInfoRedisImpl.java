/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.geowebcache.sqlite.MbtilesInfo;

/**
 * Serializable representation of an MbtilesInfo annotated for Spring Data
 * @author smithkm
 *
 */
@Data
@EqualsAndHashCode (callSuper = true)
public class MBTilesInfoRedisImpl extends BlobStoreInfoRedisImpl<MbtilesInfo> {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 2264577421681913485L;

    /**
     * Create from a real BlobStoreInfo
     */
    public MBTilesInfoRedisImpl(MbtilesInfo template) {
        super(template);
    }
    
    /**
     * Create from values
     */
    public MBTilesInfoRedisImpl(String name, boolean enabled, boolean default1,
            String mbtilesMetadataDirectory, int executorConcurrency, Boolean gzipVector) {
        super(name, enabled, default1);
        this.mbtilesMetadataDirectory = mbtilesMetadataDirectory;
        this.executorConcurrency = executorConcurrency;
        this.gzipVector = gzipVector;
    }
    
    /**
     * Default constructor to make serializtion happy
     */
    public MBTilesInfoRedisImpl() {
        super();
    }
    
    private String mbtilesMetadataDirectory;

    private int executorConcurrency;

    private Boolean gzipVector;
    
    @Override
    protected MbtilesInfo constructInfo() {
        return new MbtilesInfo();
    }

}
