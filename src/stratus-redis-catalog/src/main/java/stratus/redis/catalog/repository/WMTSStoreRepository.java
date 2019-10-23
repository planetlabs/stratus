/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import stratus.redis.catalog.info.WMTSStoreInfoRedisImpl;

/**
 * @author joshfix
 * Created on 9/26/17
 */
public interface WMTSStoreRepository extends CatalogInfoCrudRepository<WMTSStoreInfoRedisImpl>,
        CatalogInfoByWorkspace<WMTSStoreInfoRedisImpl> {}