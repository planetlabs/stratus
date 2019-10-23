/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import stratus.redis.geoserver.info.LoggingInfoRedisImpl;
import org.springframework.data.repository.CrudRepository;

/**
 * @author joshfix
 * Created on 6/5/18
 */
public interface LoggingInfoRepository extends CrudRepository<LoggingInfoRedisImpl, String>{}