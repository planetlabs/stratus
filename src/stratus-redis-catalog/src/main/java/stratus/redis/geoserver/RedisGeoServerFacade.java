/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Info;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geoserver.config.*;
import org.geoserver.config.impl.GeoServerInfoImpl;
import org.geoserver.config.impl.LoggingInfoImpl;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.config.impl.SettingsInfoImpl;
import org.geoserver.ows.util.OwsUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import stratus.redis.catalog.RedisCatalogUtils;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.info.*;
import stratus.redis.geoserver.repository.*;
import stratus.redis.upgrade.StratusUpgradingDeserializingConverter;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.geoserver.catalog.CatalogFacade.ANY_WORKSPACE;

/**
 * @author joshfix
 * Created on 6/5/18
 */
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiringInspection")
public class RedisGeoServerFacade implements GeoServerFacade {

    private GeoServer geoServer;
    private final GeoServerInfoRepository geoServerInfoRepository;
    private final LoggingInfoRepository loggingInfoRepository;
    private final List<AbstractServiceInfoRepository> serviceInfoRepositories;
    private final SettingsInfoRepository settingsInfoRepository;
    private final JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer(
            new SerializingConverter(), new StratusUpgradingDeserializingConverter(new DeserializingConverter()));

    //For Stratus 1.2 backwards compatibility only
    private final ServiceInfoWrapperRepository serviceInfoWrapperRepository;
    private final RedisConfigProps redisConfigProps;

    public static final String GLOBAL_ID = "GeoServerInfo.global";
    public static final String GLOBAL_LOGGING_ID = "LoggingInfo.global";
    public static final String NO_WORKSPACE = "NO_WORKSPACE";

    public RedisGeoServerFacade(GeoServerInfoRepository geoServerInfoRepository, LoggingInfoRepository loggingInfoRepository,
                                ServiceInfoWrapperRepository serviceInfoWrapperRepository, List<AbstractServiceInfoRepository> serviceInfoRepositories, SettingsInfoRepository settingsInfoRepository,
                                GeoServer geoServer, List<ServiceInfoRegisteringBean> serviceInfoRegisteringBeans, RedisConfigProps configProps) {
        this.geoServerInfoRepository = geoServerInfoRepository;
        this.loggingInfoRepository = loggingInfoRepository;
        this.serviceInfoWrapperRepository = serviceInfoWrapperRepository;
        this.serviceInfoRepositories = serviceInfoRepositories;
        this.settingsInfoRepository = settingsInfoRepository;
        this.geoServer = geoServer;
        this.redisConfigProps = configProps;
        for (ServiceInfoRegisteringBean serviceInfoRegisteringBean : serviceInfoRegisteringBeans) {
            serviceInfoRegisteringBean.register(this);
        }
        // Register generic ServiceInfo repository
        ServiceClassMapping<ServiceInfo, ServiceInfoImpl, ServiceInfoRedisImpl> entry = new ServiceClassMapping<>
                (ServiceInfo.class, ServiceInfoImpl.class, ServiceInfoRedisImpl.class, ServiceInfoRepository.class);
        // Don't add ServiceInfo as a key so that it can still be used to return everything
        serviceMappingRegistry.put(ServiceInfoImpl.class, entry);
        serviceMappingRegistry.put(ServiceInfoRedisImpl.class, entry);
        serviceMappingRegistry.put(ServiceInfoRepository.class, entry);
    }

    @Override
    public GeoServer getGeoServer() {
        return geoServer;
    }

