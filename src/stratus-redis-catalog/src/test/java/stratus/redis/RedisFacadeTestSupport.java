/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis;

import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.repository.*;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.CachingGeoServerFacade;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.geoserver.ServiceInfoRegisteringBean;
import stratus.redis.geoserver.repository.*;
import stratus.redis.repository.RedisRepository;
import stratus.redis.repository.RedisRepositoryImpl;
import org.geoserver.catalog.*;
import org.geoserver.catalog.event.CatalogListener;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.RedisCatalogFacadeFactory;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerFacade;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.test.GeoServerTestApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Support class for redis testing.
 * Primarily used by test classes extending some descendant of {@link org.geoserver.test.GeoServerSystemTestSupport}
 *
 * Provides access to {@link RedisRepository} and associated repository interfaces.
 *
 * Provides utility methods for creating {@link RedisCatalogFacade} and {@link RedisGeoServerFacade}, and for copying
 * the standard {@link org.geoserver.test.GeoServerSystemTestSupport} test data into the redis facades
 *
 * Created by tbarsballe on 2016-11-07.
 */
public class RedisFacadeTestSupport {

    @Autowired
    public RedisRepositoryImpl repository;
    @Autowired
    public NamespaceRepository namespaceRepository;
    @Autowired
    public WorkspaceRepository workspaceRepository;
    @Autowired
    public StoreRepository storeRepository;
    @Autowired
    public DataStoreRepository dataStoreRepository;
    @Autowired
    public CoverageStoreRepository coverageStoreRepository;
    @Autowired
    public WMSStoreRepository wmsStoreRepository;
    @Autowired
    public CoverageResourceRepository coverageResourceRepository;
    @Autowired
    public FeatureTypeResourceRepository featureTypeResourceRepository;
    @Autowired
    public WMSLayerResourceRepository wmsLayerResourceRepository;
    @Autowired
    public LayerRepository layerRepository;
    @Autowired
    public StyleRepository styleRepository;
    @Autowired
    public LayerGroupRepository layerGroupRepository;
    @Autowired
    public MapRepository mapRepository;
    @Autowired
    public WMTSStoreRepository wmtsStoreRepository;
    @Autowired
    public WMTSLayerResourceRepository wmtsLayerRepository;
    @Autowired
    public StoreLookupRepository storeLookupRepository;
    @Autowired
    public GeoServerInfoRepository geoServerInfoRepository;
    @Autowired
    public LoggingInfoRepository loggingInfoRepository;
    @Autowired
    public ServiceInfoWrapperRepository serviceInfoWrapperRepository;
    @Autowired(required=false)
    public List<AbstractServiceInfoRepository> serviceInfoRepositories = new ArrayList<>();
    @Autowired
    public SettingsInfoRepository settingsInfoRepository;
    @Autowired
    public GeoServer geoServer;
    @Autowired(required=false)
    public RedisConfigProps configProps = new RedisConfigProps();
    @Autowired(required=false)
    List<ServiceInfoRegisteringBean> serviceInfoRegisteringBeans = new ArrayList<>();

    private CatalogFacade oldCatalogFacade = null;
    private GeoServerFacade oldGeoServerFacade = null;
    private boolean catalogFacadeInitialized = false;
    private boolean geoServerFacadeInitialized = false;

    @PostConstruct
    private void ensureRepositoryClean() {
        repository.flush();
    }

    /**
     * Creates a new {@link RedisCatalogFacade}, given the geoserver {@link CatalogImpl}
     * @param catalog The catalog
     * @return The created facade
     */
    public CatalogFacade createCatalogFacade(CatalogImpl catalog) {
        RedisCatalogFacade facade = new RedisCatalogFacade(repository, catalog, workspaceRepository, storeRepository, dataStoreRepository,
                coverageStoreRepository, wmsStoreRepository, coverageResourceRepository, featureTypeResourceRepository,
                wmsLayerResourceRepository, namespaceRepository, layerRepository, styleRepository, layerGroupRepository,
                mapRepository, wmtsLayerRepository, wmtsStoreRepository);
        return RedisCatalogFacadeFactory.create(catalog, facade);
    }

    /**
     * Creates a new {@link RedisGeoServerFacade}
     * @return
     */
    public GeoServerFacade createGeoServerFacade() {
        RedisGeoServerFacade facade = new RedisGeoServerFacade(geoServerInfoRepository, loggingInfoRepository,
                serviceInfoWrapperRepository, serviceInfoRepositories, settingsInfoRepository, geoServer, serviceInfoRegisteringBeans, configProps);

        return new CachingGeoServerFacade(facade.getGeoServer(), facade);
    }

