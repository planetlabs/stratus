/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import stratus.redis.geoserver.info.SettingsInfoRedisImpl;
import org.springframework.data.repository.CrudRepository;

/**
 * @author joshfix
 * Created on 6/5/18
 */
public interface SettingsInfoRepository extends CrudRepository<SettingsInfoRedisImpl, String> {

    SettingsInfoRedisImpl findByWorkspaceId(String workspaceId);
}