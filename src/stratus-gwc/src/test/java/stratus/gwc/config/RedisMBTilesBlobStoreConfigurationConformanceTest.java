/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.redis.config.RedisConfigProps;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geowebcache.config.BlobStoreConfiguration;
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.sqlite.MbtilesInfo;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;

/**
 * Redis integration test for {@link RedisMBTilesBlobStoreConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisMBTilesBlobStoreConfigurationConformanceTest extends RedisBlobStoreConfigurationConformanceTest {

    @Override
    protected void doModifyInfo(BlobStoreInfo info, int rand) throws Exception {
        ((MbtilesInfo)info).setExecutorConcurrency(rand);
    }
    
    @Override
    protected String getExistingInfo() {
        Assume.assumeFalse(true);
        return null;
    }
    
    @Override
    protected BlobStoreInfo getGoodInfo(String id, int rand) throws Exception {
        MbtilesInfo info = new MbtilesInfo(id);
        info.setEnabled(false);
        info.setDefault(false);
        doModifyInfo(info,rand);
        return info;
    }
    
    @Override
    protected BlobStoreInfo getBadInfo(String id, int rand) throws Exception {
        BlobStoreInfo info = new MbtilesInfo(id) {
            
            /** serialVersionUID */
            private static final long serialVersionUID = -5745963516177615504L;
            
            @Override
            public String getName() {
                return null;
            }
        };
        return info;
    }
    
    @Override
    protected BlobStoreConfiguration getConfig() throws Exception {
        BlobStoreConfiguration config = new RedisBlobStoreConfiguration(redisTestSupport.repository, 
                redisTestSupport.configProps, wrapForExceptions(redisTestSupport.bsRepository));
        
        return config;
    }
    
    @Override
    protected BlobStoreConfiguration getSecondConfig() throws Exception {
        //getConfig creates a fresh config each time
        return getConfig();
    }
    
    @Override
    protected Matcher<BlobStoreInfo> infoEquals(BlobStoreInfo expected) {
        return both(Matchers.<BlobStoreInfo>hasProperty("name", equalTo(expected.getName())))
                .and(hasProperty("executorConcurrency", equalTo(((MbtilesInfo)expected).getExecutorConcurrency())));
    }
    
    @Override
    protected Matcher<BlobStoreInfo> infoEquals(int rand) {
        return hasProperty("executorConcurrency", equalTo(rand));
    }
    
}
