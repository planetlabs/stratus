/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;


import stratus.config.StratusConfigProps;
import stratus.config.WebXmlConfig;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.cache.rest.preloaders.LayerPreloader;
import stratus.redis.cache.rest.preloaders.LayersPreloader;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.wcs.WCSConfig;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.WFSConfig;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.ContactInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.SettingsInfo;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.ows.util.OwsUtils;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
        WebXmlConfig.class, RestCachingInterceptor.class, LayerPreloader.class, LayersPreloader.class},
        properties = {"stratus.catalog.redis.caching.enable-rest-caching=true", "spring.main.allow-bean-definition-overriding=true"})
public class RedisLayerTest extends LayerControllerTest {

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
    protected List<Filter> getFilters() {
        return Collections.singletonList(cachingFilter);
    }

    @Test
    public void testGetLayerVirtualWorkspaceWithSettings() {

        GeoServer geoServer = getGeoServer();
        Catalog catalog = getCatalog();
        WorkspaceInfo ws = catalog.getWorkspaceByName(MockData.CITE_PREFIX);
        LayerInfo layer = catalog.getLayerByName(MockData.BASIC_POLYGONS.getLocalPart());

        SettingsInfo t = geoServer.getFactory().createSettings();

        SettingsInfo global = geoServer.getGlobal().getSettings();

        ContactInfo contact = geoServer.getFactory().createContact();
        OwsUtils.copy(global.getContact(), contact, ContactInfo.class);

        OwsUtils.copy(global, t, SettingsInfo.class);
        t.setContact(contact);
        t.setWorkspace(ws);

        geoServer.add(t);

        assertNotNull(geoServer.getCatalog().getWorkspace(ws.getId()));
        SettingsInfo settings = geoServer.getSettings(ws);
        assertNotNull(settings);

        //get in local workspace shouldn't give stackoverflow
        LocalWorkspace.set(ws);
        try {
            assertNotNull(geoServer.getCatalog().getWorkspaceByName(ws.getName()));
            assertNotNull(geoServer.getCatalog().getNamespaceByPrefix(ws.getName()));
            assertNotNull(geoServer.getCatalog().getLayerByName(layer.getName()));
        } finally {
            LocalWorkspace.remove();
        }
    }
}
