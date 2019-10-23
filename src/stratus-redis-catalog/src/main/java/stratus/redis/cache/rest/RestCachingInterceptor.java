/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest;

import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.geoserver.CachingGeoServerFacade;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "stratus.catalog.redis.caching", value = "enable-rest-caching", havingValue = "true")
public class RestCachingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    @Qualifier("catalog")
    private Catalog catalog;

    @Autowired
    @Qualifier("geoServer")
    private GeoServer geoServer;

    @Autowired
    private RedisLayerIndexFacade indexFacade;

    @Autowired
    @Qualifier("transactionalRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConverter converter;

    /**
     * The request paths that should be mapped by this interceptor.
     */
    public final static String[] PATHS = {"/rest/**"};

    /**
     * Contains mappings between regex path patterns the preloader associated with that pattern.
     */
    private final static Map<String, Preloader> preloaderMap = new HashMap<>();

    public RestCachingInterceptor(@Autowired(required = false) List<Preloader> preloaders) {
        preloaders.forEach(preloader ->
                Arrays.asList(preloader.getPaths()).forEach(path -> preloaderMap.put(path, preloader)));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        preloaderMap.entrySet().stream()
                .filter(entry -> new AntPathRequestMatcher(entry.getKey()).matches(request))
                .map(Map.Entry::getValue)
                .findFirst()
                .ifPresent(preloader -> handlePreload(preloader, request));

        return true;
    }

    /**
     * This function contains the logic to execute the preloading.
     *
     * @param preloader The preloader that matched the request path
     * @param request   HttpServloetRequest object
     */
    private void handlePreload(Preloader preloader, HttpServletRequest request) {
        CachingCatalogFacade cachingCatalogFacade = CachingCatalogFacade.unwrapCatalog(catalog);

        if (cachingCatalogFacade == null) {
            return;
        }

        CachingGeoServerFacade cachingGeoServerFacade = CachingGeoServerFacade.unwrapGeoServer(geoServer);
        RedisMultiQueryCachingEngine queryEngine = new RedisMultiQueryCachingEngine(redisTemplate, converter);

        Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (log.isDebugEnabled()) {
            log.debug("Invoking caching preloader: " + preloader.getClass().getSimpleName());
        }
        preloader.preload(request.getMethod(), pathVariables, queryEngine);

        //All catalog rest endpoints need the GeoServerInfo global
        queryEngine.getGeoServerInfo();

        //Execute any queries loaded into the query engine, then update the catalog cache
        queryEngine.execute(indexFacade.cacheProperties.getUseParallelQueries());
        queryEngine.loadIntoCache(cachingCatalogFacade, cachingGeoServerFacade);
        queryEngine.clear();
    }

}
