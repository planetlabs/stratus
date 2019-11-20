/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.catalog.workspace;

import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import stratus.redis.catalog.info.WorkspaceInfoRedisImpl;

import java.util.List;

/**
 * @author joshfix
 * Created on 6/15/18
 */
public interface WorkspaceService {
    WorkspaceInfo getWorkspace(String id, String name);

    List<WorkspaceInfo> getWorkspaces();

    Page<WorkspaceInfoRedisImpl> getWorkspaces(Pageable page);

    Page<WorkspaceInfoRedisImpl> getWorkspacesByName(String name, Pageable page);

    List<SearchResult> getCatalogInfoByName(String name);
}
