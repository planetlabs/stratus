/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs;

import stratus.config.StratusConfigProps;
import stratus.config.GeoServerSystemImportResourcesConfig;
import stratus.config.WebXmlConfig;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.cache.rest.preloaders.FeatureTypePreloader;
import stratus.redis.cache.rest.preloaders.FeatureTypesPreloader;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wfs.DescribeFeatureTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class, RedisLayerIndexFacade.class,
        StratusConfigProps.class, CacheProperties.class, WebXmlConfig.class, RestCachingInterceptor.class, RedisConfigProps.class,
        FeatureTypePreloader.class, FeatureTypesPreloader.class, RedisCatalogImportResourcesConfig.class,
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class,
        WMSConfig.class, WFSConfig.class},
        properties = {"stratus.catalog.redis.caching.enable-rest-caching=true", "spring.main.allow-bean-definition-overriding=true"})
public class RedisDescribeFeatureTest extends DescribeFeatureTest {
    
    @Autowired
    private RedisFacadeTestSupport redisTestSupport;
    
    @Autowired
    CachingFilter cachingFilter;

    @Autowired
    private GeoServerSystemImportResourcesConfig importResourcesConfig;

    @Override
    protected void setUpSpring(List<String> springContextLocations) {
        importResourcesConfig.buildFilteredApplicationContextXmlResourceList(springContextLocations);
    }
    
    @Override
    protected void onTearDown(SystemTestData testData) {
        redisTestSupport.repository.flush();
    }
    
    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        redisTestSupport.repository.flush();
        redisTestSupport.setCatalogFacade(GeoServerSystemTestSupport.applicationContext);
        redisTestSupport.setGeoServerFacade(GeoServerSystemTestSupport.applicationContext);
    }
    
    @Override
    protected List<Filter> getFilters() {
        return Collections.singletonList(cachingFilter);
    }
    
    @Test
    public void testGetWithOperationInPath() throws Exception {
        Document doc = getAsDOM("wfs/DescribeFeatureType?version=1.0.0");
        assertThat(doc.getDocumentElement(), hasProperty("nodeName", equalTo("xsd:schema")));
    }
}
