/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.GeoServerFacade;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.config.impl.GeoServerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class, RedisConfigProps.class,
        RedisCatalogImportResourcesConfig.class, RedisLayerIndexFacade.class, CacheProperties.class,
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class GeoServerImplWithRedisFacadeTest extends org.geoserver.config.GeoServerImplTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    private GeoServerFacade facade;

    @Override
    public void setUp() throws Exception {
        redisTestSupport.repository.flush();
        super.setUp();

    }

    @Test
    public void testModifyGlobalService() throws Exception {
        GeoServerInfo global = geoServer.getFactory().createGlobal();
        geoServer.setGlobal(global);

        global = geoServer.getGlobal();
        global.setGlobalServices(false);

        assertTrue(geoServer.getGlobal().isGlobalServices());

        geoServer.save(global);

        assertFalse(geoServer.getGlobal().isGlobalServices());
    }

    @Override
    protected GeoServerImpl createGeoServer() {
        GeoServerImpl gs = new GeoServerImpl();
        gs.setCatalog(new CatalogImpl());
        facade = redisTestSupport.createGeoServerFacade();
        gs.setFacade(facade);

        if (gs.getGlobal() == null) {
            GeoServerInfo geoServerInfo = gs.getFactory().createGlobal();
            gs.setGlobal(geoServerInfo);
        }
        if (gs.getLogging() == null) {
            LoggingInfo loggingInfo = gs.getFactory().createLogging();
            gs.setLogging(loggingInfo);
        }
        return gs;
    }

    @Test
    public void testModifySettings() throws Exception {

        WorkspaceInfo ws = geoServer.getCatalog().getFactory().createWorkspace();
        ws.setName("acme");
        geoServer.getCatalog().add(ws);

        SettingsInfo t = geoServer.getFactory().createSettings();
        t.setNumDecimals(7);
        t.setWorkspace(ws);
        geoServer.add(t);

        SettingsInfo settings = geoServer.getSettings(ws);
        assertNotNull(settings);

        settings.setNumDecimals(6);
        facade.save(settings);

        assertEquals(6, geoServer.getSettings(ws).getNumDecimals());
        assertEquals(4, geoServer.getSettings().getNumDecimals());

    }

    @Test
    public void testModifyLogging() {
        LoggingInfo logging = facade.getLogging();
        logging.setLevel("VERBOSE_LOGGING.properties");
        facade.save(logging);

        assertEquals(logging.getLevel(), facade.getLogging().getLevel());
    }
}
