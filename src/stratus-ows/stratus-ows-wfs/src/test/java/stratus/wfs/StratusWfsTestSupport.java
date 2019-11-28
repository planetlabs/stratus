/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs;

import org.geoserver.catalog.Catalog;
import org.geoserver.ows.StratusOwsTestSupport;
import org.geoserver.test.GeoServerTestApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stratus.ows.OWSCachingCallback;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.index.LayerIndexListener;

@Component
public class StratusWfsTestSupport {

    @Autowired
    private WFSHandler handler;
    @Autowired
    private OWSCachingCallback owsCachingCallback;
    @Autowired
    private LayerIndexListener layerIndexListener;
    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    public void configureWfsTestPreSetup(GeoServerTestApplicationContext applicationContext, Catalog catalog) {
        catalog.addListener(layerIndexListener);

    }
    public void configureWfsTestPostSetup(GeoServerTestApplicationContext applicationContext, Catalog catalog) throws NoSuchFieldException, IllegalAccessException {
        applicationContext.getBeanFactory().registerSingleton("wfsHandler", handler);
        //redisTestSupport.repository.flush();
        redisTestSupport.setCatalogFacade(applicationContext);
        redisTestSupport.setGeoServerFacade(applicationContext);

        StratusOwsTestSupport.configureOwsCachingCallback(applicationContext, owsCachingCallback);
    }

}
