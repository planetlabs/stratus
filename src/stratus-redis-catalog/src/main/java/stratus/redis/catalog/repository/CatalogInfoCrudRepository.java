/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import org.geoserver.catalog.CatalogInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by joshfix on 9/20/16.
 */
public interface CatalogInfoCrudRepository<T extends CatalogInfo> extends PagingAndSortingRepository<T, String> {}
