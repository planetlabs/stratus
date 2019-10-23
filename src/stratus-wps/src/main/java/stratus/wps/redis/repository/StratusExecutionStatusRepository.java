/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.redis.repository;

import stratus.wps.model.StratusExecutionStatus;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StratusExecutionStatusRepository extends PagingAndSortingRepository<StratusExecutionStatus, String> {
}
