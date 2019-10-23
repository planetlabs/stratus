/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import lombok.Data;
import org.geowebcache.config.BlobStoreInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * Representation of a BlobStoreInfo for Redis serialization
 * @author smithkm
 *
 * @param <T>
 */
@Data
@RedisHash("BlobStore")
public abstract class BlobStoreInfoRedisImpl<T extends BlobStoreInfo> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String name;
    
    private boolean enabled;
    
    @Indexed
    private boolean defaultFlag;
    
    /**
     * Create from a real BlobStoreInfo
     */
    public BlobStoreInfoRedisImpl(T template) {
        BeanUtils.copyProperties(template, this);
        this.setDefaultFlag(template.isDefault()); // Having the property named default causes problems with serializtion
    }
    
    /**
     * Create from values
     */
   public BlobStoreInfoRedisImpl(String name, boolean enabled, boolean defaultFlag) {
        super();
        this.name = name;
        this.enabled = enabled;
        this.defaultFlag = defaultFlag;
    }
    
   /**
    * Default constructor to make serializtion happy
    */
    public BlobStoreInfoRedisImpl() {
        
    };
    
    /**
     * Construct the corresponding Info object. Bean Properties will be copied over.
     * @return
     */
    protected abstract T constructInfo();
    
    /**
     * Get a real BlobStoreInfo
     */
    public T getInfo() {
        T info = constructInfo();
        BeanUtils.copyProperties(this, info);
        info.setDefault(this.isDefaultFlag());
        return info;
    }
    
}
