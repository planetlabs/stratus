/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import org.springframework.data.repository.CrudRepository;
import stratus.redis.geoserver.info.LoggingInfoRedisImpl;

/**
 * @author joshfix
 * Created on 6/5/18
 */
public interface LoggingInfoRepository extends CrudRepository<LoggingInfoRedisImpl, String>{}