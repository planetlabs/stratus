/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import org.springframework.data.repository.CrudRepository;
import stratus.redis.geoserver.info.ServiceInfoWrapper;

import java.util.List;
import java.util.Optional;

/**
 * @author joshfix
 * Created on 6/6/18
 *
 * @deprecated - Used by Stratus 1.2 catalogs only; preserved for backwards-compatibility
 */
@Deprecated
public interface ServiceInfoWrapperRepository extends CrudRepository<ServiceInfoWrapper, String>{

    Optional<ServiceInfoWrapper> findById(String id);
    ServiceInfoWrapper findByName(String name);
    ServiceInfoWrapper findByClazz(String clazz);
    ServiceInfoWrapper findByWorkspaceId(String workspaceId);
    ServiceInfoWrapper findByWorkspaceIdAndClazz(String workspaceId, String clazz);
    ServiceInfoWrapper findByIdAndClazz(String id, String clazz);
    ServiceInfoWrapper findByNameAndClazz(String name, String clazz);
    ServiceInfoWrapper findByNameAndWorkspaceIdAndClazz(String name, String workspaceId, String clazz);
    List<ServiceInfoWrapper> findAllByWorkspaceId(String workspaceId);

}
