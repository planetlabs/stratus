/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.impl.DefaultGeoServerFacade;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.platform.GeoServerExtensions;
import stratus.redis.geoserver.DefaultGeoServerCache;
import stratus.redis.geoserver.GeoServerCache;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * A {@link Filter} which instantiates local cached {@link CatalogCache} and {@link GeoServerCache} instances on the
 * current request thread prior to the execution of the request, and unsets these caches after a request has completed.
 */
@Slf4j
public class CachingFilter implements Filter {

    public static ThreadLocal<CatalogCache> CATALOG = new ThreadLocal<>();
    public static ThreadLocal<GeoServerCache> GEOSERVER = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CatalogImpl catalog = new CatalogImpl();
        CatalogCache catalogCache = new DefaultCatalogCache(catalog);
        catalog.setFacade(catalogCache);

        try {
            GeoServerCache geoServerCache = new DefaultGeoServerCache(new GeoServerImpl());
            geoServerCache.setGeoServer(null);

            Field global = DefaultGeoServerFacade.class.getDeclaredField("global");
            global.setAccessible(true);
            global.set(geoServerCache, null);

            Field logging = DefaultGeoServerFacade.class.getDeclaredField("logging");
            logging.setAccessible(true);
            logging.set(geoServerCache, null);

            GEOSERVER.set(geoServerCache);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.warn("Unable to initialize GeoServerCache", e);
        }

        CATALOG.set(catalogCache);

        try {
            chain.doFilter(request, response);
        }
        finally {
            CATALOG.remove();
            GEOSERVER.remove();
            GeoServerExtensions.extensions(ThreadCachingBean.class)
                .forEach(ThreadCachingBean::clearThreadCache);
        }
    }

    @Override
    public void destroy() {

    }
}
