/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import stratus.redis.index.LayerGroupIndex;
import stratus.redis.index.RedisLayerIndexFacade;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tbarsballe on 2017-02-08.
 */
public class LayerGroupIndexTest extends AbstractRedisCatalogTest {

    @Autowired
    RedisLayerIndexFacade indexFacade;

    @Test
    public void testGetNothing() {
        LayerGroupIndex index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNull(index);

        List<LayerGroupIndex> indices = indexFacade.getLayerGroupIndices(Collections.singletonList("layerGroup"));
        assertEquals(0, indices.size());
    }

    @Test
    public void testIndexCreated() {
        addLayerGroup();
        LayerGroupIndex index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNotNull(index);
        assertEquals(1, index.getLayers().size());
        assertEquals("wsName:ftName", index.getLayers().iterator().next());
    }

    @Test
    public void testLayerGroupRenamed() {
        addLayerGroup();

        LayerGroupInfo lg = catalog.getLayerGroupByName("layerGroup");
        lg.setName("newLayerGroup");
        catalog.save(lg);
        LayerGroupIndex index = indexFacade.getLayerGroupIndex("newLayerGroup");
        assertNotNull(index);
        index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNull(index);

        lg = catalog.getLayerGroupByName("newLayerGroup");
        lg.setWorkspace(ws);
        catalog.save(lg);
        index = indexFacade.getLayerGroupIndex("wsName:newLayerGroup");
        assertNotNull(index);
        index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNull(index);
    }

    @Test
    public void testNestedLayerGroup() {
        addLayerGroup();

        CatalogFactory factory = catalog.getFactory();

        FeatureTypeInfo ft2 = factory.createFeatureType();
        ft2.setEnabled(true);
        ft2.setName( "ftName2" );
        ft2.setAbstract( "ftAbstract" );
        ft2.setDescription( "ftDescription" );
        ft2.setStore( ds );
        ft2.setNamespace( ns );

        catalog.add(ft2);

        LayerInfo l2 = factory.createLayer();
        l2.setResource( ft2 );
        l2.setEnabled(true);
        l2.setDefaultStyle( s );

        catalog.add(l2);

        LayerGroupInfo lg2 = factory.createLayerGroup();
        lg2.setName("layerGroup2");
        lg2.getLayers().add(l2);
        lg2.getStyles().add(s);
        catalog.add(lg2);

        LayerGroupInfo lg = catalog.getLayerGroupByName("layerGroup");
        lg.getLayers().add(lg2);
        lg.getStyles().add(null);
        catalog.save(lg);

        LayerGroupIndex index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNotNull(index);
        assertEquals(2, index.getLayers().size());
        Iterator<String> layers = index.getLayers().iterator();
        assertEquals("wsName:ftName", layers.next());
        assertEquals("wsName:ftName2", layers.next());

        //test deletion
        lg2 = catalog.getLayerGroupByName("layerGroup2");
        ft2 = catalog.getFeatureTypeByName("ftName2");
        new CascadeDeleteVisitor(catalog).visit(lg2);
        new CascadeDeleteVisitor(catalog).visit(l2);
        new CascadeDeleteVisitor(catalog).visit(ft2);
        index = indexFacade.getLayerGroupIndex("layerGroup");
        assertNotNull(index);
        assertEquals(1, index.getLayers().size());
    }


}
