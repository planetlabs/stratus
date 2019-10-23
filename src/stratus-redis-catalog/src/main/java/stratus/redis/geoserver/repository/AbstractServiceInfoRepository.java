/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import stratus.redis.geoserver.info.AbstractServiceInfoRedisImpl;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Base class for ServiceInfoRepositories
 * @param <T>
 */
public interface AbstractServiceInfoRepository<T  extends AbstractServiceInfoRedisImpl> extends CrudRepository<T, String> {

    Optional<T> findById(String id);
    T findByName(String name);
    T findByClazz(String clazz);
    T findByWorkspaceId(String workspaceId);
    T findByWorkspaceIdAndClazz(String workspaceId, String clazz);
    T findByIdAndClazz(String id, String clazz);
    T findByNameAndClazz(String name, String clazz);
    T findByNameAndWorkspaceIdAndClazz(String name, String workspaceId, String clazz);
    List<T> findAllByWorkspaceId(String workspaceId);
}
