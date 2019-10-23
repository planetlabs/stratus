/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.ThreadCachingBean;
import org.easymock.EasyMock;
import org.geoserver.platform.GeoServerExtensionsHelper;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.locks.LockProvider;
import org.geowebcache.storage.BlobStore;
import org.geowebcache.storage.BlobStoreAggregator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ClusteredCompositeBlobStoreWithCachingFilterTest {
    private BlobStoreAggregator bsAgg;
    private TileLayerDispatcher tld;
    private BlobStoreInfo testBlobStoreInfo;
    private BlobStore testBlobStore1;
    private TileLayer testLayer;
    private CachingFilter filter;
    
    @Rule
    public GeoServerExtensionsHelper.ExtensionsHelperRule extensions = 
            new GeoServerExtensionsHelper.ExtensionsHelperRule();
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void testDestroyCachedSubordinates() throws Exception {
        
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
        EasyMock.expect(testBlobStore1.getLayerMetadata("testLayer", "testMetaData")).andStubReturn("Blah");
        
        // Filter should cause CCBS to clean up subordinate stores at end of request. 
        testBlobStore1.destroy(); EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        extensions.singleton("compositeBlobStore", ccbs, 
                ClusteredCompositeBlobStore.class, BlobStore.class, ThreadCachingBean.class);
        
        FilterChain chain = (req, resp) -> {
            // cause a subbordinate store to be instantiated.
            ccbs.getLayerMetadata("testLayer", "testMetaData");
        };
        
        filter = new CachingFilter();
        
        ServletRequest req = new MockHttpServletRequest();
        ServletResponse resp = new MockHttpServletResponse();
        
        filter.doFilter(req, resp, chain);
        
        EasyMock.verify(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
    }
    
    @Test
    public void testDestroyCachedDefault() throws Exception {
        
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
        EasyMock.expect(testBlobStore1.getLayerMetadata("testLayer", "testMetaData")).andStubReturn("Blah");
        
        // Filter should cause CCBS to clean up subordinate stores at end of request. 
        testBlobStore1.destroy(); EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        extensions.singleton("compositeBlobStore", ccbs, 
                ClusteredCompositeBlobStore.class, BlobStore.class, ThreadCachingBean.class);
        
        FilterChain chain = (req, resp) -> {
            // cause a subbordinate store to be instantiated.
            ccbs.getLayerMetadata("testLayer", "testMetaData");
        };
        
        filter = new CachingFilter();
        
        ServletRequest req = new MockHttpServletRequest();
        ServletResponse resp = new MockHttpServletResponse();
        
        filter.doFilter(req, resp, chain);
        
        EasyMock.verify(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
    }
    
    @Test
    public void testDestroyCachedSubordinatesOnException() throws Exception {
        
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
        EasyMock.expect(testBlobStore1.getLayerMetadata("testLayer", "testMetaData")).andStubReturn("Blah");
        
        // Filter should cause CCBS to clean up subordinate stores at end of request. 
        testBlobStore1.destroy(); EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        extensions.singleton("compositeBlobStore", ccbs, 
                ClusteredCompositeBlobStore.class, BlobStore.class, ThreadCachingBean.class);
        
        FilterChain chain = (req, resp) -> {
            // cause a subbordinate store to be instantiated.
            ccbs.getLayerMetadata("testLayer", "testMetaData");
            throw new ServletException("TestException");
        };
        
        filter = new CachingFilter();
        
        ServletRequest req = new MockHttpServletRequest();
        ServletResponse resp = new MockHttpServletResponse();
        
        try {
            exception.expect(ServletException.class);
            filter.doFilter(req, resp, chain);
        } finally {
            EasyMock.verify(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        }
    }
    
    @Test
    public void testDestroyCachedDefaultOnException() throws Exception {
        
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
        EasyMock.expect(testBlobStore1.getLayerMetadata("testLayer", "testMetaData")).andStubReturn("Blah");
        
        // Filter should cause CCBS to clean up subordinate stores at end of request. 
        testBlobStore1.destroy(); EasyMock.expectLastCall().once();
        
        EasyMock.replay(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        
        ClusteredCompositeBlobStore ccbs = new ClusteredCompositeBlobStore(tld, bsAgg);
        extensions.singleton("compositeBlobStore", ccbs, 
                ClusteredCompositeBlobStore.class, BlobStore.class, ThreadCachingBean.class);
        
        FilterChain chain = (req, resp) -> {
            // cause a subbordinate store to be instantiated.
            ccbs.getLayerMetadata("testLayer", "testMetaData");
            throw new ServletException("TestException");
        };
        
        filter = new CachingFilter();
        
        ServletRequest req = new MockHttpServletRequest();
        ServletResponse resp = new MockHttpServletResponse();
        
        try {
            exception.expect(ServletException.class);
            filter.doFilter(req, resp, chain);
        } finally {
            EasyMock.verify(bsAgg, tld, testBlobStoreInfo, testBlobStore1, testLayer);
        }
    }
}
