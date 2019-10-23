/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.index.VirtualServiceIndex;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tbarsballe on 2017-02-08.
 */
public class VirtualServiceIndexTest extends AbstractRedisCatalogTest {

    @Autowired
    RedisLayerIndexFacade indexFacade;

    @Test
    public void testGetNothing() {
        VirtualServiceIndex index = indexFacade.getVirtualServiceIndex("layerGroup");
        assertNull(index);
        index = indexFacade.getVirtualServiceIndex("wsName");
        assertNull(index);

        List<VirtualServiceIndex> indices = indexFacade.getVirtualServiceIndices(Collections.singletonList("layerGroup"));
        assertEquals(0, indices.size());
        indices = indexFacade.getVirtualServiceIndices(Collections.singletonList("wsName"));
        assertEquals(0, indices.size());
    }

    @Test
    public void testIndexCreated() {
        addLayerGroup();
        VirtualServiceIndex index = indexFacade.getVirtualServiceIndex("wsName");
        assertNotNull(index);
        assertEquals(VirtualServiceIndex.Type.WORKSPACE, index.getType());
        assertEquals(1, index.getLayers().size());
        assertEquals("wsName:ftName", index.getLayers().iterator().next());

        index = indexFacade.getVirtualServiceIndex("layerGroup");
        assertNotNull(index);
        assertEquals(VirtualServiceIndex.Type.LAYER_GROUP, index.getType());
        assertEquals(1, index.getLayers().size());
        assertEquals("wsName:ftName", index.getLayers().iterator().next());
    }

    @Test
    public void testLayerGroupRenamed() {
        addLayerGroup();

        LayerGroupInfo lg = catalog.getLayerGroupByName("layerGroup");
        lg.setName("newLayerGroup");
        catalog.save(lg);
        VirtualServiceIndex index = indexFacade.getVirtualServiceIndex("newLayerGroup");
        assertNotNull(index);
        index = indexFacade.getVirtualServiceIndex("layerGroup");
        assertNull(index);

        //global to local: delete index
        lg = catalog.getLayerGroupByName("newLayerGroup");
        lg.setWorkspace(ws);
        catalog.save(lg);
        index = indexFacade.getVirtualServiceIndex("wsName:newLayerGroup");
        assertNull(index);
        index = indexFacade.getVirtualServiceIndex("newLayerGroup");
        assertNull(index);

        //local to global - create index
        lg = catalog.getLayerGroupByName("wsName:newLayerGroup");
        lg.setWorkspace(null);
        catalog.save(lg);
        index = indexFacade.getVirtualServiceIndex("wsName:newLayerGroup");
        assertNull(index);
        index = indexFacade.getVirtualServiceIndex("newLayerGroup");
        assertNotNull(index);
    }

    @Test
    public void testLayerGroupRenamedToWorkspace() {
        addLayerGroup();
        VirtualServiceIndex index = indexFacade.getVirtualServiceIndex("wsName");
        assertNotNull(index);
        assertEquals(VirtualServiceIndex.Type.WORKSPACE, index.getType());

        LayerGroupInfo lg = catalog.getLayerGroupByName("layerGroup");
        lg.setName("wsName");
        catalog.save(lg);
        index = indexFacade.getVirtualServiceIndex("wsName");
        assertNotNull(index);
        assertEquals(VirtualServiceIndex.Type.WORKSPACE, index.getType());
        index = indexFacade.getVirtualServiceIndex("layerGroup");
        assertNull(index);
    }

    @Test
    public void testWorkspaceRenamedToLayerGroup() {
        addLayerGroup();

        WorkspaceInfo ws = catalog.getWorkspaceByName("wsName");
        ws.setName("layerGroup");
        catalog.save(ws);
        VirtualServiceIndex index = indexFacade.getVirtualServiceIndex("layerGroup");
        assertNotNull(index);
        assertEquals(VirtualServiceIndex.Type.WORKSPACE, index.getType());
        index = indexFacade.getVirtualServiceIndex("wsName");
        assertNull(index);
    }
}
