/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs;

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
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wfs.StratusWfsTestSupport;
import stratus.wfs.WFSConfig;
import stratus.wfs.WFSHandler;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class, WFSConfig.class,
        RedisLayerIndexFacade.class, CacheProperties.class, OWSCachingCallback.class, StratusWfsTestSupport.class,
        SimpleImportResourcesConfig.class, WFSInfoClassRegisteringBean.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class WFSConfigTest extends WFSTestSupport {

    @Autowired
    private StratusWfsTestSupport testSupport;

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        applicationContext.getBeanFactory().registerSingleton("dummyCachingHandler",
                new StratusOwsTestSupport.DummyCachingHandler());
        testSupport.configureWfsTestPreSetup(applicationContext, getCatalog());
        super.onSetUp(testData);
        testSupport.configureWfsTestPostSetup(applicationContext, getCatalog());
    }

    @Test
    public void testWMSHandlerConfigured() {
        assertEquals(1, GeoServerExtensions.extensions(WFSHandler.class).size());
    }

    @Test
    public void testCallbacksConfigured() throws Exception {
        StratusOwsTestSupport.assertOwsCachingCallbackConfigured();
        //Do a getCapabilities request
        get("wfs?request=getCapabilities&version=2.0.0");
        assertTrue(GeoServerExtensions.bean(StratusOwsTestSupport.DummyCachingHandler.class).wasHandled);
    }
}
