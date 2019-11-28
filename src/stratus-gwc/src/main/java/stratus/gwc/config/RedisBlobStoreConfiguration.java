/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import lombok.extern.slf4j.Slf4j;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreConfigurationListener;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.ConfigurationPersistenceException;
import org.geowebcache.config.FileBlobStoreInfo;
import org.geowebcache.s3.S3BlobStoreInfo;
import org.geowebcache.sqlite.MbtilesInfo;
import org.geowebcache.storage.UnsuitableStorageException;
import org.geowebcache.util.ExceptionUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stratus.gwc.redis.data.BlobStoreInfoRedisImpl;
import stratus.gwc.redis.data.FileBlobStoreInfoRedisImpl;
import stratus.gwc.redis.data.MBTilesInfoRedisImpl;
import stratus.gwc.redis.data.S3BlobStoreInfoRedisImpl;
import stratus.gwc.redis.repository.BlobStoreRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GeoWebCache BlobStoreConfiguration which stores BlobStores in a SpringData repository.
 * 
 * @author smithkm
 *
 */
@Slf4j
@Primary
@Service("stratusGWCBlobStoreConfiguration")
public class RedisBlobStoreConfiguration extends BaseRedisConfiguration implements DefaultingBlobStoreConfiguration {

    private final BlobStoreRepository bsRepository;

    private BlobStoreConfigurationListenerCollection blobStoreListeners = new BlobStoreConfigurationListenerCollection();

    public RedisBlobStoreConfiguration(RedisRepositoryImpl repository, RedisConfigProps configProps, BlobStoreRepository bsRepository) {
        super(repository, configProps, "Stratus BlobStore Catalog");
        this.bsRepository = bsRepository;
    }

    @Override
    public List<BlobStoreInfo> getBlobStores() {
        List<BlobStoreInfo> blobStores = new ArrayList<>();
        for (BlobStoreInfoRedisImpl<?> blobStore : bsRepository.findAll()) {
            blobStores.add(resolve(blobStore));
        }
        return blobStores;
    }

    @Override
    public Optional<BlobStoreInfo> getBlobStore(String blobStoreName) {
        return bsRepository.findById(blobStoreName).map(BlobStoreInfoRedisImpl::getInfo);
    }

