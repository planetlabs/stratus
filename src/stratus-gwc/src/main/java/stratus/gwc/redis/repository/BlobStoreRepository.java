/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.repository;

import stratus.gwc.redis.data.BlobStoreInfoRedisImpl;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * SpringData repository for BlobStoreInfo objects
 *
 * @author smithkm
 * @param <T>
 */
public interface BlobStoreRepository extends PagingAndSortingRepository<BlobStoreInfoRedisImpl<?>, String> {
    public Optional<BlobStoreInfoRedisImpl<?>> findByDefaultFlag(boolean value);
}
