/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.ModificationProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.internal.util.collections.Sets;
import stratus.redis.catalog.info.FeatureTypeInfoRedisImpl;
import stratus.redis.catalog.repository.*;
import stratus.redis.repository.RedisRepository;
import stratus.redis.repository.RedisSetRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test that the RedisCatalogFascade check that changes do not put the catalog in a state that can 
 * not be read by failing immediately and rolling back if possible..
 * @author Kevin Smith
 *
 */
public class RedisCatalogFacadeCorruptionProtectionTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    RedisCatalogFacade fascade;
    
    RedisRepository repository;
    RedisSetRepository setRepository;
    CatalogImpl catalog;
    WorkspaceRepository workspaceRepository;
    StoreRepository storeRepository;
    DataStoreRepository dataStoreRepository;
    CoverageStoreRepository coverageStoreRepository;
    WMSStoreRepository wmsStoreRepository;
    CoverageResourceRepository coverageResourceRepository;
    FeatureTypeResourceRepository featureTypeResourceRepository;
    WMSLayerResourceRepository wmsLayerResourceRepository;
    NamespaceRepository namespaceRepository;
    LayerRepository layerRepository;
    StyleRepository styleRepository;
    LayerGroupRepository layerGroupRepository;
    MapRepository mapRepository;
    WMTSLayerResourceRepository wmtsLayerRepository;
    WMTSStoreRepository wmtsStoreRepository;

    private IMocksControl control;
    
    @Before
    public void setup() throws Exception {
        control = EasyMock.createControl();
        repository = control.createMock("respository", RedisRepository.class);
        setRepository = control.createMock("setRepository", RedisSetRepository.class);
        catalog = EasyMock.createNiceMock("catalog", CatalogImpl.class);
        workspaceRepository = control.createMock("workspaceRepository", WorkspaceRepository.class);
        storeRepository = control.createMock("storeRepository", StoreRepository.class);
        dataStoreRepository = control.createMock("dataStoreRepository", DataStoreRepository.class);
        coverageStoreRepository = control.createMock("coverageStoreRepository", CoverageStoreRepository.class);
        wmsStoreRepository = control.createMock("wmsStoreRepository", WMSStoreRepository.class);
        coverageResourceRepository = control.createMock("coverageResourceRepository", CoverageResourceRepository.class);
        featureTypeResourceRepository = control.createMock("featureTypeResourceRepository", FeatureTypeResourceRepository.class);
        wmsLayerResourceRepository = control.createMock("wmsLayerResourceRepository", WMSLayerResourceRepository.class);
        namespaceRepository = control.createMock("namespaceRepository", NamespaceRepository.class);
        layerRepository = control.createMock("layerRepository", LayerRepository.class);
        styleRepository = control.createMock("styleRepository", StyleRepository.class);
        layerGroupRepository = control.createMock("layerGroupRepository", LayerGroupRepository.class);
        mapRepository = control.createMock("mapRepository", MapRepository.class);
        wmtsLayerRepository = control.createMock("wmtsLayerRepository", WMTSLayerResourceRepository.class);
        control.checkOrder(true);
        
        EasyMock.expect(repository.getRedisSetRepository()).andStubReturn(setRepository);
        catalog.resolve(EasyMock.anyObject(CatalogInfo.class));EasyMock.expectLastCall().anyTimes();
        EasyMock.replay(catalog);
        
        fascade = new RedisCatalogFacade(repository, catalog, workspaceRepository, storeRepository, dataStoreRepository, coverageStoreRepository, wmsStoreRepository, coverageResourceRepository, featureTypeResourceRepository, wmsLayerResourceRepository, namespaceRepository, layerRepository, styleRepository, layerGroupRepository, mapRepository, wmtsLayerRepository, wmtsStoreRepository);
    }
    
    @After
    public void verify() throws Exception {
        control.verify();
    }
    
    @Test
    public void testSuccessOnAdd() throws Exception {
        FeatureTypeInfo ft = new FeatureTypeInfoImpl(catalog, "testFt");
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        ft.setStore(ds);
        
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.empty());
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), equalTo(ft));
            return capFt.getValue();
        });
        // Read it back to confirm it's OK
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andAnswer(() -> Optional.of(capFt.getValue()));

        control.replay();
        
        fascade.add(ft);
    }
    
    @Test
    public void testExceptionOnAdd() throws Exception {
        FeatureTypeInfo ft = new FeatureTypeInfoImpl(catalog, "testFt");
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        ft.setStore(ds);
        
        Exception ex = new IllegalStateException("This is a test");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.empty());
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), equalTo(ft));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex);
        // So delete the layer we added
        final String indexKey = "FeatureTypeInfo:testFt:idx";
        final String rootKey = "FeatureTypeInfo";
        final String primaryKey = "FeatureTypeInfo:testFt";
        EasyMock.expect(setRepository.getSetMembers(indexKey)).andReturn(Sets.newSet("key1", "key2"));
        repository.deleteKey("key1");EasyMock.expectLastCall();
        repository.deleteKey("key2");EasyMock.expectLastCall();
        repository.deleteKey(indexKey);EasyMock.expectLastCall();
        EasyMock.expect(setRepository.removeFromSet(rootKey, "testFt")).andReturn(1L);
        repository.deleteKey(primaryKey);EasyMock.expectLastCall();
        control.replay();
        
        // and propagate the exception
        exception.expectCause(sameInstance(ex));
        
        fascade.add(ft);
    }
    
    @Test
    public void testSuccessOnModify() throws Exception {
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        
        FeatureTypeInfoRedisImpl ftOld = new FeatureTypeInfoRedisImpl();
        ftOld.setId("testFt");
        ftOld.setCatalog(catalog);
        ftOld.setAbstract("Old");
        ftOld.setStore(ds);
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.of(ftOld)).times(2);
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), allOf(
                    hasProperty("id", equalTo("testFt")),
                    hasProperty("abstract", equalTo("New"))));
            return capFt.getValue();
        });
        // Read it back to confirm it's OK
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andAnswer(() -> Optional.of(capFt.getValue()));
        
        control.replay();
        
        FeatureTypeInfo ft = fascade.getResource("testFt", FeatureTypeInfo.class);
        ft.setAbstract("New");
        
        fascade.save(ft);
    }
    
    @Test
    public void testExceptionOnModify() throws Exception {
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        
        FeatureTypeInfoRedisImpl ftOld = new FeatureTypeInfoRedisImpl();
        ftOld.setId("testFt");
        ftOld.setCatalog(catalog);
        ftOld.setAbstract("Old");
        ftOld.setStore(ds);
        
        Exception ex = new IllegalStateException("This is a test");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.of(ftOld)).times(2);
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), allOf(
                    hasProperty("id", equalTo("testFt")),
                    hasProperty("abstract", equalTo("New"))));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex);
        // So save the old version
        EasyMock.expect(featureTypeResourceRepository.save((FeatureTypeInfoRedisImpl)EasyMock.eq(ftOld))).andReturn(ftOld);
        
        control.replay();
        
        FeatureTypeInfo ft = fascade.getResource("testFt", FeatureTypeInfo.class);
        ft.setAbstract("New");
        
        // and propagate the exception
        exception.expectCause(sameInstance(ex));
        
        fascade.save(ft);
    }

    @Test
    public void testExceptionOnRollbackAdd() throws Exception {
        FeatureTypeInfo ft = new FeatureTypeInfoImpl(catalog, "testFt");
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        ft.setStore(ds);
        
        Exception ex1 = new IllegalStateException("This is a test");
        Exception ex2 = new IllegalStateException("Now things are really bad");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.empty());
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), equalTo(ft));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex1);
        // So delete the layer we added
        final String indexKey = "FeatureTypeInfo:testFt:idx";
        final String rootKey = "FeatureTypeInfo";
        final String primaryKey = "FeatureTypeInfo:testFt";
        EasyMock.expect(setRepository.getSetMembers(indexKey)).andReturn(Sets.newSet("key1", "key2"));
        repository.deleteKey("key1");EasyMock.expectLastCall();
        repository.deleteKey("key2");EasyMock.expectLastCall();
        repository.deleteKey(indexKey);EasyMock.expectLastCall();
        EasyMock.expect(setRepository.removeFromSet(rootKey, "testFt")).andReturn(1L);
        repository.deleteKey(primaryKey);EasyMock.expectLastCall().andThrow(ex2);
        control.replay();
        
        // and propagate the second exception, suppressing the first.
        exception.expectCause(both(sameInstance(ex2)).and(hasProperty("suppressed", arrayContaining(ex1))));
        
        fascade.add(ft);
    }
    
    @Test
    public void testExceptionOnRollbackModify() throws Exception {
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        
        FeatureTypeInfoRedisImpl ftOld = new FeatureTypeInfoRedisImpl();
        ftOld.setId("testFt");
        ftOld.setCatalog(catalog);
        ftOld.setAbstract("Old");
        ftOld.setStore(ds);
        
        Exception ex1 = new IllegalStateException("This is a test");
        Exception ex2 = new IllegalStateException("Now things are really bad");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.of(ftOld)).times(2);
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), allOf(
                    hasProperty("id", equalTo("testFt")),
                    hasProperty("abstract", equalTo("New"))));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex1);
        // So save the old version
        EasyMock.expect(featureTypeResourceRepository.save((FeatureTypeInfoRedisImpl)EasyMock.eq(ftOld))).andThrow(ex2);
        
        control.replay();
        
        FeatureTypeInfo ft = fascade.getResource("testFt", FeatureTypeInfo.class);
        ft.setAbstract("New");
        
        // and propagate the second exception, suppressing the first.
        exception.expectCause(both(sameInstance(ex2)).and(hasProperty("suppressed", arrayContaining(ex1))));
        
        fascade.save(ft);
    }

    // Not sure if this should work, but as long as it does, avoid corrupting the catalog
    @Test
    public void testSuccessOnAddBySave() throws Exception {
        FeatureTypeInfo ft = new FeatureTypeInfoImpl(catalog, "testFt");
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        ft.setStore(ds);
        
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.empty());
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), equalTo(ft));
            return capFt.getValue();
        });
        // Read it back to confirm it's OK
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andAnswer(() -> Optional.of(capFt.getValue()));
        
        control.replay();
        
        fascade.save(ModificationProxy.create(ft, FeatureTypeInfo.class));
    }
    
    // Not sure if this should work, but as long as it does, avoid corrupting the catalog
    @Test
    public void testExceptionOnAddBySave() throws Exception {
        FeatureTypeInfo ft = new FeatureTypeInfoImpl(catalog, "testFt");
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        ft.setStore(ds);
        
        Exception ex = new IllegalStateException("This is a test");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.empty());
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), equalTo(ft));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex);
        // So delete the layer we added
        final String indexKey = "FeatureTypeInfo:testFt:idx";
        final String rootKey = "FeatureTypeInfo";
        final String primaryKey = "FeatureTypeInfo:testFt";
        EasyMock.expect(setRepository.getSetMembers(indexKey)).andReturn(Sets.newSet("key1", "key2"));
        repository.deleteKey("key1");EasyMock.expectLastCall();
        repository.deleteKey("key2");EasyMock.expectLastCall();
        repository.deleteKey(indexKey);EasyMock.expectLastCall();
        EasyMock.expect(setRepository.removeFromSet(rootKey, "testFt")).andReturn(1L);
        repository.deleteKey(primaryKey);EasyMock.expectLastCall();
        control.replay();
        
        // and propagate the exception
        exception.expectCause(sameInstance(ex));
        
        fascade.save(ModificationProxy.create(ft, FeatureTypeInfo.class));
    }
    
    // Not sure if this should work, but as long as it does, avoid corrupting the catalog
    @Test
    public void testSuccessOnModifyByAdd() throws Exception {
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        
        FeatureTypeInfoRedisImpl ftOld = new FeatureTypeInfoRedisImpl();
        ftOld.setId("testFt");
        ftOld.setCatalog(catalog);
        ftOld.setAbstract("Old");
        ftOld.setStore(ds);
        FeatureTypeInfoImpl ftNew = new FeatureTypeInfoImpl(catalog, "testFt");
        ftNew.setAbstract("New");
        ftNew.setStore(ds);
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.of(ftOld)).times(1);
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), allOf(
                    hasProperty("id", equalTo("testFt")),
                    hasProperty("abstract", equalTo("New"))));
            return capFt.getValue();
        });
        // Read it back to confirm it's OK
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andAnswer(() -> Optional.of(capFt.getValue()));
        
        control.replay();
        
        fascade.add(ftNew);
    }
    
    // Not sure if this should work, but as long as it does, avoid corrupting the catalog
    @Test
    public void testExceptionOnModifyByAdd() throws Exception {
        DataStoreInfo ds = new DataStoreInfoImpl(catalog, "testDs");
        
        FeatureTypeInfoRedisImpl ftOld = new FeatureTypeInfoRedisImpl();
        ftOld.setId("testFt");
        ftOld.setCatalog(catalog);
        ftOld.setAbstract("Old");
        ftOld.setStore(ds);
        FeatureTypeInfoImpl ftNew = new FeatureTypeInfoImpl(catalog, "testFt");
        ftNew.setAbstract("New");
        ftNew.setStore(ds);
        
        Exception ex = new IllegalStateException("This is a test");
        
        Capture<FeatureTypeInfoRedisImpl> capFt = new Capture<>();
        // Check for an existing info
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andReturn(Optional.of(ftOld)).times(1);
        // Try to save
        EasyMock.expect(featureTypeResourceRepository.save(EasyMock.capture(capFt))).andAnswer(()->{
            assertThat(capFt.getValue(), allOf(
                    hasProperty("id", equalTo("testFt")),
                    hasProperty("abstract", equalTo("New"))));
            return capFt.getValue();
        });
        // Read it back, but there's a problem
        EasyMock.expect(featureTypeResourceRepository.findById("testFt")).andThrow(ex);
        // So save the old version
        EasyMock.expect(featureTypeResourceRepository.save((FeatureTypeInfoRedisImpl)EasyMock.eq(ftOld))).andReturn(ftOld);
        
        control.replay();
        
        // and propagate the exception
        exception.expectCause(sameInstance(ex));
        
        fascade.add(ftNew);
    }


}
