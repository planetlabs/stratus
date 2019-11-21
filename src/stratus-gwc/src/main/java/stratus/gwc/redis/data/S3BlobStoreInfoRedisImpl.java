/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.geowebcache.s3.Access;
import org.geowebcache.s3.S3BlobStoreInfo;

/**
 * Serializable representation of an MbtilesInfo annotated for Spring Data
 * @author smithkm
 *
 */
@Data
@EqualsAndHashCode (callSuper = true)
public class S3BlobStoreInfoRedisImpl extends BlobStoreInfoRedisImpl<S3BlobStoreInfo> {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -3772112974889448534L;

    private String bucket;

    private String prefix;

    private String awsAccessKey;

    private String awsSecretKey;

    private Access access = Access.PUBLIC;

    private Integer maxConnections;

    private Boolean useHTTPS = true;

    private String proxyDomain;

    private String proxyWorkstation;

    private String proxyHost;

    private Integer proxyPort;

    private String proxyUsername;

    private String proxyPassword;

    private Boolean useGzip;

    /**
     * Create from a real BlobStoreInfo
     */
    public S3BlobStoreInfoRedisImpl(S3BlobStoreInfo template) {
        super(template);
        setAccess(template.getAccess());
        setUseHTTPS(template.isUseHTTPS());
    }
    
    /**
     * Create from values
     */
   public S3BlobStoreInfoRedisImpl(String name, boolean enabled, boolean default1,
            String mbtilesMetadataDirectory, int executorConcurrency, Boolean gzipVector) {
        super(name, enabled, default1);
        this.mbtilesMetadataDirectory = mbtilesMetadataDirectory;
        this.executorConcurrency = executorConcurrency;
        this.gzipVector = gzipVector;
    }
    
    /**
     * Default constructor to make serializtion happy
     */
    public S3BlobStoreInfoRedisImpl() {
        super();
    }
    
    private String mbtilesMetadataDirectory;

    private int executorConcurrency;

    private Boolean gzipVector;
    
    @Override
    protected S3BlobStoreInfo constructInfo() {
        return new S3BlobStoreInfo();
    }

}
