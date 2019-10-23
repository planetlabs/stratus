/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import java.util.List;

public interface CatalogInfoByStore<T> {

	T findByStoreId(String storeId);

	T findByStoreIdAndName(String storeId, String name);

	List<T> findListByStoreId(String storeId);

}
