/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.easymock.EasyMock;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreConfigurationListener;
import org.geowebcache.config.BlobStoreConfigurationTest;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.ConfigurationPersistenceException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import stratus.gwc.redis.repository.BlobStoreRepository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.geowebcache.util.TestUtils.isPresent;
import static org.geowebcache.util.TestUtils.notPresent;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Base for conformance tests of BlobStorConfigurations based on Redis
 * @author smithkm
 *
 */
public abstract class RedisBlobStoreConfigurationConformanceTest
        extends BlobStoreConfigurationTest {
    
    @Autowired
    protected RedisGWCTestSupport redisTestSupport;
    
    private boolean failNextRead = false;
    
    private boolean failNextWrite = false;
    
    @After
    public void tearDown() {
        // Clear the gwc config
        redisTestSupport.repository.flush();
    }
    
    static final Collection<String> readMethods = Arrays.asList("findAll", "findById", "exists",
            "count");
    
    static final Collection<String> writeMethods = Arrays.asList("save", "delete", "deleteAll");
    
    /**
     * Wrap a BlobStoreRepository with a proxy that throws exceptions if failNextRead or failNextWrite are set
     */
    protected BlobStoreRepository wrapForExceptions(final BlobStoreRepository real) {
        BlobStoreRepository proxy = (BlobStoreRepository) Proxy.newProxyInstance(
                BlobStoreRepository.class.getClassLoader(),
                new Class<?>[] { BlobStoreRepository.class, PagingAndSortingRepository.class,
                        CrudRepository.class, Repository.class },
                (Object iproxy, Method method, Object[] args) -> {
                    if (readMethods.contains(method.getName())) {
                        if (failNextRead) {
                            failNextRead = false;
                            throw new DataAccessException("TEST EXCEPTION ON READ") {
                                /** serialVersionUID */
                                private static final long serialVersionUID = 1L;
                            };
                        }
                    } else if (writeMethods.contains(method.getName())) {
                        if (failNextWrite) {
                            failNextWrite = false;
                            throw new DataAccessException("TEST EXCEPTION ON WRITE") {
                                /** serialVersionUID */
                                private static final long serialVersionUID = 1L;
                            };
                        }
                    } else {
                        Assert.fail(
                                method.getName() + " is not in the list of read or write methods.");
                    }
                    try {
                        return method.invoke(real, args);
                    } catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
                });
        return proxy;
    }
    
    @Override
    public void failNextRead() {
        this.failNextRead = true;
    }
    
    @Override
    public void failNextWrite() {
        this.failNextWrite = true;
    }
    
    // TODO we should probably up-port these listener tests to community
    
    @Test
    public void testListenerHearsAdd() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        listener.handleAddBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.addInfo(this.config, goodInfo);
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerHearsRemove() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        listener.handleRemoveBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.removeInfo(this.config, "test");
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerHearsModify() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        listener.handleModifyBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.doModifyInfo(goodInfo, 2);
        this.config.modifyBlobStore(goodInfo);
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerHearsRename() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        BlobStoreInfo expectedInfo = this.getGoodInfo("newName", 1);
        this.addInfo(this.config, goodInfo);
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        listener.handleRenameBlobStore(EasyMock.eq("test"), EasyMock.eq(expectedInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.config.renameBlobStore("test", "newName");
        EasyMock.verify(listener);
    }
    
    @Test
    public void testMultipleListenersHearAdd() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener1",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener2",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        listener1.handleAddBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        listener2.handleAddBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener1, listener2);
        this.addInfo(this.config, goodInfo);
        EasyMock.verify(listener1, listener2);
    }
    
    @Test
    public void testRemoveListener() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener1",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener2",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        this.config.removeBlobStoreListener(listener1);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        listener2.handleAddBlobStore(EasyMock.eq(goodInfo));
        EasyMock.expectLastCall().once();
        EasyMock.replay(listener1, listener2);
        this.addInfo(this.config, goodInfo);
        EasyMock.verify(listener1, listener2);
    }
    
    @Test
    public void testListenerDoesntHearFailureToAddBadInfo() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        BlobStoreInfo badInfo = this.getBadInfo("test", 1);
        // listener.handleAddBlobStore(EasyMock.eq(badInfo)); EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        try {
            this.addInfo(this.config, badInfo);
        } catch (IllegalArgumentException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToAddDuplicate() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        BlobStoreInfo goodInfo2 = this.getGoodInfo("test", 2);
        // listener.handleAddBlobStore(EasyMock.eq(badInfo)); EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.config.addBlobStoreListener(listener);
        try {
            this.addInfo(this.config, goodInfo2);
        } catch (IllegalArgumentException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToAddDueToBackend() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        // listener.handleAddBlobStore(EasyMock.eq(badInfo)); EasyMock.expectLastCall().once();
        EasyMock.replay(listener);
        this.failNextWrite();
        try {
            this.addInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToModifyBadInfo() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        this.config.addBlobStoreListener(listener);
        BlobStoreInfo badInfo = this.getBadInfo("test", 1);
        EasyMock.replay(listener);
        try {
            this.modifyInfo(this.config, badInfo);
        } catch (IllegalArgumentException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToModifyDoesntExist() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        EasyMock.replay(listener);
        try {
            this.modifyInfo(this.config, goodInfo);
        } catch (NoSuchElementException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToModifyDueToBackend() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        this.config.addBlobStoreListener(listener);
        goodInfo = this.getGoodInfo("test", 2);
        EasyMock.replay(listener);
        this.failNextWrite();
        try {
            this.modifyInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToRemoveDoesntExist() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        EasyMock.replay(listener);
        try {
            this.removeInfo(this.config, "test");
        } catch (NoSuchElementException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToRemoveDueToBackend() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        this.config.addBlobStoreListener(listener);
        EasyMock.replay(listener);
        this.failNextWrite();
        try {
            this.removeInfo(this.config, "test");
        } catch (ConfigurationPersistenceException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToRenameDoesntExist() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        this.config.addBlobStoreListener(listener);
        EasyMock.replay(listener);
        try {
            this.renameInfo(this.config, "test", "test2");
        } catch (NoSuchElementException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToRenameDuplicate() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        BlobStoreInfo goodInfo2 = this.getGoodInfo("test2", 1);
        this.addInfo(this.config, goodInfo2);
        this.config.addBlobStoreListener(listener);
        EasyMock.replay(listener);
        try {
            this.renameInfo(this.config, "test", "test2");
        } catch (IllegalArgumentException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    @Test
    public void testListenerDoesntHearFailureToRenameDueToBackend() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        this.config.addBlobStoreListener(listener);
        EasyMock.replay(listener);
        this.failNextWrite();
        try {
            this.removeInfo(this.config, "test");
        } catch (ConfigurationPersistenceException ex) {
            // Do Nothing
        }
        EasyMock.verify(listener);
    }
    
    // Exceptions during add handlers
    
    @Test
    public void testExceptionInAddListenerIsWrapped() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.config.addBlobStoreListener(listener);
        listener.handleAddBlobStore(goodInfo);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", sameInstance(ex))));
        
        this.addInfo(this.config, goodInfo);
    }
    
    @Test
    public void testExceptionInAddListenerNotRolledBack() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.config.addBlobStoreListener(listener);
        listener.handleAddBlobStore(goodInfo);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        try {
            this.addInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex2) {
            // Do Nothing
        }
        
        assertThat(this.getInfo(config, "test"), isPresent(equalTo(goodInfo)));
    }
    
    @Test
    public void testExceptionInAddListenerDoesntBlockOtherListeners() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        GeoWebCacheException ex1 = new GeoWebCacheException("TEST1");
        listener1.handleAddBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleAddBlobStore(goodInfo);EasyMock.expectLastCall().once();

        EasyMock.replay(listener1, listener2);
        
        try {
            this.addInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex3) {
            // Do Nothing
        }
        
        EasyMock.verify(listener2);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExceptionInAddListenerRecordsSuppressedExceptions() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        Exception ex1 = new GeoWebCacheException("TEST1");
        Exception ex2 = new IOException("TEST2");
        listener1.handleAddBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleAddBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex2);
        
        EasyMock.replay(listener1, listener2);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", allOf(
                        sameInstance(ex2),
                        hasProperty("suppressed", arrayContainingInAnyOrder(sameInstance(ex1)))))));
        
        this.addInfo(this.config, goodInfo);
    }
    // Exceptions during modify handlers
    
    @Test
    public void testExceptionInModifyListenerIsWrapped() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForModify();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", sameInstance(ex))));
        
        this.modifyInfo(this.config, goodInfo);
    }
    
    /**
     * Set up the configuration for a modify test with an info named "test".
     * @return an info named "test" with a different value
     * @throws Exception
     */
    protected BlobStoreInfo prepForModify() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        goodInfo = this.getGoodInfo("test", 2);
        return goodInfo;
    }
    
    @Test
    public void testExceptionInModifyListenerNotRolledBack() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForModify();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        try {
            this.modifyInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex2) {
            // Do Nothing
        }
        
        assertThat(this.getInfo(config, "test"), isPresent(infoEquals(2)));
    }
    
    @Test
    public void testExceptionInModifyListenerDoesntBlockOtherListeners() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForModify();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        GeoWebCacheException ex1 = new GeoWebCacheException("TEST1");
        listener1.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().once();

        EasyMock.replay(listener1, listener2);
        
        try {
            this.modifyInfo(this.config, goodInfo);
        } catch (ConfigurationPersistenceException ex3) {
            // Do Nothing
        }
        
        EasyMock.verify(listener2);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExceptionInModifyListenerRecordsSuppressedExceptions() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForModify();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        Exception ex1 = new GeoWebCacheException("TEST1");
        Exception ex2 = new IOException("TEST2");
        listener1.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleModifyBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex2);
        
        EasyMock.replay(listener1, listener2);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", allOf(
                        sameInstance(ex2),
                        hasProperty("suppressed", arrayContainingInAnyOrder(sameInstance(ex1)))))));
        
        this.modifyInfo(this.config, goodInfo);
    }
    
    // Exceptions during rename handlers
    /**
     * Set up the configuration for a rename test with an info named "test".
     * @return an info named "test2"
     * @throws Exception
     */
    protected BlobStoreInfo prepForRename() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        goodInfo = this.getGoodInfo("test2", 1);
        return goodInfo;
    }
    
    @Test
    public void testExceptionInRenameListenerIsWrapped() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRename();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", sameInstance(ex))));
        
        this.renameInfo(this.config, "test", "test2");
    }
    
    @Test
    public void testExceptionInRenameListenerNotRolledBack() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRename();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        try {
            this.renameInfo(this.config, "test", "test2");
        } catch (ConfigurationPersistenceException ex2) {
            // Do Nothing
        }
        
        assertThat(this.getInfo(config, "test2"), isPresent(infoEquals(goodInfo)));
    }
    
    @Test
    public void testExceptionInRenameListenerDoesntBlockOtherListeners() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRename();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        GeoWebCacheException ex1 = new GeoWebCacheException("TEST1");
        listener1.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().once();

        EasyMock.replay(listener1, listener2);
        
        try {
            this.renameInfo(this.config, "test", "test2");
        } catch (ConfigurationPersistenceException ex3) {
            // Do Nothing
        }
        
        EasyMock.verify(listener2);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExceptionInRenameListenerRecordsSuppressedExceptions() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRename();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        Exception ex1 = new GeoWebCacheException("TEST1");
        Exception ex2 = new IOException("TEST2");
        listener1.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleRenameBlobStore("test", goodInfo);EasyMock.expectLastCall().andThrow(ex2);
        
        EasyMock.replay(listener1, listener2);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", allOf(
                        sameInstance(ex2),
                        hasProperty("suppressed", arrayContainingInAnyOrder(sameInstance(ex1)))))));
        
        this.renameInfo(this.config, "test", "test2");
    }
    
    // Exceptions during remove handlers
    
    /**
     * Set up the configuration for a remove test with an info named "test".
     * @return the info that was added
     * @throws Exception
     */
    protected BlobStoreInfo prepForRemove() throws Exception {
        BlobStoreInfo goodInfo = this.getGoodInfo("test", 1);
        this.addInfo(this.config, goodInfo);
        return goodInfo;
    }
    
    @Test
    public void testExceptionInRemoveListenerIsWrapped() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRemove();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", sameInstance(ex))));
        
        this.removeInfo(this.config, "test");
    }
    
    @Test
    public void testExceptionInRemoveListenerNotRolledBack() throws Exception {
        BlobStoreConfigurationListener listener = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRemove();
        
        this.config.addBlobStoreListener(listener);
        GeoWebCacheException ex = new GeoWebCacheException("TEST");
        listener.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex);
        EasyMock.replay(listener);
        
        try {
            this.removeInfo(this.config, "test");
        } catch (ConfigurationPersistenceException ex2) {
            // Do Nothing
        }
        
        assertThat(this.getInfo(config, "test"), notPresent());
    }
    
    @Test
    public void testExceptionInRemoveListenerDoesntBlockOtherListeners() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRemove();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        GeoWebCacheException ex1 = new GeoWebCacheException("TEST1");
        listener1.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().once();

        EasyMock.replay(listener1, listener2);
        
        try {
            this.removeInfo(this.config, "test");
        } catch (ConfigurationPersistenceException ex3) {
            // Do Nothing
        }
        
        EasyMock.verify(listener2);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testExceptionInRemoveListenerRecordsSuppressedExceptions() throws Exception {
        BlobStoreConfigurationListener listener1 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreConfigurationListener listener2 = EasyMock.createMock("listener",
                BlobStoreConfigurationListener.class);
        BlobStoreInfo goodInfo = prepForRemove();
        this.config.addBlobStoreListener(listener1);
        this.config.addBlobStoreListener(listener2);
        Exception ex1 = new GeoWebCacheException("TEST1");
        Exception ex2 = new IOException("TEST2");
        listener1.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex1);
        listener2.handleRemoveBlobStore(goodInfo);EasyMock.expectLastCall().andThrow(ex2);
        
        EasyMock.replay(listener1, listener2);
        
        exception.expect(allOf(
                instanceOf(ConfigurationPersistenceException.class),
                hasProperty("cause", allOf(
                        sameInstance(ex2),
                        hasProperty("suppressed", arrayContainingInAnyOrder(sameInstance(ex1)))))));
        
        this.removeInfo(this.config, "test");
    }

}