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
import org.geowebcache.config.BlobStoreInfo;
import org.geowebcache.config.FileBlobStoreInfo;
import org.geowebcache.s3.S3BlobStoreInfo;
import org.geowebcache.sqlite.MbtilesInfo;
import org.geowebcache.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Additional tests for RedisBlobStoreConfiguration
 * @author smithkm
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisBlobStoreConfigurationTest{
    
    @Autowired
    protected RedisGWCTestSupport redisTestSupport;
    private RedisBlobStoreConfiguration config;
    
    @After
    public void tearDown() {
        // Clear the gwc config
        redisTestSupport.repository.flush();
    }
    
    @Before
    public void setUp() throws Exception {
        config = getConfig();
    }
    
    protected RedisBlobStoreConfiguration getConfig() throws Exception {
        RedisBlobStoreConfiguration config = new RedisBlobStoreConfiguration(redisTestSupport.repository, 
                redisTestSupport.configProps, redisTestSupport.bsRepository);
        
        return config;
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testHeterogeneousInfos() {
        config.addBlobStore(new FileBlobStoreInfo("files"));
        config.addBlobStore(new S3BlobStoreInfo("amazon"));
        config.addBlobStore(new MbtilesInfo("mbtiles"));
        
        List<BlobStoreInfo> blobStores = config.getBlobStores();
        assertThat(blobStores, containsInAnyOrder(
                both(hasProperty("name", equalTo("files")))
                    .and(instanceOf(FileBlobStoreInfo.class)),
                both(hasProperty("name", equalTo("amazon")))
                    .and(instanceOf(S3BlobStoreInfo.class)),
                both(hasProperty("name", equalTo("mbtiles")))
                    .and(instanceOf(MbtilesInfo.class)) ));
    }
    
    @Test
    public void testEmptyDefault() {
        Optional<? extends BlobStoreInfo> info = config.getDefaultBlobStore();
        assertThat(info, TestUtils.notPresent());
    }
    
    @Test
    public void testNoDefault() {
        FileBlobStoreInfo info = new FileBlobStoreInfo("files");
        config.addBlobStore(info);
        assertThat(config.getBlobStores(), containsInAnyOrder(both(hasProperty("name", equalTo("files")))
                .and(instanceOf(FileBlobStoreInfo.class))
                .and(hasProperty("default", is(false)))));
        assertThat(config.getDefaultBlobStore(), TestUtils.notPresent());
        assertThat(config.getBlobStore("files"), TestUtils.isPresent());
    }
    
    @Test
    public void testWithDefault() {
        FileBlobStoreInfo info = new FileBlobStoreInfo("files");
        info.setDefault(true);
        assertThat(info, hasProperty("default", is(true)));
        config.addBlobStore(info);
        assertThat(config.getBlobStore("files"), TestUtils.isPresent(hasProperty("default", is(true))));
        assertThat(config.getDefaultBlobStore(), TestUtils.isPresent(hasProperty("name", is("files"))));
    }
}