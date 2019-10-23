/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.geowebcache.config.BlobStoreConfiguration;
import org.geowebcache.config.BlobStoreInfo;

import java.util.Optional;

/**
 * BlobStoreConfiguration which knows which of its Infos, if any is the default
 * @author smithkm
 *
 */
public interface DefaultingBlobStoreConfiguration extends BlobStoreConfiguration {
    
    /**
     * Retrieves the default {@link BlobStoreInfo} from this configuration.
     * @return An Optional wrapping the desired {@link BlobStoreInfo}, or Empty if it does not exist.
     */
    public Optional<? extends BlobStoreInfo> getDefaultBlobStore();
}
