/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import java.util.List;

public interface CatalogInfoByNamespace<T> extends CatalogInfoByName<T>  {

	List<T> findListByNamespaceId(String namespaceId);

	T findByNamespaceIdAndName(String namespaceId, String name);
	
}
