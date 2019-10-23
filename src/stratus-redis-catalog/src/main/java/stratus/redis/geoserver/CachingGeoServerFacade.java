/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import stratus.redis.cache.CachingFacade;
import stratus.redis.cache.CachingFilter;
import org.geoserver.catalog.Info;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.config.*;
import org.geoserver.config.impl.GeoServerImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * GeoServer Facade which caches the configuration in a GeoServerInfo instance, and delegates to the passed facade.
 * Intended to be used when repeatedly resolving references in situations where looking up configuration objects is
 * expensive, namely when executing requests against a remote catalog.
 *
 * All get methods will first check the context cache, only querying the delegate facade if the requested object is not
 * found.
 * All other methods will just use the delegate. Note that this means if you update a catalog object, the cached version
 * will not be changed. Consequently, this class should generally be used in read-only situations.
 *
 * The cache is stored in a {@link ThreadLocal} ({@link CachingFilter#GEOSERVER}), and therefore caching operations will
 * only affect the current thread.
 */
public class CachingGeoServerFacade implements GeoServerFacade, CachingFacade<GeoServerCache, Info> {

    GeoServer geoServer;
    GeoServerFacade delegate;

    private static GeoServerCache dummyCache = new DummyGeoServerCache();

    public CachingGeoServerFacade(GeoServer geoServer, GeoServerFacade delegate) {
        this.geoServer = geoServer;
        this.delegate = delegate;
    }

    private GeoServerCache checkCache() {
        GeoServerCache cache = CachingFilter.GEOSERVER.get();
        if (cache != null) {
            cache.setGeoServer(geoServer);
            return cache;
        }
        return dummyCache;
    }

    /**
     * Returns the current cache. Since this is stored in a {@link ThreadLocal}, the same instance of
     * CachingCatalogFacade may return a different value for the cache, depending on the thread this is called from.
     *
     * @return cache
     */
    public GeoServerCache getCache() {
        return checkCache();
    }
    /**
     * Loads the provided list of CatalogInfo objects into the cache.
     * Future requests to the catalog will use the underlying facade if values are not found in the cache
     *
     * Since the cache is stored in a {@link ThreadLocal}, this will only apply to the current thread.
     * @param infos
     */
    public void loadCache(Iterable<Info> infos) {
        loadCache(infos, false);
    }

    /**
     * Loads the provided list of CatalogInfo objects into the cache.
     *
     * Since the cache is stored in a {@link ThreadLocal}, this will only apply to the current thread.
     *
     * @param infos
     * @param isComplete If true, marks the cache as an authoratative representation of the catalog.
     *                   Future requests to the catalog will NOT use the underlying facade if values are not found in
     *                   the cache.
     *                   Otherwise, future requests to the catalog will use the underlying facade if values are not
     *                   found in the cache.
     */
    public void loadCache(Iterable<Info> infos, boolean isComplete) {
        //TODO - test this...
        GeoServerCache cache = checkCache();
        //Load cache in order, such that AbstractCatalogFacade#resolve does not trigger unnecessary lookups
        for (Info info : infos) {
            if (info instanceof GeoServerInfo) {
                cache.setGlobal((GeoServerInfo) info);
            } else if (info instanceof ServiceInfo) {
                cache.add((ServiceInfo) info);
            } else if (info instanceof SettingsInfo) {
                cache.add((SettingsInfo) info);
            } else if (info instanceof LoggingInfo) {
                cache.setLogging((LoggingInfo) info);
            }
        }
        if (isComplete) {
            //Set flags for list gets
            cache.setServicesCached(true);
        }
    }
    /**
     * Retrieves an underlying {@link CachingGeoServerFacade} from a {@link GeoServer}.
     *
     * @param geoServer The GeoServer
     * @return The {@link CachingGeoServerFacade} underlying the geoserver, or null if no such facade exists.
     */
    public static CachingGeoServerFacade unwrapGeoServer(GeoServer geoServer) {
        GeoServerImpl geoserverImpl = ((GeoServerImpl) geoServer);

        if (geoserverImpl.getFacade() instanceof CachingGeoServerFacade) {
            return (CachingGeoServerFacade) geoserverImpl.getFacade();
        }
        return null;
    }

    @Override
    public void setGeoServer(GeoServer geoServer) {
        this.geoServer = geoServer;
        delegate.setGeoServer(geoServer);
    }

    /* caching methods */

    @Override
    public GeoServerInfo getGlobal() {
        GeoServerCache cache = checkCache();
        GeoServerInfo gsInfo = cache.getGlobal();
        if (gsInfo != null) {
            return gsInfo;
        }
        //Have we tried and failed before?
        if (cache.isCached("getGlobal", Collections.emptyList())) {
            return null;
        }
        gsInfo = delegate.getGlobal();
        if (gsInfo != null) {
            cache.setGlobal(ModificationProxy.unwrap(gsInfo));
        } else {
            cache.setCached("getGlobal", Collections.emptyList(), true);
        }
        return gsInfo;
    }
    @Override
    public SettingsInfo getSettings(WorkspaceInfo workspace) {
        GeoServerCache cache = checkCache();
        SettingsInfo settings = cache.getSettings(workspace);
        if (settings != null) {
            return settings;
        }
        //Have we tried and failed before?
        if (cache.isCached("getSettings", Collections.singletonList(workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace)))) {
            return null;
        }
        settings = delegate.getSettings(workspace);
        if (settings != null) {
            cache.add(ModificationProxy.unwrap(settings));
        } else {
            cache.setCached("getSettings", Collections.singletonList(workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace)), true);
        }
        return settings;
    }

    @Override
    public LoggingInfo getLogging() {
        GeoServerCache cache = checkCache();
        LoggingInfo logging = cache.getLogging();
        if (logging != null) {
            return logging;
        }
        //Have we tried and failed before?
        if (cache.isCached("getLogging", Collections.emptyList())) {
            return null;
        }
        logging = delegate.getLogging();
        if (logging != null) {
            cache.setLogging(ModificationProxy.unwrap(logging));
        } else {
            cache.setCached("getLogging", Collections.emptyList(), true);
        }
        return logging;
    }

    @Override
    public <T extends ServiceInfo> T getService(Class<T> clazz) {
        GeoServerCache cache = checkCache();
        T service = cache.getService(clazz);
        if (service != null) {
            return service;
        }
        //If we have loaded all services, querying delegate won't return anything
        if (cache.isServicesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getService", Collections.singletonList(clazz))) {
            return null;
        }
        service = delegate.getService(clazz);
        if (service != null) {
            cache.add(ModificationProxy.unwrap(service));
        } else {
            cache.setCached("getService", Collections.singletonList(clazz), true);
        }
        return service;
    }

    @Override
    public <T extends ServiceInfo> T getService(WorkspaceInfo workspace, Class<T> clazz) {
        GeoServerCache cache = checkCache();
        T service = cache.getService(workspace, clazz);
        if (service != null) {
            return service;
        }
        //If we have loaded all services, querying delegate won't return anything
        if (cache.isServicesCached() || cache.isServicesCached(workspace)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getService", Arrays.asList(workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace), clazz))) {
            return null;
        }
        service = delegate.getService(workspace, clazz);
        if (service != null) {
            cache.add(ModificationProxy.unwrap(service));
        } else {
            cache.setCached("getService", Arrays.asList(workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace), clazz), true);
        }
        return service;
    }

    @Override
    public <T extends ServiceInfo> T getService(String id, Class<T> clazz) {
        GeoServerCache cache = checkCache();
        T service = cache.getService(id, clazz);
        if (service != null) {
            return service;
        }
        //If we have loaded all services, querying delegate won't return anything
        if (cache.isServicesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getService", Arrays.asList(id, clazz))) {
            return null;
        }
        service = delegate.getService(id, clazz);
        if (service != null) {
            cache.add(ModificationProxy.unwrap(service));
        } else {
            cache.setCached("getService", Arrays.asList(id, clazz), true);
        }
        return service;
    }

    @Override
    public <T extends ServiceInfo> T getServiceByName(String name, Class<T> clazz) {
        GeoServerCache cache = checkCache();
        T service = cache.getServiceByName(name, clazz);
        if (service != null) {
            return service;
        }
        //If we have loaded all services, querying delegate won't return anything
        if (cache.isServicesCached()) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getServiceByName", Arrays.asList(name, clazz))) {
            return null;
        }
        service = delegate.getServiceByName(name, clazz);
        if (service != null) {
            cache.add(ModificationProxy.unwrap(service));
        } else {
            cache.setCached("getServiceByName", Arrays.asList(name, clazz), true);
        }
        return service;
    }

    @Override
    public <T extends ServiceInfo> T getServiceByName(String name, WorkspaceInfo workspace, Class<T> clazz) {
        GeoServerCache cache = checkCache();
        T service = cache.getServiceByName(name, workspace, clazz);
        if (service != null) {
            return service;
        }
        //If we have loaded all services, querying delegate won't return anything
        if (cache.isServicesCached() || cache.isServicesCached(workspace)) {
            return null;
        }
        //Have we tried and failed before?
        if (cache.isCached("getServiceByName", Arrays.asList(name, workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace), clazz))) {
            return null;
        }
        service = delegate.getServiceByName(name, workspace, clazz);
        if (service != null) {
            cache.add(ModificationProxy.unwrap(service));
        } else {
            cache.setCached("getServiceByName", Arrays.asList(name, workspace == null || workspace.getId() == null ? null : ModificationProxy.unwrap(workspace), clazz), true);
        }
        return service;
    }

    /* list gets */

    @Override
    public Collection<? extends ServiceInfo> getServices() {
        GeoServerCache cache = checkCache();
        if (cache.isServicesCached()) {
            return cache.getServices();
        }
        Collection<? extends ServiceInfo> services = delegate.getServices();
        if (services != null) {
            for (ServiceInfo service : services) {
                if (cache.getService(service.getId(), ServiceInfo.class) == null) {
                    cache.add(ModificationProxy.unwrap(service));
                }
            }
        }
        cache.setServicesCached(true);
        return services;
    }

    @Override
    public Collection<? extends ServiceInfo> getServices(WorkspaceInfo workspace) {
        GeoServerCache cache = checkCache();
        if (cache.isServicesCached(workspace)) {
            return cache.getServices(workspace);
        }
        Collection<? extends ServiceInfo> services = delegate.getServices(workspace);
        if (services != null) {
            for (ServiceInfo service : services) {
                if (cache.getService(service.getId(), ServiceInfo.class) == null) {
                    cache.add(ModificationProxy.unwrap(service));
                }
            }
        }
        cache.setServicesCached(workspace, true);
        return services;
    }

    /* delegate methods */

    @Override
    public GeoServer getGeoServer() {
        return delegate.getGeoServer();
    }

    @Override
    public void setGlobal(GeoServerInfo global) {
        delegate.setGlobal(global);
    }

    @Override
    public void save(GeoServerInfo geoServer) {
        delegate.save(geoServer);
    }

    @Override
    public void add(SettingsInfo settings) {
        delegate.add(settings);
    }

    @Override
    public void save(SettingsInfo settings) {
        delegate.save(settings);
    }

    @Override
    public void remove(SettingsInfo settings) {
        delegate.remove(settings);
    }

    @Override
    public void setLogging(LoggingInfo logging) {
        delegate.setLogging(logging);
    }

    @Override
    public void save(LoggingInfo logging) {
        delegate.save(logging);
    }

    @Override
    public void add(ServiceInfo service) {
        delegate.add(service);
    }

    @Override
    public void remove(ServiceInfo service) {
        delegate.remove(service);
    }

    @Override
    public void save(ServiceInfo service) {
        delegate.save(service);
    }

    @Override
    public void dispose() {
        delegate.dispose();
    }

    private static class DummyGeoServerCache implements GeoServerCache {

        @Override
        public GeoServer getGeoServer() {
            return null;
        }

        @Override
        public void setGeoServer(GeoServer geoServer) {

        }

        @Override
        public GeoServerInfo getGlobal() {
            return null;
        }

        @Override
        public void setGlobal(GeoServerInfo global) {

        }

        @Override
        public void save(GeoServerInfo geoServer) {

        }

        @Override
        public SettingsInfo getSettings(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public void add(SettingsInfo settings) {

        }

        @Override
        public void save(SettingsInfo settings) {

        }

        @Override
        public void remove(SettingsInfo settings) {

        }

        @Override
        public LoggingInfo getLogging() {
            return null;
        }

        @Override
        public void setLogging(LoggingInfo logging) {

        }

        @Override
        public void save(LoggingInfo logging) {

        }

        @Override
        public void add(ServiceInfo service) {

        }

        @Override
        public void remove(ServiceInfo service) {

        }

        @Override
        public void save(ServiceInfo service) {

        }

        @Override
        public Collection<? extends ServiceInfo> getServices() {
            return null;
        }

        @Override
        public Collection<? extends ServiceInfo> getServices(WorkspaceInfo workspace) {
            return null;
        }

        @Override
        public <T extends ServiceInfo> T getService(Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ServiceInfo> T getService(WorkspaceInfo workspace, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ServiceInfo> T getService(String id, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ServiceInfo> T getServiceByName(String name, Class<T> clazz) {
            return null;
        }

        @Override
        public <T extends ServiceInfo> T getServiceByName(String name, WorkspaceInfo workspace, Class<T> clazz) {
            return null;
        }

        @Override
        public void dispose() {

        }

        @Override
        public boolean isCached(String method, List<Object> arguments) {
            return false;
        }

        @Override
        public void setCached(String method, List<Object> arguments, boolean cached) { }

        @Override
        public <T extends ServiceInfo> boolean isServicesCached() {
            return false;
        }

        @Override
        public <T extends ServiceInfo> boolean isServicesCached(WorkspaceInfo workspace) {
            return false;
        }

        @Override
        public <T extends ServiceInfo> void setServicesCached(boolean cached) { }

        @Override
        public <T extends ServiceInfo> void setServicesCached(WorkspaceInfo workspace, boolean cached) { }
    }
}
