/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import stratus.commons.event.GeoServerInitializedEvent;
import stratus.commons.lock.InitializationProvider;
import stratus.commons.lock.LockingInitializer;
import stratus.commons.lock.LockingInitializerConfig;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.RedisCatalogUtils;
import stratus.redis.catalog.config.StratusCatalogConfigProps;
import stratus.redis.lock.RedisStratusLockProvider;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.redis.store.RedisResourceInitializer;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.RedisCatalogFacadeFactory;
import org.geoserver.config.*;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.config.util.XStreamPersisterFactory;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.*;
import org.geoserver.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author Josh Fix
 * @author tingold
 */
@Slf4j
@Primary
@Service
public class RedisGeoServerLoader extends DefaultGeoServerLoader implements ApplicationContextAware {

    private final GeoServerFacade geoServerFacade;
    private final RedisCatalogFacade facade;
    private final RedisRepositoryImpl repository;
    private final GeoServer geoserver;
    private final CatalogImpl catalog;
    private final StratusCatalogConfigProps configProps;
    private final RedisResourceInitializer resourceInitializer;
    private XStreamPersister xStreamPersister;

    private ApplicationContext applicationContext;

    private LockingInitializer initializer;

    public static final String GEOSERVER_INITIALIZATION_KEY = "GeoServer:Initialized";

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public RedisGeoServerLoader(GeoServerResourceLoader resourceLoader,
                                @Autowired(required=false) RedisGeoServerFacade geoServerFacade,
                                RedisCatalogFacade facade, RedisRepositoryImpl repository, GeoServer geoserver,
                                CatalogImpl catalog, StratusCatalogConfigProps configProps,
                                RedisResourceInitializer resourceInitializer) {
        super(resourceLoader);
        this.geoServerFacade = geoServerFacade;
        this.facade = facade;
        this.repository = repository;
        this.geoserver = geoserver;
        this.catalog = catalog;
        this.configProps = configProps;
        this.resourceInitializer = resourceInitializer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        ((GeoServerImpl) geoserver).setFacade(new CachingGeoServerFacade(geoserver, geoServerFacade));
        catalog.setFacade(RedisCatalogFacadeFactory.create(catalog, facade));

        final XStreamPersisterFactory xStreamPersisterFactory = new XStreamPersisterFactory();
        xStreamPersister = xStreamPersisterFactory.createXMLPersister();
        xStreamPersister.setCatalog(catalog);

        System.setProperty("GeoServerConfigurationLock.enabled", "false");
        initializeGeoServerSubSystem();

        //add the listener which will persist changes
        catalog.addListener( new GeoServerConfigPersister( resourceLoader, xStreamPersister ) );
    }

    public void initializeGeoServerSubSystem() {
        LockingInitializerConfig initializerConfig = new LockingInitializerConfig(
                configProps.getMinWaitForInitializerCheck(),
                configProps.getMaxWaitForInitializerCheck(), configProps.getInitializerTimeout(),
                "GeoServerSubSystem", GEOSERVER_INITIALIZATION_KEY);

        InitializationProvider initializationProvider = () -> {
            initializeRedisResourceStore();
            initializeGeoServer(xStreamPersister);
            initializeExtensions();
            log.info("GeoServer subsystem successfully initialized.");
        };

        initializer = new LockingInitializer(new RedisStratusLockProvider(initializerConfig, repository), initializerConfig);
        initializer.execute(initializationProvider);
    }

    public void setGeoServerInitialized() {
        if (initializer.isLockAcquired()) {
            initializer.setInitialized(true);
            initializer.clearLocks();
            log.info("GeoServer subsystem successfully initialized.");
        }
    }

