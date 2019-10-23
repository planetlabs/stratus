/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wcs;

import stratus.ows.OWSCachingException;
import stratus.ows.OWSCachingHandler;
import org.geoserver.ows.Request;

/**
 * Handle catalog caching for WCS requests
 */
public class WCSHandler extends OWSCachingHandler {
    @Override
    public void handle(String serviceName, String versionName, String requestName, String virtualWsName, String virtualLayerName, Request request) throws OWSCachingException {
        if ("WCS".equalsIgnoreCase(serviceName)) {
            if ("GetCapabilities".equalsIgnoreCase(requestName)) {
                //all Coverage layers

            } else if ("GetCoverage".equalsIgnoreCase(requestName) ||
                    "DescribeCoverage".equalsIgnoreCase(requestName)) {
                //coverageid key
            } else {

            }
        }
    }
}
