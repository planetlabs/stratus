/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.locks.LockProvider;
import org.geowebcache.storage.*;
import stratus.redis.cache.ThreadCachingBean;

import java.util.*;

/**
 * Similar to CompositeBlobStore but builds subordinate blob stores on the fly and caches them only 
 * for a single request.
 * @author smithkm
 *
 */

public class ClusteredCompositeBlobStore implements BlobStore, ThreadCachingBean {
    
    private TileLayerDispatcher layers;
    private BlobStoreAggregator blobStoreAggregator;
    private LockProvider lockProvider;
    private Collection<BlobStoreListener> listeners = new HashSet<>();
    
    /**
     * 
     * @param layers
     * @param blobStoreAggregator
     */
    public ClusteredCompositeBlobStore(TileLayerDispatcher layers,
            BlobStoreAggregator blobStoreAggregator) {
        super();
        this.layers = layers;
        this.blobStoreAggregator = blobStoreAggregator;
    }
    
    private class StoreAndConfig {
        public final BlobStore store;
        public final BlobStoreInfo config;
        
        public StoreAndConfig(BlobStoreInfo config) throws StorageException {
            this.store = config.createInstance(layers, lockProvider);
            listeners.forEach(store::addListener);
            this.config = config;
        }
    }
    private ThreadLocal<Map<String, StoreAndConfig>> cache = 
            new ThreadLocal<Map<String, StoreAndConfig>>() {
        
        @Override
        protected Map<String, StoreAndConfig> initialValue() {
            return new HashMap<>();
        }
        
    };
    private ThreadLocal<StoreAndConfig> defaultCache = new ThreadLocal<>();
    
    
    protected StoreAndConfig getStoreInternal(String name) throws StorageException {
        try {
            return cache.get().computeIfAbsent(name, (k)->{
                try {
                    return new StoreAndConfig(getInfo(k));
                } catch (GeoWebCacheException|StorageException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if(e.getCause() instanceof StorageException) {
                throw (StorageException) e.getCause();
            } else if(e.getCause() instanceof GeoWebCacheException) {
                throw new StorageException("Could not get configuration for blob store "+name, e.getCause());
            } else  {
                throw e;
            }
        }
    }

    /**
     * Get the default blob store and its config
     * @return
     * @throws StorageException
     */
    protected StoreAndConfig getDefaultInternal() throws StorageException {
        try {
            StoreAndConfig result = defaultCache.get();
            if(Objects.isNull(result)) {
                try {
                    if(blobStoreAggregator instanceof DefaultingBlobStoreAggregator) {
                        result =  new StoreAndConfig(((DefaultingBlobStoreAggregator) blobStoreAggregator).getDefaultBlobStore());
                    } else {
                        // Aggregator doesn't know how to find the default so try to find it
                        for(BlobStoreInfo bsi:blobStoreAggregator.getBlobStores()) {
                            if (bsi.isDefault()) {
                                result = new StoreAndConfig(bsi);
                            }
                        }
                        if(Objects.isNull(result)) {
                            throw new GeoWebCacheException("Thread " + Thread.currentThread().getId()
                                    + " No default blobstore found. Check the logfiles,"
                                    + " it may not have loaded properly.");
                        }
                    }
                } catch(StorageException | GeoWebCacheException e) {
                    throw new RuntimeException(e);
                }
                defaultCache.set(result);
            }
            return result;
        } catch (RuntimeException e) {
            if(e.getCause() instanceof StorageException) {
                throw (StorageException) e.getCause();
            } else if(e.getCause() instanceof GeoWebCacheException) {
                throw new StorageException("Could not get configuration for default blob store ", e.getCause());
            } else  {
                throw e;
            }
        }
    }

    private BlobStoreInfo getInfo(String name) throws GeoWebCacheException {
        return blobStoreAggregator.getBlobStore(name);
    }


    @Override
    public boolean delete(String layerName) throws StorageException {
        return storeByLayer(layerName).delete(layerName);
    }

    private BlobStore safeStoreByLayer(String layerName) {
        try {
            return storeByLayer(layerName);
        } catch (StorageException ex) {
            throw new RuntimeException(ex);
        }
    }
    private BlobStore storeByLayer(String layerName) throws StorageException {
        String blobStoreId;
        try {
            blobStoreId = layers.getTileLayer(layerName).getBlobStoreId();
        } catch (GeoWebCacheException e) {
            throw new StorageException("Could not get BlobStore for layer "+layerName,e);
        }
        StoreAndConfig storeAndConfig = storeByName(layerName, blobStoreId);
        return storeAndConfig.store;
    }

    /**
     * Get a store and its config based on a layer name
     * @param layerName
     * @param blobStoreId
     * @return
     * @throws StorageException
     */
    protected StoreAndConfig storeByName(String layerName, String blobStoreId)
            throws StorageException {
        StoreAndConfig storeAndConfig = Objects.nonNull(blobStoreId) ?
                getStoreInternal(blobStoreId) : getDefaultInternal();
        if(!storeAndConfig.config.isEnabled()) {
            throw new StorageException("Blob Store "+blobStoreId+" used by layer "+layerName+" is not enabled");
        }
        return storeAndConfig;
    }


    @Override
    public boolean deleteByGridsetId(String layerName, String gridSetId) throws StorageException {
        return storeByLayer(layerName).deleteByGridsetId(layerName, gridSetId);
    }

    @Override
    public boolean deleteByParametersId(String layerName, String parametersId)
            throws StorageException {
        return storeByLayer(layerName).deleteByParametersId(layerName, parametersId);
    }

    @Override
    public boolean delete(TileObject obj) throws StorageException {
        return storeByLayer(obj.getLayerName()).delete(obj);
    }

    @Override
    public boolean delete(TileRange obj) throws StorageException {
        return storeByLayer(obj.getLayerName()).delete(obj);
    }

    @Override
    public boolean get(TileObject obj) throws StorageException {
        return storeByLayer(obj.getLayerName()).get(obj);
    }

    @Override
    public void put(TileObject obj) throws StorageException {
        storeByLayer(obj.getLayerName()).put(obj);
    }
    
    @Override
    public void clear() throws StorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearThreadCache() {
        cache.get().values().stream().map(e->e.store).forEach(BlobStore::destroy);
        StoreAndConfig defaultEntry = defaultCache.get();
        if(Objects.nonNull(defaultEntry)) {
            defaultEntry.store.destroy();
        }
        cache.remove();
        defaultCache.remove();
    }
    
    @Override
    public void destroy() {
        // Ideally we would do this for all the active threads but ThreadLocal does not provide that.
        clearThreadCache();
    }

    @Override
    public void addListener(BlobStoreListener listener) {
        listeners.add(listener);
        cache.get().values().forEach(entry->{entry.store.addListener(listener);});
        StoreAndConfig defaultEntry = defaultCache.get();
        if(Objects.nonNull(defaultEntry)) {
            defaultEntry.store.addListener(listener);
        }
    }

    @Override
    public boolean removeListener(BlobStoreListener listener) {
        Boolean result = listeners.remove(listener);
        cache.get().values().forEach(entry->{entry.store.removeListener(listener);});
        StoreAndConfig defaultEntry = defaultCache.get();
        if(Objects.nonNull(defaultEntry)) {
            defaultEntry.store.removeListener(listener);
        }
        return result;
    }

    @Override
    public boolean rename(String oldLayerName, String newLayerName) throws StorageException {
        return storeByLayer(oldLayerName).rename(oldLayerName, newLayerName);
    }

    @Override
    public String getLayerMetadata(String layerName, String key) {
        return safeStoreByLayer(layerName).getLayerMetadata(layerName, key);
    }

    @Override
    public void putLayerMetadata(String layerName, String key, String value) {
        safeStoreByLayer(layerName).putLayerMetadata(layerName, key, value);
    }

    @Override
    public boolean layerExists(String layerName) {
        return safeStoreByLayer(layerName).layerExists(layerName);
    }

    @Override
    public Map<String, Optional<Map<String, String>>> getParametersMapping(String layerName) {
        return safeStoreByLayer(layerName).getParametersMapping(layerName);
    }

    @Override
    public boolean deleteByParameters(String layerName, Map<String, String> parameters)
            throws StorageException {
        return storeByLayer(layerName).deleteByParameters(layerName, parameters);
    }

    @Override
    public Set<Map<String, String>> getParameters(String layerName) throws StorageException {
        return storeByLayer(layerName).getParameters(layerName);
    }
    
    @Override
    public Set<String> getParameterIds(String layerName) throws StorageException {
        return storeByLayer(layerName).getParameterIds(layerName);
    }
    
    @Override
    public boolean purgeOrphans(TileLayer layer) throws StorageException {
        return storeByName(layer.getName(), layer.getBlobStoreId()).store.purgeOrphans(layer);
    }
    
    
}
