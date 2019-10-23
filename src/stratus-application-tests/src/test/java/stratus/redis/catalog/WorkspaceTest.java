/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Basic GeoServer-Redis integration test for {@link WorkspaceInfo}
 *
 * Created by joshfix on 8/26/16.
 */

public class WorkspaceTest extends AbstractRedisCatalogTest {

    /**
     * Test verifies that that a data store that has been added to the catalog properly
     * saves all fields to redis and that the store retrieved from each store query method
     * is identical to the store originally saved to redis.
     */
    @Test
    public void testAddWorkspace() {
        template.getConnectionFactory().getConnection().flushAll();

        String workspaceName = "TestWorkspace";
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName(workspaceName);
        workspace = catalogFacade.add(workspace);

        WorkspaceInfo workspaceById = catalogFacade.getWorkspace(workspace.getId());
        assertThat(workspaceById).isEqualToIgnoringNullFields(workspace);

        WorkspaceInfo workspaceByName = catalogFacade.getWorkspaceByName(workspace.getName());
        assertThat(workspaceByName).isEqualToIgnoringNullFields(workspace);

        List<WorkspaceInfo> workspaces = catalogFacade.getWorkspaces();
        assertThat(workspaces).isNotNull();
        assertThat(workspaces, hasSize(1));
        assertThat(workspaces.get(0)).isEqualToIgnoringNullFields(workspace);
    }

    @Test
    public void testRenameWorkspace() {
        template.getConnectionFactory().getConnection().flushAll();

        String workspaceName = "TestWorkspace";
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName(workspaceName);
        workspace = catalogFacade.add(workspace);

        String newWorkspaceName = "TestWorkspace1";
        workspace.setName(newWorkspaceName);
        catalogFacade.save(workspace);

        WorkspaceInfo newWorkspace = catalogFacade.getWorkspace(workspace.getId());
        assertThat(newWorkspaceName).isEqualTo(newWorkspace.getName());
    }
}
