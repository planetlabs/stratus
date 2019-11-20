/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import stratus.wps.model.StratusExecutionStatus;

public interface StratusExecutionStatusRepository extends PagingAndSortingRepository<StratusExecutionStatus, String> {
}
