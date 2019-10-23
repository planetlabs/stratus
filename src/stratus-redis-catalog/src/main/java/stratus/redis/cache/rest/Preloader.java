/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache.rest;

import stratus.redis.index.engine.RedisMultiQueryCachingEngine;

import java.util.Map;

/**
 * @author joshfix
 * Created on 9/21/17
 */

public interface Preloader {

    String WORKSPACE = "workspaceName";
    String DATASTORE = "dataStoreName";
    String DATASTORE_ALT = "storeName";
    String FEATURETYPE = "featureTypeName";
    String NAMESPACE = "namespaceName";
    String COVERAGESTORE = "storeName";
    String COVERAGE = "coverageName";
    String WMSSTORE = "storeName";
    String WMSLAYER = "layerName";
    String LAYER = "layerName";
    String LAYERGROUP = "layerGroupName";
    String STYLE = "styleName";
    String[] getPaths();
    void preload(String method, Map<String, String> pathVariables, RedisMultiQueryCachingEngine queryEngine);

}
