/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import org.springframework.data.repository.CrudRepository;
import stratus.redis.geoserver.info.SettingsInfoRedisImpl;

/**
 * @author joshfix
 * Created on 6/5/18
 */
public interface SettingsInfoRepository extends CrudRepository<SettingsInfoRedisImpl, String> {

    SettingsInfoRedisImpl findByWorkspaceId(String workspaceId);
}