/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.service.rest;

import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.wcs.WCSConfig;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.rest.service.LocalWCSSettingsControllerTest;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class, WCSConfig.class,
        RedisRepositoryImpl.class, RedisLayerIndexFacade.class, CacheProperties.class, RedisFacadeTestSupport.class,
        WCSInfoClassRegisteringBean.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisLocalWCSSettingsTest extends LocalWCSSettingsControllerTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    @Override
    protected void onTearDown(SystemTestData testData) {
        redisTestSupport.repository.flush();
    }

    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);

        redisTestSupport.setCatalogFacade(GeoServerSystemTestSupport.applicationContext);
        redisTestSupport.setGeoServerFacade(GeoServerSystemTestSupport.applicationContext);
    }
}
