/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import stratus.gwc.redis.data.GridSetRedisImpl;

/**
 * SpringData repository for GridSet objects
 *
 * @see GridSetRedisImpl
 * @author smithkm
 */
public interface GridSetRepository extends PagingAndSortingRepository<GridSetRedisImpl, String> {

}
