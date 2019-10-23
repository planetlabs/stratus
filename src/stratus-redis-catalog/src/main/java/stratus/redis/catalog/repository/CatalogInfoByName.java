/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogInfoByName<T> {

	Page<T> findByName(String name, Pageable page);

	T findByName(String name);

	List<T> findListByName(String name);

}