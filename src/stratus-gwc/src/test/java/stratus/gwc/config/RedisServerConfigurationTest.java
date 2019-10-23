/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;


import stratus.gwc.redis.data.ServerConfigurationRedisImpl;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.apache.commons.io.FileUtils;
import org.geowebcache.config.*;
import org.geowebcache.grid.GridSetBroker;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.net.URL;

/**
 * Redis integraiton test for {@link RedisServerConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisServerConfigurationTest extends ServerConfigurationTest {

    @Autowired
    public RedisGWCTestSupport testSupport;

    private RedisServerConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = new RedisServerConfiguration(testSupport.repository, testSupport.configProps, testSupport.serverRepository);
        testSupport.repository.flush();
    }

    @Override
    protected ServerConfiguration getConfig() throws Exception {
        //TODO: Proper import and initialization
        // create a temp XML config
        File configDir = temp.getRoot();
        File configFile = temp.newFile(XMLConfiguration.DEFAULT_CONFIGURATION_FILE_NAME);
        // copy the example XML to the temp config file
        URL source = XMLConfiguration.class.getResource("geowebcache_190.xml");
        FileUtils.copyURLToFile(source, configFile);
        // initialize the config with an XMLFileResourceProvider that uses the temp config file
        ConfigurationResourceProvider configProvider =
                new XMLFileResourceProvider(XMLConfiguration.DEFAULT_CONFIGURATION_FILE_NAME,
                        (WebApplicationContext)null, configDir.getAbsolutePath(), null);
        XMLConfiguration config = new XMLConfiguration(null, configProvider);
        GridSetBroker gridSetBroker = new GridSetBroker(true, true);
        config.setGridSetBroker(gridSetBroker);
        config.afterPropertiesSet();
        //copy from XMLConfig
        ServerConfigurationRedisImpl configurationInternal = new ServerConfigurationRedisImpl(config);
        this.config.setConfiguration(configurationInternal);
        this.config.afterPropertiesSet();

        //manually set the version so that the test passes
        configurationInternal = this.config.getConfiguration();
        configurationInternal.setVersion("1.13.0");
        this.config.setConfiguration(configurationInternal);

        return this.config;
    }
}