/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms;

import org.geoserver.data.test.SystemTestData;
import org.geoserver.ows.StratusOwsTestSupport;
import org.geoserver.platform.GeoServerExtensions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.ows.OWSCachingCallback;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wms.StratusWmsTestSupport;
import stratus.wms.WMSConfig;
import stratus.wms.WMSHandler;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Sanity tests to ensure the various handlers, callbacks, and other configuration added by Stratus is actually getting
 * used.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class,
        WMSConfig.class, RedisLayerIndexFacade.class, LayerIndexListener.class,
        CacheProperties.class, OWSCachingCallback.class, StratusWmsTestSupport.class,
        SimpleImportResourcesConfig.class, WMSInfoClassRegisteringBean.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class WMSConfigTest extends WMSTestSupport {

    @Autowired
    private StratusWmsTestSupport testSupport;

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        applicationContext.getBeanFactory().registerSingleton("dummyCachingHandler",
                new StratusOwsTestSupport.DummyCachingHandler());
        testSupport.configureWmsTestPreSetup(applicationContext, getCatalog());
        super.onSetUp(testData);
        testSupport.configureWmsTestPostSetup(applicationContext, getCatalog());
    }

    @Test
    public void testWMSHandlerConfigured() {
        assertEquals(1, GeoServerExtensions.extensions(WMSHandler.class).size());
    }

    @Test
    public void testCallbacksConfigured() throws Exception {
        StratusOwsTestSupport.assertOwsCachingCallbackConfigured();
        //Do a getCapabilities request
        get("wms?request=getCapabilities&version=1.1.1");
        assertTrue(GeoServerExtensions.bean(StratusOwsTestSupport.DummyCachingHandler.class).wasHandled);
    }
}