    @Override
    public void setGeoServer(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    @Override
    public GeoServerInfo getGlobal() {
        GeoServerInfo geoServerInfo = geoServerInfoRepository.findById(GLOBAL_ID).orElse(null);
        if (geoServerInfo == null) {
            return null;
        }
        GeoServerInfoImpl global = new GeoServerInfoImpl();
        BeanUtils.copyProperties(geoServerInfo, global);
        global.setGeoServer(geoServer);
        return ModificationProxy.create(global, GeoServerInfo.class);
    }

    @Override
    public void setGlobal(GeoServerInfo globalProxy) {
        if (globalProxy == null) {
            return;
        }
        GeoServerInfo global = ModificationProxy.unwrap(globalProxy);
        OwsUtils.set(global, "id", GLOBAL_ID);

        if (null == global.getSettings()) {
            SettingsInfo defaultSettings = geoServer.getFactory().createSettings();
            add(defaultSettings);
            global.setSettings(defaultSettings);
        } else {
            add(global.getSettings());
        }
        saveGlobal(global);
    }

    @Override
    public void save(GeoServerInfo globalProxy) {
        ModificationProxy h = (ModificationProxy) Proxy.getInvocationHandler(globalProxy);
        geoServer.fireGlobalModified(globalProxy, h.getPropertyNames(), h.getOldValues(), h.getNewValues());
        h.commit();
        saveGlobal((GeoServerInfo) h.getProxyObject());
    }

    private void saveGlobal(GeoServerInfo global) {
        GeoServerInfoRedisImpl geoServerInfo = new GeoServerInfoRedisImpl();
        BeanUtils.copyProperties(global, geoServerInfo);

        // handle globalServices manually because the java Introspector does not respect "is" prefix for getters of
        // Boolean object types (as opposed to primitives)
        geoServerInfo.setGlobalServices(global.isGlobalServices());

        // The ThreadPoolExecutor is transient, but spring data doesn't honor that keyword.  If this value is set, it
        // will cause a deserialization issue.
        geoServerInfo.getCoverageAccess().setThreadPoolExecutor(null);
        geoServerInfoRepository.save(geoServerInfo);
    }

    @Override
    public SettingsInfo getSettings(WorkspaceInfo workspace) {
        SettingsInfoRedisImpl redisSettings = settingsInfoRepository.findByWorkspaceId(workspace.getId());
        if (null == redisSettings) {
            return null;
        }
        SettingsInfoImpl settings = new SettingsInfoImpl();
        BeanUtils.copyProperties(redisSettings, settings);
        return ModificationProxy.create(settings, SettingsInfo.class);
    }

    @Override
    public void add(SettingsInfo settings) {
        settings = ModificationProxy.unwrap(settings);
        if (null == settings.getId()) {
            RedisCatalogUtils.setId(settings, SettingsInfo.class);
        }
        saveRedisInfo(settings, settingsInfoRepository, SettingsInfoRedisImpl.class);
    }

    @Override
    public void save(SettingsInfo settings) {
        ModificationProxy h = (ModificationProxy) Proxy.getInvocationHandler(settings);
        geoServer.fireSettingsModified(settings, h.getPropertyNames(), h.getOldValues(), h.getNewValues());
        h.commit();
        saveRedisInfo((SettingsInfo) h.getProxyObject(), settingsInfoRepository, SettingsInfoRedisImpl.class);
    }

    @Override
    public void remove(SettingsInfo settings) {
        settingsInfoRepository.findById(settings.getId()).ifPresent(settingsInfoRepository::delete);
    }

    @Override
    public LoggingInfo getLogging() {
        LoggingInfoRedisImpl redisLogging = loggingInfoRepository.findById(GLOBAL_LOGGING_ID).orElse(null);
        if (redisLogging == null) {
            return null;
        }
        LoggingInfoImpl logging = new LoggingInfoImpl();
        BeanUtils.copyProperties(redisLogging, logging);
        return ModificationProxy.create(logging, LoggingInfo.class);
    }

    @Override
    public void setLogging(LoggingInfo logging) {
        logging = ModificationProxy.unwrap(logging);
        OwsUtils.set(logging, "id", GLOBAL_LOGGING_ID);
        saveRedisInfo(logging, loggingInfoRepository, LoggingInfoRedisImpl.class);
    }

    @Override
    public void save(LoggingInfo logging) {
        ModificationProxy h = (ModificationProxy) Proxy.getInvocationHandler(logging);
        geoServer.fireLoggingModified(logging, h.getPropertyNames(), h.getOldValues(), h.getNewValues());
        h.commit();
        saveRedisInfo((LoggingInfo) h.getProxyObject(), loggingInfoRepository, LoggingInfoRedisImpl.class);
    }

    @Override
    public void add(ServiceInfo service) {
        if (null == service) {
            return;
        }
        service = ModificationProxy.unwrap(service);

        if (null == service.getId()) {
            RedisCatalogUtils.setId(service, ServiceInfo.class);
        }

        saveRedisInfo(service, lookupServiceInfoRepository(service), redisClass(service.getClass()));
    }

    @Override
    public void remove(ServiceInfo service) {
        if (null != service) {
            service = ModificationProxy.unwrap(service);
            AbstractServiceInfoRepository repository = lookupServiceInfoRepository(service);
            if (repository ==  null) {
                log.error("Could not remove "+ service.getClass().getSimpleName()+"service: "+service.getId()+ " - no compatible repository");
            } else {
                repository.findById(service.getId()).ifPresent(repository::delete);
                //Stratus 1.2 backwards compatibility
                if (redisConfigProps.isEnableStratus12Upgrade()) {
                    serviceInfoWrapperRepository.findById(service.getId()).ifPresent(serviceInfoWrapperRepository::delete);
                }
            }
        }
    }

    @Override
    public void save(ServiceInfo service) {
        ModificationProxy h = (ModificationProxy) Proxy.getInvocationHandler(service);
        geoServer.fireServiceModified(service, h.getPropertyNames(), h.getOldValues(), h.getNewValues());
        h.commit();

        ServiceInfo proxyObject = (ServiceInfo) h.getProxyObject();
        saveRedisInfo(proxyObject, lookupServiceInfoRepository(proxyObject), redisClass(proxyObject.getClass()));
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            serviceInfoWrapperRepository.findById(proxyObject.getId()).ifPresent(serviceInfoWrapperRepository::delete);
        }
    }

