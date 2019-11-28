/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.repository;

import org.springframework.data.repository.CrudRepository;
import stratus.gwc.redis.data.ServerConfigurationRedisImpl;

/**
 * SpringData repository for server configuration objects
 *
 * @see ServerConfigurationRedisImpl
 */
public interface ServerRepository extends CrudRepository<ServerConfigurationRedisImpl, String> {

}