    /**
     * Given the applicationContext, creates a new {@link RedisCatalogFacade} and inserts it into the context, copying
     * all data from the preexisting {@link CatalogFacade} into this new facade.
     *
     * @param applicationContext The application context
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void setCatalogFacade(GeoServerTestApplicationContext applicationContext) throws NoSuchFieldException, IllegalAccessException {
        if (catalogFacadeInitialized) {
            return;
        }
        CatalogImpl catalogImpl = applicationContext.getBean(CatalogImpl.class);

        //Hack to remove CatalogPersister and other listeners
        ArrayList listeners = new ArrayList();
        for (CatalogListener l : (Iterable<CatalogListener>) catalogImpl.getListeners()) {
            //whitelist of listeners to retain
            if (l instanceof ResourcePool.CacheClearingListener || l instanceof NamespaceWorkspaceConsistencyListener || l.getClass().getSimpleName().equals("CatalogLayerEventListener")) {
                listeners.add(l);
            }
        }
        Field field = CatalogImpl.class.getDeclaredField("listeners");
        field.setAccessible(true);
        field.set(catalogImpl, listeners);

        if (oldCatalogFacade == null) {
            oldCatalogFacade = catalogImpl.getFacade();
        }
        CatalogFacade oldFacade = oldCatalogFacade;
        CatalogFacade facade = createCatalogFacade(catalogImpl);

        //Port default catalog contents
        oldFacade.getNamespaces().forEach(facade::add);
        oldFacade.getWorkspaces().forEach(facade::add);
        oldFacade.getStores(DataStoreInfo.class).forEach(facade::add);
        oldFacade.getStores(CoverageStoreInfo.class).forEach(facade::add);
        oldFacade.getStores(WMSStoreInfo.class).forEach(facade::add);
        oldFacade.getStores(WMTSStoreInfo.class).forEach(facade::add);
        oldFacade.getResources(FeatureTypeInfo.class).forEach(facade::add);
        oldFacade.getResources(CoverageInfo.class).forEach(facade::add);
        oldFacade.getResources(WMSLayerInfo.class).forEach(facade::add);
        oldFacade.getResources(WMTSLayerInfo.class).forEach(facade::add);
        oldFacade.getLayers().forEach(facade::add);
        oldFacade.getLayerGroups().forEach(facade::add);
        oldFacade.getStyles().forEach(facade::add);
        oldFacade.getMaps().forEach(facade::add);

        if (oldFacade.getDefaultWorkspace() != null) {
            facade.setDefaultWorkspace(facade.getWorkspaceByName(oldFacade.getDefaultWorkspace().getName()));
        }
        if (oldFacade.getDefaultNamespace() != null) {
            facade.setDefaultNamespace(facade.getNamespaceByPrefix(oldFacade.getDefaultNamespace().getPrefix()));
        }
        for (WorkspaceInfo ws : facade.getWorkspaces()) {
            DataStoreInfo oldStore = oldFacade.getDefaultDataStore(oldFacade.getWorkspaceByName(ws.getName()));
            if (oldStore != null) {
                facade.setDefaultDataStore(ws, facade.getStoreByName(ws, oldStore.getName(), DataStoreInfo.class));
            }
        }

        catalogImpl.setFacade(facade);
        catalogFacadeInitialized = true;
    }

    /**
     * Given the applicationContext, creates a new {@link RedisGeoServerFacade} and inserts it into the context, copying
     * all data from the preexisting {@link GeoServerFacade} into this new facade.
     *
     * @param applicationContext The application context
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void setGeoServerFacade(GeoServerTestApplicationContext applicationContext) throws NoSuchFieldException, IllegalAccessException {
        if (geoServerFacadeInitialized) {
            return;
        }
        GeoServerImpl geoServerImpl = applicationContext.getBean(GeoServerImpl.class);

        //Hack to remove GeoServerPersister and other listeners
        ArrayList listeners = new ArrayList();
        for (ConfigurationListener l : geoServerImpl.getListeners()) {
            //whitelist of listeners to retain
            if (false) {
                listeners.add(l);
            }
        }
        Field field = GeoServerImpl.class.getDeclaredField("listeners");
        field.setAccessible(true);
        field.set(geoServerImpl, listeners);

        if (oldGeoServerFacade == null) {
            oldGeoServerFacade = geoServerImpl.getFacade();
        }
        GeoServerFacade oldFacade = oldGeoServerFacade;
        GeoServerFacade facade = createGeoServerFacade();

        facade.setGlobal(oldFacade.getGlobal());
        oldFacade.getServices().forEach(facade::add);

        for (WorkspaceInfo ws : geoServerImpl.getCatalog().getWorkspaces()) {
            oldFacade.getServices(ws).forEach(facade::add);
        }
        geoServerImpl.setFacade(facade);
        geoServerFacadeInitialized = true;
    }

}
