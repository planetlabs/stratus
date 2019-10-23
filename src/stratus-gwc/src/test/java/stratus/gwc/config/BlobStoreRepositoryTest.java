/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.FileBlobStoreInfoRedisImpl;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geowebcache.util.TestUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class BlobStoreRepositoryTest{
    
    @Autowired
    protected RedisGWCTestSupport redisTestSupport;
    
    @After
    public void tearDown() {
        // Clear the gwc config
        redisTestSupport.repository.flush();
    }
    
    @Test
    public void testFileBlobStore() {
        FileBlobStoreInfoRedisImpl orig = new FileBlobStoreInfoRedisImpl();
        orig.setName("files");
        orig.setBaseDirectory("/tmp");
        orig.setDefaultFlag(true);
        orig.setEnabled(true);
        orig.setFileSystemBlockSize(42);
        
        redisTestSupport.bsRepository.save(orig);
        
        assertThat(redisTestSupport.bsRepository.findAll(), contains(equalTo(orig)));
        assertThat(redisTestSupport.bsRepository.findById("files").orElse(null), equalTo(orig));
        assertThat(redisTestSupport.bsRepository.findByDefaultFlag(true), TestUtils.isPresent(equalTo(orig)));
    }
}
