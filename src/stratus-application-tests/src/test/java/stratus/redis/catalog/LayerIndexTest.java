/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import stratus.redis.index.LayerIndex;
import stratus.redis.index.RedisLayerIndexFacade;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tbarsballe on 2017-02-08.
 */
public class LayerIndexTest extends AbstractRedisCatalogTest {

    @Autowired
    RedisLayerIndexFacade indexFacade;

    @Test
    public void testGetNothing() {
        LayerIndex index = indexFacade.getLayerIndex("wsName:ftName");
        assertNull(index);

        List<LayerIndex> indices = indexFacade.getLayerIndices(Collections.singletonList("wsName:ftName"));
        assertEquals(0, indices.size());
    }

    @Test
    public void testIndexCreated() {
        addLayer();
        LayerIndex index = indexFacade.getLayerIndex("wsName:ftName");
        assertNotNull(index);
        assertEquals(ws.getId(), index.getWorkspaceId().getId());
        assertEquals(ft.getId(), index.getResourceId().getId());
    }

    @Test
    public void testLayerRenamed() {
        addLayer();

        FeatureTypeInfo ft = catalog.getFeatureTypeByName("ftName");
        ft.setName("layerName");
        catalog.save(ft);
        LayerIndex index = indexFacade.getLayerIndex("wsName:ftName");
        assertNotNull(index);
        index = indexFacade.getLayerIndex("wsName:layerName");
        assertNull(index);
    }

    @Test
    public void testStylesChanged() {
        addLayer();

        LayerIndex index = indexFacade.getLayerIndex("wsName:ftName");
        assertNotNull(index);
        assertEquals(0, index.getStyleIds().size());

        LayerInfo l = catalog.getLayerByName("ftName");
        l.getStyles().add(catalog.getStyleByName(s.getName()));
        catalog.save(l);
        index = indexFacade.getLayerIndex("wsName:ftName");
        assertNotNull(index);
        assertEquals(1, index.getStyleIds().size());
        assertEquals(s.getId(), index.getStyleIds().get(0).getId());
    }
}