    @Override
    public Collection<? extends ServiceInfo> getServices() {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Services(serviceInfoWrapperRepository.findAllByWorkspaceId(NO_WORKSPACE));
        }
        List<? extends AbstractServiceInfoRedisImpl> results = new ArrayList<>();
        for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
            results.addAll(repository.findAllByWorkspaceId(NO_WORKSPACE));
        }
        return proxifyServiceList(results);
    }

    @Override
    public Collection<? extends ServiceInfo> getServices(WorkspaceInfo workspace) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            if (workspace != null && workspace != ANY_WORKSPACE) {
                migrateStratus_1_2_Services(serviceInfoWrapperRepository.findAllByWorkspaceId(workspace.getId()));
            } else if (workspace != ANY_WORKSPACE) {
                migrateStratus_1_2_Services(serviceInfoWrapperRepository.findAllByWorkspaceId(NO_WORKSPACE));
            }
        }

        if (workspace != null && workspace != ANY_WORKSPACE) {
            List<? extends AbstractServiceInfoRedisImpl> results = new ArrayList<>();
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                results.addAll(repository.findAllByWorkspaceId(workspace.getId()));
            }
            return proxifyServiceList(results);
        } else if (workspace == ANY_WORKSPACE) {
            return getServices();
        } else {
            List<? extends AbstractServiceInfoRedisImpl> results = new ArrayList<>();
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                results.addAll(repository.findAllByWorkspaceId(NO_WORKSPACE));
            }
            return proxifyServiceList(results);
        }
    }

    @Override
    public <T extends ServiceInfo> T getService(Class<T> clazz) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Service(serviceInfoWrapperRepository.findByWorkspaceIdAndClazz(NO_WORKSPACE, clazz.getSimpleName()));
        }

        AbstractServiceInfoRedisImpl redisService = null;
        AbstractServiceInfoRepository serviceInfoRepository = lookupServiceInfoRepository(clazz);
        if (serviceInfoRepository != null) {
            redisService = serviceInfoRepository.findByWorkspaceIdAndClazz(NO_WORKSPACE, clazz.getSimpleName());
        } else {
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                redisService = repository.findByWorkspaceIdAndClazz(NO_WORKSPACE, clazz.getSimpleName());
                if (redisService != null) {
                    break;
                }
            }
        }
        return proxifyService(redisService, clazz);

    }

    @Override
    public <T extends ServiceInfo> T getService(WorkspaceInfo workspace, Class<T> clazz) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Service(serviceInfoWrapperRepository.findByWorkspaceIdAndClazz(workspace.getId(), getClassName(clazz)));
        }

        AbstractServiceInfoRedisImpl redisService = null;
        AbstractServiceInfoRepository serviceInfoRepository = lookupServiceInfoRepository(clazz);
        if (serviceInfoRepository != null) {
            redisService = serviceInfoRepository.findByWorkspaceIdAndClazz(workspace.getId(), getClassName(clazz));
        } else {
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                redisService = repository.findByWorkspaceIdAndClazz(workspace.getId(), getClassName(clazz));
                if (redisService != null) {
                    break;
                }
            }
        }
        return proxifyService(redisService, clazz);
    }

    @Override
    public <T extends ServiceInfo> T getService(String id, Class<T> clazz) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Service(serviceInfoWrapperRepository.findByIdAndClazz(id, getClassName(clazz)));
        }

        AbstractServiceInfoRedisImpl redisService = null;
        AbstractServiceInfoRepository serviceInfoRepository = lookupServiceInfoRepository(clazz);
        if (serviceInfoRepository != null) {
            redisService = serviceInfoRepository.findByIdAndClazz(id, getClassName(clazz));
        } else {
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                redisService = repository.findByIdAndClazz(id, getClassName(clazz));
                if (redisService != null) {
                    break;
                }
            }
        }
        return proxifyService(redisService, clazz);
    }

    @Override
    public <T extends ServiceInfo> T getServiceByName(String name, Class<T> clazz) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Service(serviceInfoWrapperRepository.findByNameAndClazz(name, getClassName(clazz)));
        }

        AbstractServiceInfoRedisImpl redisService = null;
        AbstractServiceInfoRepository serviceInfoRepository = lookupServiceInfoRepository(clazz);
        if (serviceInfoRepository != null) {
            redisService = serviceInfoRepository.findByNameAndClazz(name, getClassName(clazz));
        } else {
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                redisService = repository.findByNameAndClazz(name, getClassName(clazz));
                if (redisService != null) {
                    break;
                }
            }
        }
        return proxifyService(redisService, clazz);
    }

    @Override
    public <T extends ServiceInfo> T getServiceByName(String name, WorkspaceInfo workspace, Class<T> clazz) {
        //Stratus 1.2 backwards compatibility
        if (redisConfigProps.isEnableStratus12Upgrade()) {
            migrateStratus_1_2_Service(serviceInfoWrapperRepository.findByNameAndWorkspaceIdAndClazz(name, workspace.getId(), getClassName(clazz)));
        }

        AbstractServiceInfoRedisImpl redisService = null;
        AbstractServiceInfoRepository serviceInfoRepository = lookupServiceInfoRepository(clazz);
        if (serviceInfoRepository != null) {
            redisService = serviceInfoRepository.findByNameAndWorkspaceIdAndClazz(name, workspace.getId(), getClassName(clazz));
        } else {
            for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
                redisService = repository.findByNameAndWorkspaceIdAndClazz(name, workspace.getId(), getClassName(clazz));
                if (redisService != null) {
                    break;
                }
            }
        }
        return proxifyService(redisService, clazz);
    }

    private ServiceInfo decodeService(AbstractServiceInfoRedisImpl redisService) {
        if (null != redisService) {
            try {
                ServiceClassMapping classMapping = serviceMappingRegistry.get(redisService.getClass());
                ServiceInfo service = (ServiceInfo) classMapping.serviceImpl.newInstance();
                BeanUtils.copyProperties(redisService, service);
                service.setGeoServer(geoServer);
                // resolve workspace
                service.setWorkspace(ResolvingProxy.resolve(geoServer.getCatalog(), service.getWorkspace()));
                OwsUtils.resolveCollections(service);
                return service;
            } catch (Exception e) {
                log.error("Error converting from redis service info object.", e);
            }
        }
        return null;
    }

    private <T> T proxifyService(AbstractServiceInfoRedisImpl redisService, Class<T> clazz) {
        if (redisService == null) {
            return null;
        }
        ServiceClassMapping classMapping = serviceMappingRegistry.get(redisService.getClass());
        ServiceInfo service = decodeService(redisService);
        if (service == null) {
            return null;
        }
        return (T) ModificationProxy.create(service, classMapping.getServiceInterface());
    }

    private Collection<? extends ServiceInfo> proxifyServiceList(Collection<? extends AbstractServiceInfoRedisImpl> serviceListRedis) {
        List<ServiceInfo> services = new ArrayList<>();
        for (AbstractServiceInfoRedisImpl redisImpl : serviceListRedis) {
            ServiceInfo service = decodeService(redisImpl);
            if (service != null) {
                services.add(service);
            }
        }
        return ModificationProxy.createList(services, ServiceInfo.class);
    }

    private void migrateStratus_1_2_Services(List<ServiceInfoWrapper> wrappers) {
        for (ServiceInfoWrapper wrapper : wrappers) {
            migrateStratus_1_2_Service(wrapper);
        }
    }

    private void migrateStratus_1_2_Service(ServiceInfoWrapper wrapper) {
        if (wrapper != null) {
            try {
                ServiceInfo info = decodeServiceWrapper(wrapper);
                AbstractServiceInfoRepository repository = lookupServiceInfoRepository(info);
                saveRedisInfo(info, repository, redisClass(info.getClass()));
                if (repository.findById(info.getId()).isPresent()) {
                    serviceInfoWrapperRepository.delete(wrapper);
                }
            } catch (Exception e) {
                log.error("Could not migrate Stratus 1.2 " + wrapper.getClazz() + " with id " + wrapper.getId() + " to current Stratus configuration.");
            }
        }
    }

    @Override
    public void dispose() {}

    private <T extends Info> void saveRedisInfo(Info info, CrudRepository repository, Class<T> clazz) {
        if (repository == null || clazz == null) {
            log.error("Error saving redis info object of type " + info.getClass().getSimpleName() +
                    ": repository or implementation not found");
            return;
        }
        try {
            T redisInfo = clazz.newInstance();
            BeanUtils.copyProperties(info, redisInfo);

            if (redisInfo instanceof AbstractServiceInfoRedisImpl) {
                AbstractServiceInfoRedisImpl serviceInfoRedis = (AbstractServiceInfoRedisImpl) redisInfo;
                serviceInfoRedis.setClazz(serviceMappingRegistry.get(clazz).getServiceInterface().getSimpleName());
            }
            repository.save(redisInfo);
        } catch (Exception e) {
            log.error("Error creating redis info object.", e);
        }
    }

    //Four-way map of interface <-> impl <-> redis impl <-> repository
    private HashMap<Class<?>, ServiceClassMapping<?,?,?>> serviceMappingRegistry = new HashMap<>();

    @Getter
    private class ServiceClassMapping<IF extends ServiceInfo, IM extends ServiceInfoImpl, RM extends AbstractServiceInfoRedisImpl> {
        final Class<IF> serviceInterface;
        final Class<IM> serviceImpl;
        final Class<RM> redisImpl;
        final Class<? extends AbstractServiceInfoRepository<RM>> repositoryClass;

        public ServiceClassMapping(Class<IF> serviceInterface, Class<IM> serviceImpl, Class<RM> redisImpl, Class<? extends AbstractServiceInfoRepository<RM>> repositoryClass) {
            this.serviceInterface = serviceInterface;
            this.serviceImpl = serviceImpl;
            this.redisImpl = redisImpl;
            this.repositoryClass = repositoryClass;
        }
    }

    public <IF extends ServiceInfo, IM extends ServiceInfoImpl, RM extends AbstractServiceInfoRedisImpl> void registerServiceInfoMapping(
            Class<IF> serviceInterface, Class<IM> serviceImpl, Class<RM> redisImpl, Class<? extends AbstractServiceInfoRepository<RM>> repositoryClass) {
        ServiceClassMapping<IF, IM, RM> entry = new ServiceClassMapping<IF, IM, RM>(serviceInterface, serviceImpl, redisImpl, repositoryClass);
        serviceMappingRegistry.put(serviceInterface, entry);
        serviceMappingRegistry.put(serviceImpl, entry);
        serviceMappingRegistry.put(redisImpl, entry);
        serviceMappingRegistry.put(repositoryClass, entry);
    }

    private Class<? extends AbstractServiceInfoRedisImpl> redisClass(Class<? extends ServiceInfo> serviceInterface) {
        Class<? extends AbstractServiceInfoRedisImpl> redisClass = serviceMappingRegistry.get(serviceInterface) == null ?
                null : serviceMappingRegistry.get(serviceInterface).getRedisImpl();
        if (redisClass == null) {
            log.warn("Could not find matching redis class for " + serviceInterface.getSimpleName());
        }
        return redisClass;
    }

    private AbstractServiceInfoRepository lookupServiceInfoRepository(ServiceInfo info) {
        return lookupServiceInfoRepository(info.getClass());
    }

    private AbstractServiceInfoRepository lookupServiceInfoRepository(Class<? extends ServiceInfo> clazz) {
        if (serviceMappingRegistry.get(clazz) == null) {
            return null;
        }
        Class repositoryClass = serviceMappingRegistry.get(clazz).getRepositoryClass();

        for (AbstractServiceInfoRepository repository : serviceInfoRepositories) {
            if (repositoryClass.isAssignableFrom(repository.getClass())) {
                return repository;
            }
        }
        return null;
    }

    /* Stratus 1.2 Service Wrapper handling */

    private List buildServiceListProxy(Iterable<ServiceInfoWrapper> wrappers) {
        List services = new ArrayList();
        for (ServiceInfoWrapper wrapper : wrappers) {
            services.add(decodeServiceWrapper(wrapper));
        }
        return ModificationProxy.createList(services, ServiceInfo.class);
    }

    private ServiceInfoWrapper buildServiceWrapper(ServiceInfo service) {
        ServiceInfoWrapper wrapper = new ServiceInfoWrapper();
        wrapper.setClazz(getClassName(service.getClass()));
        wrapper.setName(service.getName());
        wrapper.setId(service.getId());
        if (null == service.getWorkspace()) {
            wrapper.setWorkspaceId(NO_WORKSPACE);
        } else {
            wrapper.setWorkspaceId(service.getWorkspace().getId());
        }
        // base 64 encode, otherwise spring data creates a hash field for every entry in the byte array
        wrapper.setSerializedObject(Base64.getEncoder().encodeToString(serializer.serialize(service)));
        return wrapper;
    }

    private ServiceInfo decodeServiceWrapper(ServiceInfoWrapper wrapper) {
        ServiceInfo service = (ServiceInfo) serializer.deserialize(Base64.getDecoder().decode(wrapper.getSerializedObject()));
        service.setGeoServer(geoServer);
        return service;
    }

    private String getClassName(Class clazz) {
        String name = clazz.getSimpleName();

        // if it's not an implementation class, assume it's the interface and use it
        if (!name.endsWith("Impl")) {
            return name;
        }

        Class[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            name = interfaces[0].getSimpleName();
        }
        return name;
    }

    private <T extends ServiceInfo> T proxifyServiceWrapper(ServiceInfoWrapper wrapper, Class<T> clazz) {
        return null == wrapper ? null : ModificationProxy.create((T) decodeServiceWrapper(wrapper), clazz);
    }

    /**************************************/
}
