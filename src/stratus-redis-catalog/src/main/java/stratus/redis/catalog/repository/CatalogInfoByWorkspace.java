/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import org.geoserver.catalog.CatalogInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogInfoByWorkspace <T extends CatalogInfo> extends CatalogInfoByName<T> {

	T findByWorkspaceIdAndName(String workspaceId, String name);

	List<T> findListByWorkspaceId(String workspaceId);

	Page<T> findListByWorkspaceId(String workspaceId, Pageable page);

}