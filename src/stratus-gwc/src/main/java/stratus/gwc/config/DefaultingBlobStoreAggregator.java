/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.storage.BlobStoreAggregator;

import java.util.stream.Stream;

/**
 * BlobStoreAggregator that knows how to find the default blob store.
 * @author smithkm
 *
 */
// TODO this should probably be upported to community
public class DefaultingBlobStoreAggregator extends BlobStoreAggregator {


    /**
     * Get the default blob store
     * @return
     * @throws GeoWebCacheException if no default is found.
     */
    public BlobStoreInfo getDefaultBlobStore() throws GeoWebCacheException {
        
        return getConfigs().stream()
            .flatMap(c->{
                    if(c instanceof DefaultingBlobStoreConfiguration) {
                        return ((DefaultingBlobStoreConfiguration) c).getDefaultBlobStore()
                                .map(Stream::of)
                                .orElseGet(Stream::empty); // TODO In Java 9+ use Optional.stream()
                    } else {
                        return c.getBlobStores().stream();
                    }
                })
            .filter(BlobStoreInfo::isDefault)
            .findFirst()
            .orElseThrow(()->new GeoWebCacheException("Thread " + Thread.currentThread().getId()
                    + " No default blobstore found. Check the logfiles,"
                    + " it may not have loaded properly."));
        // I would have preferred Optional or null but this is consistent with the rest of BlobStoreAggregator
    }
    
}
