/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.CatalogImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.catalog.AbstractRedisCatalogTest;
import stratus.redis.catalog.impl.CatalogInfoConvert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tbarsballe on 2017-02-08.
 */
public class RedisLayerIndexFacadeTest extends AbstractRedisCatalogTest {

    @Autowired
    RedisLayerIndexFacade facade;

    @Test
    public void testGetLayerGroupIndicesNames() {
        addLayerGroup();
        List<LayerGroupIndex> indices = facade.getLayerGroupIndices(Collections.singletonList("layerGroup"));

        assertEquals(1, indices.size());
        assertEquals("layerGroup", indices.get(0).getName());
        assertEquals(1, indices.get(0).getLayers().size());
        assertEquals("wsName:ftName", indices.get(0).getLayers().iterator().next());
    }

    @Test
    public void testGetLayerIndices() {
        addLayerGroup();
        List<LayerIndex> indices = facade.getLayerIndices(Collections.singletonList("wsName:ftName"));

        List<IndexEntry> ids = new ArrayList<>();

        for (LayerIndex index : indices) {
            ids.addAll(index.getIds());
        }

        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(l.getClass()), l.getId())));
        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(s.getClass()), s.getId())));
        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(ft.getClass()), ft.getId())));
        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(ds.getClass()), ds.getId())));
        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(ws.getClass()), ws.getId())));
        assertTrue(ids.contains(new IndexEntry(CatalogInfoConvert.root(ns.getClass()), ns.getId())));
    }

    @Test
    public void testGetLayerNames() {
        addLayerGroup();
        List<LayerGroupIndex> indices = facade.getLayerGroupIndices(Collections.singletonList("layerGroup"));
        List<String> names = facade.getLayerNames(Collections.singletonList("layerGroup"), indices);
        assertEquals(1, names.size());
        assertEquals("wsName:ftName", names.get(0));
    }

    @Test
    public void testGetCatalogInfos() {
        addLayerGroup();
        List<LayerIndex> indices = facade.getLayerIndices(Collections.singletonList("wsName:ftName"));

        List<IndexEntry> ids = new ArrayList<>();

        for (LayerIndex index : indices) {
            ids.addAll(index.getIds());
        }
        CatalogImpl catalog = new CatalogImpl();
        CachingCatalogFacade cachingFacade = new CachingCatalogFacade(catalog, catalog.getFacade());
        catalog.setFacade(cachingFacade);
        List<Info> infos = facade.loadCatalogInfos(ids, cachingFacade);

        int uniqueInfos = 0;
        for (Info info : infos) {
            if (info instanceof LayerInfo) {
                uniqueInfos+=1;
                assertEquals(info.getId(), l.getId());
            } else if (info instanceof StyleInfo) {
                uniqueInfos+=2;
                assertEquals(info.getId(), s.getId());
            } else if (info instanceof FeatureTypeInfo) {
                uniqueInfos+=4;
                assertEquals(info.getId(), ft.getId());
            } else if (info instanceof DataStoreInfo) {
                uniqueInfos+=8;
                assertEquals(info.getId(), ds.getId());
            } else if (info instanceof WorkspaceInfo) {
                uniqueInfos+=16;
                assertEquals(info.getId(), ws.getId());
            } else if (info instanceof NamespaceInfo) {
                uniqueInfos+=32;
                assertEquals(info.getId(), ns.getId());
            }
        }
        assertEquals(uniqueInfos, 63);

    }
}
