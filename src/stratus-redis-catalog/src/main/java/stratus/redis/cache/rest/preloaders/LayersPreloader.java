/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest.preloaders;

import org.geoserver.catalog.*;
import org.springframework.stereotype.Component;
import stratus.redis.cache.rest.Preloader;
import stratus.redis.index.engine.RedisMultiQueryCachingEngine;
import stratus.redis.index.engine.RedisValuesQuery;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/22/17
 */
@Component
public class LayersPreloader implements Preloader {

    private static final String[] PATHS = {
            "/rest/layers"
    };

    @Override
    public String[] getPaths() {
        return PATHS;
    }

    @Override
    public void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine) {
        //Just get everything
        queryEngine.getWorkspaces();
        queryEngine.getNamespaces();
        queryEngine.getStores(DataStoreInfo.class);
        queryEngine.getStores(CoverageStoreInfo.class);
        queryEngine.getStores(WMSStoreInfo.class);
        queryEngine.getResources(FeatureTypeInfo.class);
        queryEngine.getResources(CoverageInfo.class);
        queryEngine.getResources(WMSLayerInfo.class);
        RedisValuesQuery<LayerInfo> layersQuery = queryEngine.getLayers();
        queryEngine.getStyles(layersQuery);
    }
}
