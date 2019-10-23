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
import org.geowebcache.s3.S3BlobStoreInfo;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;

/**
 * Redis integration test for {@link RedisS3BlobStoreConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisS3BlobStoreConfigurationConformanceTest extends RedisBlobStoreConfigurationConformanceTest {
    @Override
    protected void doModifyInfo(BlobStoreInfo info, int rand) throws Exception {
        ((S3BlobStoreInfo)info).setProxyPort(rand);
    }
    
    @Override
    protected String getExistingInfo() {
        Assume.assumeFalse(true);
        return null;
    }
    
    @Override
    protected BlobStoreInfo getGoodInfo(String id, int rand) throws Exception {
        S3BlobStoreInfo info = new S3BlobStoreInfo(id);
        info.setEnabled(false);
        info.setDefault(false);
        doModifyInfo(info,rand);
        return info;
    }
    
    @Override
    protected BlobStoreInfo getBadInfo(String id, int rand) throws Exception {
        BlobStoreInfo info = new S3BlobStoreInfo(id) {
            
            /** serialVersionUID */
            private static final long serialVersionUID = -2281952301530324407L;

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
                .and(hasProperty("proxyPort", equalTo(((S3BlobStoreInfo)expected).getProxyPort())));
    }
    
    @Override
    protected Matcher<BlobStoreInfo> infoEquals(int rand) {
        return hasProperty("proxyPort", equalTo(rand));
    }
    
}
