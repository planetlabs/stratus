/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.repository.GridSetRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geowebcache.config.GridSetConfiguration;
import org.geowebcache.config.GridSetConfigurationTest;
import org.geowebcache.grid.GridSet;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.*;

/**
 * Redis integration test for {@link RedisGridSetConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisGridSetConfigurationConformanceTest extends GridSetConfigurationTest {

    @Autowired
    private RedisGWCTestSupport redisTestSupport;

    private boolean failNextRead = false;
    private boolean failNextWrite = false;

    @After
    public void tearDown() {
        // Clear the gwc config
        redisTestSupport.repository.flush();
    }
    
    @Override
    protected void doModifyInfo(GridSet info, int rand) throws Exception {
        info.setDescription(Integer.toString(rand));
    }
    
    @Override
    protected String getExistingInfo() {
        Assume.assumeFalse(true);
        return null;
    }
    
    final static Collection<String> readMethods = Arrays.asList("findAll","findById", "exists", "count");
    final static Collection<String> writeMethods = Arrays.asList("save", "delete", "deleteAll");
    
    /**
     * Wrap a GridSetRepository with a proxy that throws exceptions if failNextRead or failNextWrite
     * are set
     */
    GridSetRepository wrapForExceptions(final GridSetRepository real) {
        GridSetRepository proxy = (GridSetRepository) Proxy.newProxyInstance(GridSetRepository.class.getClassLoader(), 
                new Class<?>[] {GridSetRepository.class, PagingAndSortingRepository.class, 
                                    CrudRepository.class, Repository.class}, 
                (Object iproxy, Method method, Object[] args)->{
            if(readMethods.contains(method.getName())) {
                if (failNextRead) {
                    failNextRead=false;
                    throw new DataAccessException("TEST EXCEPTION ON READ") {
                        /** serialVersionUID */
                        private static final long serialVersionUID = 1L;};
                }
            } else if (writeMethods.contains(method.getName())) {
                if (failNextWrite) {
                    failNextWrite=false;
                    throw new DataAccessException("TEST EXCEPTION ON WRITE") {
                        /** serialVersionUID */
                        private static final long serialVersionUID = 1L;};
                }
            } else {
                Assert.fail(method.getName()+" is not in the list of read or write methods.");
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
    protected GridSetConfiguration getConfig() throws Exception {
        GridSetConfiguration config = new RedisGridSetConfiguration(redisTestSupport.repository, 
                redisTestSupport.configProps, wrapForExceptions(redisTestSupport.gsRepository));
        
        return config;
    }
    
    @Override
    protected GridSetConfiguration getSecondConfig() throws Exception {
        //getConfig creates a fresh config each time
        return getConfig();
    }
    
    @Override
    protected Matcher<GridSet> infoEquals(GridSet expected) {
        return both(Matchers.<GridSet>hasProperty("name", equalTo(expected.getName())))
                .and(hasProperty("description", equalTo(expected.getDescription())));
    }
    
    @Override
    protected Matcher<GridSet> infoEquals(int rand) {
        return hasProperty("description", equalTo(Integer.toString(rand)));
    }
    
    @Override
    public void failNextRead() {
        this.failNextRead=true;
    }

    @Override
    public void failNextWrite() {
        this.failNextWrite=true;
    }
    
}
