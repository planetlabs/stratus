/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_1_1;

import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.wfs.WFSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wms.StratusWmsTestSupport;
import stratus.wms.WMSConfig;
import org.geoserver.data.test.SystemTestData;
import stratus.ows.OWSCachingCallback;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(Parameterized.class)
@ContextConfiguration(classes = {GeoServerWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class, WMSConfig.class, WFSConfig.class,
        RedisLayerIndexFacade.class, CacheProperties.class, OWSCachingCallback.class, RedisFacadeTestSupport.class,
        StratusWmsTestSupport.class, SimpleImportResourcesConfig.class, WMSInfoClassRegisteringBean.class})
public class RedisWMSCascadeTest extends WMSCascadeTest {

    @Autowired
    private StratusWmsTestSupport testSupport;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    public RedisWMSCascadeTest(boolean aphEnabled) {
        super(aphEnabled);
    }

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        testSupport.configureWmsTestPreSetup(applicationContext, getCatalog());
        super.onSetUp(testData);
        testSupport.configureWmsTestPostSetup(applicationContext, getCatalog());
    }
}
