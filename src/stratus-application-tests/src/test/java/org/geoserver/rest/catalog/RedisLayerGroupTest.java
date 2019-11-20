/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;

import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.config.StratusConfigProps;
import stratus.config.WebXmlConfig;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.cache.rest.preloaders.LayerGroupPreloader;
import stratus.redis.cache.rest.preloaders.LayerGroupsPreloader;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wcs.WCSConfig;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.WFSConfig;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        /* Catalog */
        RedisCatalogFacade.class, RedisGeoServerFacade.class, RedisLayerIndexFacade.class,
        CacheProperties.class, CatalogImpl.class, GWCWithEmbeddedRedisConfig.class, StratusConfigProps.class,
        RedisCatalogImportResourcesConfig.class, RedisRepositoryImpl.class, RedisConfigProps.class,
        /* OWS */
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        WMSConfig.class, WFSConfig.class, WCSConfig.class,
        /* GWC */
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class,
        /* Cache preloaders */
        WebXmlConfig.class, RestCachingInterceptor.class, LayerGroupPreloader.class, LayerGroupsPreloader.class,},
        properties = {"stratus.catalog.redis.caching.enable-rest-caching=true", "spring.main.allow-bean-definition-overriding=true"})
public class RedisLayerGroupTest extends LayerGroupControllerTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    @Autowired
    CachingFilter cachingFilter;

    @Autowired
    private RedisCatalogImportResourcesConfig importResourcesConfig;

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
    public void revertChanges() throws Exception {
        removeLayerGroup(null, "nestedLayerGroupTest");
        super.revertChanges();
    }

    @Override
    protected List<Filter> getFilters() {
        return Collections.singletonList(cachingFilter);
    }

    //Extra test to verify CatalogInfoConvert behavior
    @Test
    public void testLayerGroupOrder() throws Exception {
        String xml =
                "<layerGroup>" +
                        "<name>nestedLayerGroupTest</name>" +
                        "<publishables>" +
                        "<published type=\"layer\">Ponds</published>" +
                        "<published type=\"layerGroup\">sfLayerGroup</published>" +
                        "<published type=\"layer\">Forests</published>" +
                        "</publishables>" +
                        "<styles>" +
                        "<style>polygon</style>" +
                        "<style>point</style>" +
                        "<style></style>" +
                        "</styles>" +
                        "</layerGroup>";

        MockHttpServletResponse response = postAsServletResponse(RestBaseController.ROOT_PATH + "/layergroups", xml );
        assertEquals( 201, response.getStatus() );

        assertNotNull( response.getHeader( "Location") );
        assertTrue( response.getHeader("Location").endsWith( "/layergroups/nestedLayerGroupTest" ) );

        LayerGroupInfo lg = CatalogRESTTestSupport.catalog.getLayerGroupByName( "nestedLayerGroupTest");
        assertNotNull( lg );

        assertEquals( 3, lg.getLayers().size() );
        assertEquals( "Ponds", lg.getLayers().get( 0 ).getName() );
        assertEquals( "sfLayerGroup", lg.getLayers().get( 1 ).getName() );
        assertEquals( "Forests", lg.getLayers().get( 2 ).getName() );
    }
}
