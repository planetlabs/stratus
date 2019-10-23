/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;


import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import com.google.common.collect.ImmutableSet;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.NamespaceInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.geoserver.catalog.util.CloseableIterator;
import org.geotools.data.property.PropertyDataStoreFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisLayerIndexFacade.class, CacheProperties.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CatalogImplWithRedisFacadeTest extends org.geoserver.catalog.impl.CatalogImplTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    private CatalogFacade facade;

    @After
    public void tearDown() {
        facade.dispose();
        redisTestSupport.repository.flush();
    }

    @Override
    protected Catalog createCatalog() {
        CatalogImpl catalogImpl = new CatalogImpl();
        facade = redisTestSupport.createCatalogFacade(catalogImpl);
        catalogImpl.setFacade(facade);
        return catalogImpl;
    }

    /**
     * Rewriting this test as the way it is written it only worked because the default catalog shared the underlying
     * namespace objects.
     */
    @Test
    @Override
    public void testAddIsolatedNamespace() {
        // create non isolated namespace
        NamespaceInfo namespace1 = new NamespaceInfoImpl();
        namespace1.setPrefix("isolated_namespace_1");
        namespace1.setURI("http://www.isolated_namespace.com");
        // create isolated namespace with the same URI
        NamespaceInfo namespace2 = new NamespaceInfoImpl();
        namespace2.setPrefix("isolated_namespace_2");
        namespace2.setURI("http://www.isolated_namespace.com");
        namespace2.setIsolated(true);
        try {
            // add the namespaces to the catalog
            catalog.add(namespace1);
            catalog.add(namespace2);
            // retrieve the non isolated namespace by prefix
            NamespaceInfo foundNamespace1 = catalog.getNamespaceByPrefix("isolated_namespace_1");
            assertThat(foundNamespace1.getPrefix(), is("isolated_namespace_1"));
            assertThat(foundNamespace1.getURI(), is("http://www.isolated_namespace.com"));
            assertThat(foundNamespace1.isIsolated(), is(false));
            namespace1 = foundNamespace1;
            // retrieve the isolated namespace by prefix
            NamespaceInfo foundNamespace2 = catalog.getNamespaceByPrefix("isolated_namespace_2");
            assertThat(foundNamespace2.getPrefix(), is("isolated_namespace_2"));
            assertThat(foundNamespace2.getURI(), is("http://www.isolated_namespace.com"));
            assertThat(foundNamespace2.isIsolated(), is(true));
            namespace2 = foundNamespace2;
            // retrieve the namespace by URI, the non isolated one should be returned
            NamespaceInfo foundNamespace3 = catalog.getNamespaceByURI("http://www.isolated_namespace.com");
            assertThat(foundNamespace3.getPrefix(), is("isolated_namespace_1"));
            assertThat(foundNamespace3.getURI(), is("http://www.isolated_namespace.com"));
            assertThat(foundNamespace3.isIsolated(), is(false));
            // remove the non isolated namespace
            catalog.remove(foundNamespace1);
            // retrieve the namespace by URI, NULL should be returned
            NamespaceInfo foundNamespace4 = catalog.getNamespaceByURI("http://www.isolated_namespace.com");
            assertThat(foundNamespace4, nullValue());
        } finally {
            // remove the namespaces
            catalog.remove(namespace1);
            catalog.remove(namespace2);
        }
    }

    /**
     * Rewriting this test as the way it is written it only worked because the default catalog shared the underlying
     * workspace object.
     */
    @Test
    @Override
    public void testAddIsolatedWorkspace() {
        // create isolated workspace
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName("isolated_workspace");
        workspace.setIsolated(true);
        try {
            // add it to the catalog
            catalog.add(workspace);
            // retrieve the isolated workspace
            WorkspaceInfo foundWorkspace = catalog.getWorkspaceByName("isolated_workspace");
            assertThat(foundWorkspace.isIsolated(), is(true));
            workspace = foundWorkspace;
        } finally {
            // remove the isolated workspace
            catalog.remove(workspace);
        }
    }

    /**
     * Rewriting this test, because the original only works by accident.
     * the two tests at the end are done with an out-dated catalog info objects
     * in the original it does work because the object is stored in memory and isn't duplicated
     *
     * this can be removed if the original is corrected.
     */
    @Test
    @Override
    public void testFullTextSearchAddedKeyword() {
        ft.getKeywords().add(new Keyword("air_temp"));
        ft.getKeywords().add(new Keyword("temperatureAir"));

        l.setResource(ft);
        addLayer();

        LayerInfo lproxy = catalog.getLayer(l.getId());
        FeatureTypeInfo ftproxy = (FeatureTypeInfo)lproxy.getResource();

        ftproxy.getKeywords().add(new Keyword("newKeyword"));
        catalog.save(ftproxy);

        Filter filter = Predicates.fullTextSearch("newKeyword");
        assertEquals(newHashSet(ftproxy), asSet(catalog.list(FeatureTypeInfo.class, filter)));
        assertEquals(newHashSet(lproxy), asSet(catalog.list(LayerInfo.class, filter)));
    }
    
    @Test
    public void testResourceWithEnvelopes() throws Exception {
        addCoverageStore();
        addNamespace();

    	CatalogFactory factory = catalog.getFactory();
    	
    	CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");

    	CoverageInfo ci = factory.createCoverage();
    	ci.setName("ciNameWithEnvelope");
    	ci.setNamespace(ns);
    	ci.setStore(cs);
        ci.setLatLonBoundingBox(new ReferencedEnvelope(0,10,0,10, epsg4326));
        ci.setNativeBoundingBox(new ReferencedEnvelope(0,20,0,20, epsg4326));
        ci.setNativeCRS(epsg4326);
        
        catalog.add(ci);
        
        CoverageInfo ci2 = catalog.getCoverageByName("ciNameWithEnvelope");
        assertTrue(CRS.equalsIgnoreMetadata(epsg4326, ci2.getNativeCRS()));
        assertEquals(ci.getLatLonBoundingBox(), ci2.getLatLonBoundingBox());
        assertEquals(ci.getNativeBoundingBox(), ci2.getNativeBoundingBox());
    }
    
    @Test
    public void testAddLayerGroupWithEnvelope() throws Exception {
    	addLayer();
    	CoordinateReferenceSystem epsg4326 = CRS.decode("EPSG:4326");
    	lg.setBounds(new ReferencedEnvelope(0,10,0,10, epsg4326));
        catalog.add(lg);
        
        LayerGroupInfo lg2 = catalog.getLayerGroup(lg.getId());
        assertEquals(lg.getBounds(), lg2.getBounds());
    }

    private <T> Set<T> asSet(CloseableIterator<T> list) {
        ImmutableSet<T> set;
        try {
            set = ImmutableSet.copyOf(list);
        } finally {
            list.close();
        }
        return set;
    }
    
    @Test
    public void testGetLayerWithDefaultStyleNull() throws Exception {
    	addFeatureType();
    	
    	CatalogFactory factory = catalog.getFactory();
    	
    	l = factory.createLayer();
        l.setResource( ft );
        l.setEnabled(true);
        l.setDefaultStyle(null);
        
        catalog.add(l);
        
        List<LayerInfo> list = catalog.getLayers();
        
        assertEquals(1, list.size());
    }

    @Test
    public void testGetLayerGroupWithNullStyles() throws Exception {
        addFeatureType();

        CatalogFactory factory = catalog.getFactory();

        l = factory.createLayer();
        l.setResource( ft );
        l.setEnabled(true);
        l.setDefaultStyle(null);

        catalog.add(l);

        lg = factory.createLayerGroup();
        lg.setName("testLg");
        lg.getLayers().add(l);
        lg.getStyles().add(null);
        lg.getLayers().add(l);
        lg.getStyles().add(null);

        catalog.add(lg);

        LayerGroupInfo layerGroup = catalog.getLayerGroupByName(lg.getName());

        assertEquals(2, layerGroup.getLayers().size());
        assertEquals(2, layerGroup.getStyles().size());
    }
    
    @Test
    public void testDataStoreWithFile() throws Exception {
    	addWorkspace();
    	CatalogFactory factory = catalog.getFactory();
    	ds = factory.createDataStore();
        ds.setEnabled(true);
        ds.setName( "dsName");
        ds.setDescription("dsDescription");
        ds.setWorkspace( ws );
        File file = new File("/path/to/dir");
        ds.getConnectionParameters().put(PropertyDataStoreFactory.DIRECTORY.key, file);
        
        catalog.add(ds);
                
        List<DataStoreInfo> list = catalog.getDataStores();
        
        assertEquals(1, list.size());
        assertEquals(file, list.get(0).getConnectionParameters().get(PropertyDataStoreFactory.DIRECTORY.key));
    }

    /**
     * PublishedInfo.class failed to be counted because it wasn't caught by type checks in the catalog.
     */
    @Test
    public void testCountPublishedInfo() {
        addLayer();
        assertEquals(1, catalog.count(PublishedInfo.class, Filter.INCLUDE));
    }


    /**
     * Verify that the catalog is still usable if it got in an inconsistnent state
     */
    @Test
    public void testUnresolvableProperty() {
        addLayer();
        StyleInfo style = catalog.getStyle(s.getId());
        style.setWorkspace(catalog.getWorkspace(ws.getId()));
        catalog.save(style);

        assertEquals(catalog.getStyles().size(), 1);
        String key = "WorkspaceInfo:"+ws.getId();
        assertTrue(redisTestSupport.repository.keyExists(key));
        redisTestSupport.repository.deleteKey(key);

        assertEquals(catalog.getStyles().size(), 1);


    }


}