    @Override
    public Set<String> getBlobStoreNames() {
        return getBlobStores().stream().map(BlobStoreInfo::getName).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public void removeBlobStore(String blobStoreName)
            throws NoSuchElementException, IllegalArgumentException {
        BlobStoreInfo oldBlobStore = getBlobStore(blobStoreName).orElseThrow(()->new NoSuchElementException("Cannot delete blobStore " + blobStoreName + " - blobStore doesn't exist"));
        try {
            bsRepository.findById(blobStoreName).ifPresent(bsRepository::delete);
            this.blobStoreListeners.safeForEach(l->{l.handleRemoveBlobStore(oldBlobStore);});
        } catch (DataAccessException | GeoWebCacheException | IOException ex) {
            //Rollback on UnsuitableStorageException
            if (ExceptionUtils.isOrSuppresses(ex, UnsuitableStorageException.class)) {
                try {
                    save(oldBlobStore);
                } catch (ConfigurationPersistenceException cpe) {
                    cpe.addSuppressed(ex);
                    throw cpe;
                }
            }
            throw new ConfigurationPersistenceException(ex);
        }
    }

    @Transactional
    @Override
    public void modifyBlobStore(BlobStoreInfo bs) throws NoSuchElementException {
        if(bs != null) {
            BlobStoreInfo old = getBlobStore(bs.getName()).orElseThrow(()->new NoSuchElementException("Cannot modify blobStore " + bs.getName() + " - blobStore doesn't exist"));
            Optional<? extends BlobStoreInfo> oldDefault = bs.isDefault() && !old.isDefault() ? getDefaultBlobStore(): Optional.empty();
            modifyKnownBlobStore(bs);
            oldDefault.ifPresent((oldDef)->{
                try {
                    modifyKnownBlobStore(oldDef);
                } catch (RuntimeException e) {
                    log.warn("Could not unflag old default blob store "+oldDef.getName(), e);
                }
            });
        } else {
            throw new IllegalArgumentException("Cannot modify blobStore " + bs.getName() + " - blobStore is not a BlobStore");
        }
    }

    /**
     * Modify a BlobStoreInfo that is known to be present.
     * @param bs
     * @throws ConfigurationPersistenceException
     */
    protected void modifyKnownBlobStore(BlobStoreInfo bs) throws ConfigurationPersistenceException {
        Optional<BlobStoreInfo> oldBlobStore = getBlobStore(bs.getName());
        save(bs);
        try {
            this.blobStoreListeners.safeForEach(l->{l.handleModifyBlobStore(bs);});
        } catch (GeoWebCacheException | IOException e) {
            //Rollback on UnsuitableStorageException
            if (ExceptionUtils.isOrSuppresses(e, UnsuitableStorageException.class) && oldBlobStore.isPresent()) {
                try {
                    save(oldBlobStore.get());
                } catch (ConfigurationPersistenceException cpe) {
                    cpe.addSuppressed(e);
                    throw cpe;
                }
            }
            throw new ConfigurationPersistenceException(e);
        }
    }

    @Transactional
    @Override
    public void renameBlobStore(String oldName, String newName)
            throws NoSuchElementException, IllegalArgumentException {

        BlobStoreInfoRedisImpl<?> oldBlobStore = bsRepository.findById(oldName).orElseThrow(() ->
                new NoSuchElementException("Cannot rename blobStore " + oldName + " - blobStore doesn't exist"));
        if (bsRepository.findById(newName).isPresent()) {
            throw new IllegalArgumentException("Cannot rename blobStore " + oldName + " to " + newName + " - blobStore already exists");
        }
        try {
            bsRepository.findById(oldName).ifPresent(bsRepository::delete);
            oldBlobStore.setName(newName);
            bsRepository.save(oldBlobStore);
            this.blobStoreListeners.safeForEach(l->{l.handleRenameBlobStore(oldName, resolve(oldBlobStore));});
        } catch (DataAccessException | GeoWebCacheException | IOException ex) {
            //Rollback on UnsuitableStorageException
            if (ExceptionUtils.isOrSuppresses(ex, UnsuitableStorageException.class)) {
                try {
                    bsRepository.findById(newName).ifPresent(bsRepository::delete);
                    oldBlobStore.setName(oldName);
                    bsRepository.save(oldBlobStore);
                } catch (ConfigurationPersistenceException cpe) {
                    cpe.addSuppressed(ex);
                    throw cpe;
                }
            }
            throw new ConfigurationPersistenceException(ex);
        }
    }

    @Transactional
    @Override
    public void addBlobStore(BlobStoreInfo bs) throws IllegalArgumentException {
        if(bs != null) {
            if (containsBlobStore(bs.getName())) {
                throw new IllegalArgumentException("Cannot add blobStore " + bs.getName() + " - blobStore already exists");
            }
            Optional<? extends BlobStoreInfo> oldDefault = bs.isDefault() ? getDefaultBlobStore(): Optional.empty();
            save(bs);
            oldDefault.ifPresent((old)->{
                try {
                    modifyKnownBlobStore(old);
                } catch (RuntimeException e) {
                    log.warn("Could not unflag old default blob store "+old.getName(), e);
                }
            });
            try {
                this.blobStoreListeners.safeForEach(l -> {
                    l.handleAddBlobStore(bs);
                });
            } catch (GeoWebCacheException | IOException e) {
                //Rollback on UnsuitableStorageException
                if (ExceptionUtils.isOrSuppresses(e, UnsuitableStorageException.class)) {
                    try {
                        bsRepository.findById(bs.getName()).ifPresent(bsRepository::delete);
                    } catch (ConfigurationPersistenceException cpe) {
                        cpe.addSuppressed(e);
                        throw cpe;
                    }
                }
                throw new ConfigurationPersistenceException(e);
            }
        } else {
            throw new IllegalArgumentException("Cannot add blobStore " + bs.getName() + " - blobStore is not a BlobStore");
        }
    }

    /**
     * Does the configuration have a gridset by the given name
     * @param blobStoreName
     * @return
     */
    public boolean containsBlobStore(String blobStoreName) {
        return getBlobStore(blobStoreName).isPresent();
    }

    @Override
    public boolean canSave(BlobStoreInfo blobStore) {
        return Objects.nonNull(blobStore.getName()) && (
                    blobStore instanceof FileBlobStoreInfo ||
                    blobStore instanceof S3BlobStoreInfo ||
                    blobStore instanceof MbtilesInfo
                );
    }

    /**
     * Converts a {@link BlobStoreInfo} to a {@link BlobStoreInfoRedisImpl} and saves it to
     * the {@link #bsRepository}
     *
     * @param blobStore The blobStore to save
     */
    protected void save(BlobStoreInfo blobStore) {
        BlobStoreInfoRedisImpl<?> redisBlobStore = unresolve(blobStore);
        try {
            bsRepository.save(redisBlobStore);
        } catch (DataAccessException ex) {
            throw new ConfigurationPersistenceException(ex);
        }
    }

    /**
     * Converts a BlobStoreinfo into a storage object
     * @param blobStore
     * @return
     */
    protected BlobStoreInfoRedisImpl<?> unresolve(BlobStoreInfo blobStore) {
        if(blobStore instanceof FileBlobStoreInfo) {
            return new FileBlobStoreInfoRedisImpl((FileBlobStoreInfo) blobStore);
        } else if(blobStore instanceof S3BlobStoreInfo){
            return new S3BlobStoreInfoRedisImpl((S3BlobStoreInfo) blobStore);
        } else if(blobStore instanceof MbtilesInfo) {
            return new MBTilesInfoRedisImpl((MbtilesInfo) blobStore);
        } else {
            throw new IllegalArgumentException("Can not save blob store "+blobStore.getName()+" of type "+blobStore.getClass().getCanonicalName());
        }
    }

    /**
     * Converts a storage object into a real BlobStoreInfo
     * @param blobStoreRedis
     * @return
     * @throws IllegalArgumentException
     */
    protected BlobStoreInfo resolve(BlobStoreInfoRedisImpl<?> blobStoreRedis) throws IllegalArgumentException {
        if(blobStoreRedis!=null) {
            return blobStoreRedis.getInfo();
        } else {
            return null;
        }
    }

    @Override
    public int getBlobStoreCount() {
        return (int) bsRepository.count();
    }

    @Override
    public void addBlobStoreListener(BlobStoreConfigurationListener listener) {
        if(!listener.getClass().isAnnotationPresent(LocalListener.class)) {
            // This callback mechanism was created for invalidating the cached blob stores in a 
            // non-clustered GWC.  Stratus should not have this cache so this should not be used.
            // A local only implementation is provided in case we end up with anything else using the
            // mechanism. If that becomes  common, we may need to do some form of cluster event 
            // propagation.
            // If we want to be more strict, we could replace this log message with an exception.
            log.warn("Adding BlobStoreConfigurationListener "+listener.toString()+" to RedisBlobStoreConfiguration \""+this.getIdentifier()+"\" which will only report local changes.");
        }
        blobStoreListeners.add(listener);
    }

    @Override
    public void removeBlobStoreListener(BlobStoreConfigurationListener listener) {
        blobStoreListeners.remove(listener);
    }

    @Override
    public Optional<? extends BlobStoreInfo> getDefaultBlobStore() {
        return bsRepository.findByDefaultFlag(true)
                .map(BlobStoreInfoRedisImpl::getInfo);
    }
}