    private void initializeRedisResourceStore() {
        resourceInitializer.init();
        if (RedisCatalogUtils.isImportStore()) {
            try {
                ResourceStore ddResourceStore = new FileSystemResourceStore(RedisCatalogUtils.lookupGeoServerDataDirectory());
                Resource root = ddResourceStore.get("");
                for (Resource child : root.list()) {
                    Resources.copy(child, resourceInitializer.getRedisResourceStore().get(child.name()));
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    protected void initializeGeoServer(XStreamPersister xStreamPersister) {
        log.info("Initializing GeoServer subsystem.");
        try {
            log.debug("Loading Catalog");
            loadCatalog(catalog, xStreamPersister);
            log.debug("Initializing styles");
            initializeStyles(catalog, xStreamPersister);
            log.debug("Loading geoserver");
            loadGeoServer(geoserver, xStreamPersister);
            log.debug("Loading initializers");
            loadInitializers(geoserver);
        } catch (Exception e) {
            log.error("Failed to initialize GeoServer subsystem.", e);
        }
    }

    @Override
    protected void loadGeoServer(GeoServer geoServer, XStreamPersister xStreamPersister) throws Exception {

        if (!repository.isGeoServerInitialized()) {
            readConfiguration(geoServer, xStreamPersister);
        }

        if (geoServer.getGlobal() == null) {
            GeoServerInfo geoServerInfo = geoServer.getFactory().createGlobal();
            geoServerInfo.getSettings().setProxyBaseUrl(configProps.getProxyBaseUrl());
            geoServerInfo.setWebUIMode(GeoServerInfo.WebUIMode.DO_NOT_REDIRECT);
            geoServer.setGlobal(geoServerInfo);
        }
        if (geoServer.getLogging() == null) {
            LoggingInfo loggingInfo = geoServer.getFactory().createLogging();
            geoServer.setLogging(loggingInfo);
        }

        //also ensure we have a service configuration for every service we know about
        final List<XStreamServiceLoader> loaders = GeoServerExtensions.extensions(XStreamServiceLoader.class);
        for (XStreamServiceLoader loader : loaders) {
            ServiceInfo serviceInfo = geoServer.getService(loader.getServiceClass());
            if (serviceInfo == null) {
                geoServer.add(loader.create(geoServer));
            }
        }
    }

    @Override
    protected void loadCatalog(Catalog catalog, XStreamPersister xStreamPersister) throws Exception {
    	if (RedisCatalogUtils.isImportCatalog()) {
        	try {
        		new DataDirectoryGeoServerLoader().readCatalog(catalog, xStreamPersister);
        	} catch (Exception e) {
        		log.error("Failed to import catalog from data directory", e);
        	}
        }
    }

    @Override
    protected void loadInitializers(GeoServer geoServer) throws Exception {
        // load initializer extensions
        List<GeoServerInitializer> initializers =
                GeoServerExtensions.extensions(GeoServerInitializer.class);
        for (GeoServerInitializer initer : initializers) {
            try {
                if (initer instanceof ApplicationContextAware) {
                    ((ApplicationContextAware) initer).setApplicationContext(applicationContext);
                }
                initer.initialize(geoServer);
            } catch (Throwable t) {
                log.error("Failed to run initializer " + initer, t);
            }
        }
    }

    @Override
    protected void initializeStyles(Catalog catalog, XStreamPersister xStreamPersister) throws IOException {
        if (catalog.getStyleByName(StyleInfo.DEFAULT_POINT) == null) {
            initializeStyle(catalog, StyleInfo.DEFAULT_POINT, "default_point.sld");
        }
        if (catalog.getStyleByName(StyleInfo.DEFAULT_LINE) == null) {
            initializeStyle(catalog, StyleInfo.DEFAULT_LINE, "default_line.sld");
        }
        if (catalog.getStyleByName(StyleInfo.DEFAULT_POLYGON) == null) {
            initializeStyle(catalog, StyleInfo.DEFAULT_POLYGON, "default_polygon.sld");
        }
        if (catalog.getStyleByName(StyleInfo.DEFAULT_RASTER) == null) {
            initializeStyle(catalog, StyleInfo.DEFAULT_RASTER, "default_raster.sld");
        }
        if (catalog.getStyleByName(StyleInfo.DEFAULT_GENERIC) == null) {
            initializeStyle(catalog, StyleInfo.DEFAULT_GENERIC, "default_generic.sld");
        }
    }

    /**
     * Copies a well known style out to the data directory and adds a catalog
     * entry for it.
     */
    void initializeStyle(Catalog catalog, String styleName, String sld) throws IOException {

        // copy the file out to the data directory if necessary
        Resource styleResource = resourceLoader.get(Paths.path("styles", sld));
        if (!Resources.exists(styleResource)) {
            IOUtils.copy(GeoServerLoader.class.getResourceAsStream(sld), styleResource.out());
        }

        // create a style for it
        StyleInfo s = catalog.getFactory().createStyle();
        s.setName(styleName);
        s.setFilename(sld);
        catalog.add(s);
    }

    public void initializeExtensions() {
        for (RedisMasterCatalogInitializer extensionInitializer : GeoServerExtensions.extensions(RedisMasterCatalogInitializer.class)) {
            extensionInitializer.init();
        }
    }

    @Override
    public void reload() throws Exception {
        geoserver.dispose();
        init();
    }

    @EventListener
    public void onApplicationEvent(GeoServerInitializedEvent event) {
        log.debug("GeoServerInitializedEvent received in RedisGeoServerLoader.");
        this.setGeoServerInitialized();
    }

}
