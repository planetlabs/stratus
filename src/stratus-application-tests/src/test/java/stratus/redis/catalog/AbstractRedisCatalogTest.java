/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.*;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.impl.AbstractCatalogDecorator;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServerConfigPersister;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.impl.GeoServerInfoImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a basic Stratus catalog configuration for testing purposes. Includes an embedded Redis instance and GeoServer
 * catalog and configuration using {@link RedisCatalogFacade} and {@link RedisGeoServerFacade} respectively.
 *
 * Somewhat simpler than {@link stratus.StratusApplicationTestSupport}; Does not include S3 WPS
 *
 * Does not include any test data.
 *
 * @author tbarsballe
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        /* Catalog */
        RedisCatalogFacade.class, RedisGeoServerFacade.class, RedisLayerIndexFacade.class,
        CacheProperties.class, CatalogImpl.class, GWCWithEmbeddedRedisConfig.class,
        RedisCatalogImportResourcesConfig.class, RedisRepositoryImpl.class, RedisConfigProps.class,
        /* OWS */
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        /* GWC */
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public abstract class AbstractRedisCatalogTest {
    @Autowired
    protected RedisGeoServerFacade geoServerFacade;
    @Autowired
    protected RedisCatalogFacade catalogFacade;
    @Autowired
    protected RedisTemplate template;

    protected CatalogImpl catalog;
    protected GeoServerImpl geoServer;

    protected WorkspaceInfo ws;
    protected WorkspaceInfo wsA;
    protected WorkspaceInfo wsB;

    protected NamespaceInfo ns;
    protected NamespaceInfo nsA;
    protected NamespaceInfo nsB;

    protected DataStoreInfo ds;
    protected DataStoreInfo dsA;
    protected DataStoreInfo dsB;

    protected CoverageStoreInfo cs;
    protected WMSStoreInfo wms;
    protected FeatureTypeInfo ft;
    protected CoverageInfo cv;
    protected WMSLayerInfo wl;
    protected LayerInfo l;
    protected StyleInfo s;
    protected LayerGroupInfo lg;

    @PostConstruct
    protected void init() {
        if (catalogFacade.getCatalog() instanceof AbstractCatalogDecorator) {
            catalog = ((AbstractCatalogDecorator) catalogFacade.getCatalog()).unwrap(CatalogImpl.class);
            catalog.setFacade(catalogFacade);
        } else {
            catalog = ((CatalogImpl) catalogFacade.getCatalog());
            catalog.setFacade(catalogFacade);
        }
        geoServer = ((GeoServerImpl) geoServerFacade.getGeoServer());
        geoServer.setFacade(geoServerFacade);
        //Cleanup unwanted listeners. May be able to use RedisGeoServerLoader to load config properly; need to override DefaultGeoServerLoader
        List<CatalogListener> catalogListeners = new ArrayList<>();
        catalogListeners.addAll(catalog.getListeners());
        for (CatalogListener listener : catalogListeners) {
            if (listener instanceof GeoServerConfigPersister) {
                catalog.removeListener(listener);
            }
        }
        List<ConfigurationListener> geoServerListeners = new ArrayList<>();
        geoServerListeners.addAll(geoServer.getListeners());
        for (ConfigurationListener listener : geoServerListeners) {
            if (listener instanceof GeoServerConfigPersister) {
                geoServer.removeListener(listener);
            }
        }
    }

    @Before
    public void setUp() {
        template.getConnectionFactory().getConnection().flushAll();

        geoServerFacade.setGlobal(new GeoServerInfoImpl());

        CatalogFactory factory = catalog.getFactory();

        ns = factory.createNamespace();
        //ns prefix has to match workspace name, until we break that relationship
        //ns.setPrefix( "nsPrefix" );
        ns.setPrefix( "wsName" );
        ns.setURI( "nsURI" );

        nsA = factory.createNamespace();
        //ns prefix has to match workspace name, until we break that relationship
        //nsA.setPrefix( "nsPrefix" );
        nsA.setPrefix( "aaa" );
        nsA.setURI( "nsURIaaa" );

        nsB = factory.createNamespace();
        //ns prefix has to match workspace name, until we break that relationship
        //nsB.setPrefix( "nsPrefix" );
        nsB.setPrefix( "bbb" );
        nsB.setURI( "nsURIbbb" );

        ws = factory.createWorkspace();
        ws.setName( "wsName");

        wsA = factory.createWorkspace();
        wsA.setName( "aaa");

        wsB = factory.createWorkspace();
        wsB.setName( "bbb");

        ds = factory.createDataStore();
        ds.setEnabled(true);
        ds.setName( "dsName");
        ds.setDescription("dsDescription");
        ds.setWorkspace( ws );

        dsA = factory.createDataStore();
        dsA.setEnabled(true);
        dsA.setName( "dsNameA");
        dsA.setDescription("dsDescription");
        dsA.setWorkspace( wsA );

        dsB = factory.createDataStore();
        dsB.setEnabled(true);
        dsB.setName( "dsNameB");
        dsB.setDescription("dsDescription");
        dsB.setWorkspace( wsB );

        ft = factory.createFeatureType();
        ft.setEnabled(true);
        ft.setName( "ftName" );
        ft.setAbstract( "ftAbstract" );
        ft.setDescription( "ftDescription" );
        ft.setStore( ds );
        ft.setNamespace( ns );

        cs = factory.createCoverageStore();
        cs.setName("csName");
        cs.setType("fakeCoverageType");
        cs.setURL("file://fake");

        cv = factory.createCoverage();
        cv.setName("cvName");
        cv.setStore(cs);

        wms = factory.createWebMapServer();
        wms.setName("wmsName");
        wms.setType("WMS");
        wms.setCapabilitiesURL("http://fake.url");
        wms.setWorkspace(ws);

        wl = factory.createWMSLayer();
        wl.setEnabled(true);
        wl.setName("wmsLayer");
        wl.setStore(wms);
        wl.setNamespace(ns);

        s = factory.createStyle();
        s.setName( "styleName" );
        s.setFilename( "styleFilename" );

        l = factory.createLayer();
        l.setResource( ft );
        l.setEnabled(true);
        l.setDefaultStyle( s );

        lg = factory.createLayerGroup();
        lg.setName("layerGroup");
        lg.getLayers().add(l);
        lg.getStyles().add(s);
    }


    protected void addWorkspace() {
        catalog.add(ws);
    }

    protected void addNamespace() {
        catalog.add(ns);
    }

    protected void addDataStore() {
        addWorkspace();
        catalog.add(ds);
    }

    protected void addCoverageStore() {
        addWorkspace();
        catalog.add(cs);
    }

    protected void addWMSStore() {
        addWorkspace();
        catalog.add(wms);
    }

    protected void addFeatureType() {
        addDataStore();
        addNamespace();
        catalog.add(ft);
    }

    protected void addCoverage() {
        addCoverageStore();
        addNamespace();
        catalog.add(cv);
    }

    protected void addWMSLayer() {
        addWMSStore();
        addNamespace();
        catalog.add(wl);
    }

    protected void addStyle() {
        catalog.add(s);
    }

    protected void addLayer() {
        addFeatureType();
        addStyle();
        catalog.add(l);
    }

    protected void addLayerGroup() {
        addLayer();
        catalog.add(lg);
    }
}
