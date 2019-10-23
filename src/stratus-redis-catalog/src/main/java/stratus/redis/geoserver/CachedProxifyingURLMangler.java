/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import org.geoserver.config.ConfigurationListenerAdapter;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.SettingsInfo;
import org.geoserver.ows.URLMangler;
import org.geoserver.platform.GeoServerExtensions;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CachedProxifyingURLMangler implements URLMangler {

    private String proxyBase;

    public CachedProxifyingURLMangler(GeoServer geoServer) {
        //first check the system property
        proxyBase = GeoServerExtensions.getProperty("PROXY_BASE_URL");
        if (proxyBase == null) {
            //if no system property fall back to configuration
            proxyBase = geoServer.getSettings().getProxyBaseUrl();
            geoServer.addListener(new ConfigurationListenerAdapter() {
				@Override
				public void handleSettingsPostModified(SettingsInfo settings) {
					proxyBase = settings.getProxyBaseUrl();
				}
				
				@Override
				public void handlePostGlobalChange(GeoServerInfo global) {
					proxyBase = global.getSettings().getProxyBaseUrl();
				}
            });
        }
    }

    public void mangleURL(StringBuilder baseURL, StringBuilder path, Map<String, String> kvp,
            URLType type) {
    	
        // perform the replacement if the proxy base is set
        if (proxyBase != null && proxyBase.trim().length() > 0) {
            baseURL.setLength(0);
            baseURL.append(proxyBase);
        }
    }

}
