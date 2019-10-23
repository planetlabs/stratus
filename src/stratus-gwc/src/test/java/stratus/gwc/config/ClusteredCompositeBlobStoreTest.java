/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.easymock.EasyMock;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.locks.LockProvider;
import org.geowebcache.storage.BlobStore;
import org.geowebcache.storage.BlobStoreAggregator;
import org.geowebcache.storage.BlobStoreListener;
import org.geowebcache.storage.StorageException;
import org.junit.Test;

import java.util.Arrays;

public class ClusteredCompositeBlobStoreTest {
    private BlobStoreAggregator bsAgg;
    private TileLayerDispatcher tld;
    private BlobStoreInfo testBlobStoreInfo;
    private BlobStore testBlobStore1;
    private TileLayer testLayer;
    private BlobStore testBlobStore2;
    
    @Test
    public void testAddListener() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testMultipleOperationsInOneThreadAddListenerOnce() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).times(2);
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.delete("testLayer"); // Do it again
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testOperationsInSeparateThreadsEachAddListener() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        testBlobStore2 = EasyMock.createMock("testBlobStore2", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore2).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        EasyMock.expect(testBlobStore2.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        testBlobStore2.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testBlobStore2, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        Thread thread1 = new Thread(()-> {
            try {
                ccbs.delete("testLayer"); // Do something affecting testBlobStore
            } catch (StorageException ex) {
                throw new RuntimeException(ex);
            }
        });
        Thread thread2 = new Thread(()-> {
            try {
                ccbs.delete("testLayer"); // Do something affecting testBlobStore
            } catch (StorageException ex) {
                throw new RuntimeException(ex);
            }
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        EasyMock.verify(testBlobStore1, testBlobStore2);
    }
    
    @Test
    public void testAddListenerWhileCached() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).times(2);
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.addListener(testListener);
        ccbs.delete("testLayer"); // Do it again
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testRemoveListener() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        //testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        ccbs.removeListener(testListener);
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testRemoveListenerWhileCached() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        EasyMock.expect(testBlobStore1.removeListener(testListener)).andReturn(true).once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.addListener(testListener);
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.removeListener(testListener);
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testCleanUpAfterRequest() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        testBlobStore1.destroy();EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        ccbs.clearThreadCache();  // CacheFilter should call this after the request is handled
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testCleanUpAfterRequestDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        testBlobStore1.destroy();EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        ccbs.clearThreadCache();  // CacheFilter should call this after the request is handled
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testDeleteRequestNamed() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn("testBlobStore");
        EasyMock.expect(bsAgg.getBlobStore("testBlobStore")).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testDeleteRequestDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testDeleteRequestDefaultNonDefaultingAggregator() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", BlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(bsAgg.getBlobStores()).andStubReturn(Arrays.asList(testBlobStoreInfo));
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStoreInfo.isDefault()).andStubReturn(true);
        
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    @Test
    public void testAddListenerToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testMultipleOperationsInOneThreadAddListenerOnceToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).times(2);
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.delete("testLayer"); // Do it again
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testOperationsInSeparateThreadsEachAddListenerToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        testBlobStore2 = EasyMock.createMock("testBlobStore2", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore2).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        EasyMock.expect(testBlobStore2.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        testBlobStore2.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testBlobStore2, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        
        Thread thread1 = new Thread(()-> {
            try {
                ccbs.delete("testLayer"); // Do something affecting testBlobStore
            } catch (StorageException ex) {
                throw new RuntimeException(ex);
            }
        });
        Thread thread2 = new Thread(()-> {
            try {
                ccbs.delete("testLayer"); // Do something affecting testBlobStore
            } catch (StorageException ex) {
                throw new RuntimeException(ex);
            }
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        EasyMock.verify(testBlobStore1, testBlobStore2);
    }
    
    @Test
    public void testAddListenerWhileCachedToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).times(2);
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.addListener(testListener);
        ccbs.delete("testLayer"); // Do it again
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testRemoveListenerToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        //testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        ccbs.addListener(testListener);
        ccbs.removeListener(testListener);
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        
        EasyMock.verify(testBlobStore1);
    }
    
    @Test
    public void testRemoveListenerWhileCachedToDefault() throws Exception {
        bsAgg = EasyMock.createMock("bsAgg", DefaultingBlobStoreAggregator.class);
        tld = EasyMock.createMock("tld", TileLayerDispatcher.class);
        
        testBlobStoreInfo = EasyMock.createMock("testBlobStoreInfo", BlobStoreInfo.class);
        testBlobStore1 = EasyMock.createMock("testBlobStore1", BlobStore.class);
        
        testLayer = EasyMock.createMock("testLayer", TileLayer.class);
        EasyMock.expect(tld.getTileLayer("testLayer")).andStubReturn(testLayer);
        EasyMock.expect(testLayer.getBlobStoreId()).andStubReturn(null);
        EasyMock.expect(((DefaultingBlobStoreAggregator)bsAgg).getDefaultBlobStore()).andStubReturn(testBlobStoreInfo);
        EasyMock.expect(testBlobStoreInfo.createInstance(EasyMock.eq(tld), 
                EasyMock.anyObject(LockProvider.class))).andReturn(testBlobStore1).once();
        EasyMock.expect(testBlobStoreInfo.isEnabled()).andStubReturn(true);
        EasyMock.expect(testBlobStore1.delete("testLayer")).andReturn(true).once();
        
        BlobStoreListener  testListener = 
            EasyMock.createMock("testListener", BlobStoreListener.class);
        
        
        testBlobStore1.addListener(testListener);EasyMock.expectLastCall().once();
        EasyMock.expect(testBlobStore1.removeListener(testListener)).andReturn(true).once();
        
        EasyMock.replay(bsAgg, tld, testLayer, testBlobStoreInfo, testBlobStore1, testListener);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        
        ccbs.addListener(testListener);
        ccbs.delete("testLayer"); // Do something affecting testBlobStore
        ccbs.removeListener(testListener);
        
        EasyMock.verify(testBlobStore1);
    }

}
