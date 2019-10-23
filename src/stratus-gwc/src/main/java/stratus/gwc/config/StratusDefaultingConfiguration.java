/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import lombok.extern.slf4j.Slf4j;
import org.geowebcache.config.DefaultingConfiguration;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.wms.WMSHttpHelper;
import org.geowebcache.layer.wms.WMSLayer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Stratus TileLayer Defaulting Configuration
 *
 * Note: Does not back out to redis; instead uses Stratus configuration. TODO: Configure via application.yaml
 */
@Slf4j
@Primary
@Service("stratusDefaultingConfiguration")
public class StratusDefaultingConfiguration implements DefaultingConfiguration{
    @Override
    public void setDefaultValues(TileLayer layer) {
        // Additional values that can have defaults set
        if (layer.isCacheBypassAllowed() == null) {
//            if (getGwcConfig().getCacheBypassAllowed() != null) {
//                layer.setCacheBypassAllowed(getGwcConfig().getCacheBypassAllowed());
//            } else {
                layer.setCacheBypassAllowed(false);
//            }
        }

        if (layer.getBackendTimeout() == null) {
//            if (getGwcConfig().getBackendTimeout() != null) {
//                layer.setBackendTimeout(getGwcConfig().getBackendTimeout());
//            } else {
                layer.setBackendTimeout(120);
//            }
        }

//        if (layer.getFormatModifiers() == null) {
//            if (getGwcConfig().getFormatModifiers() != null) {
//                layer.setFormatModifiers(getGwcConfig().getFormatModifiers());
//            }
//        }

        if (layer instanceof WMSLayer) {
            WMSLayer wl = (WMSLayer) layer;

            URL proxyUrl = null;
            try {
                /* if (getGwcConfig().getProxyUrl() != null) {
                    proxyUrl = new URL(getGwcConfig().getProxyUrl());
                    log.debug("Using proxy " + proxyUrl.getHost() + ":" + proxyUrl.getPort());
                } else */ if (wl.getProxyUrl() != null) {
                    proxyUrl = new URL(wl.getProxyUrl());
                    log.debug("Using proxy " + proxyUrl.getHost() + ":" + proxyUrl.getPort());
                }
            } catch (MalformedURLException e) {
                log.error("could not parse proxy URL " + wl.getProxyUrl()
                        + " ! continuing WITHOUT proxy!", e);
            }

            final WMSHttpHelper sourceHelper;

            if (wl.getHttpUsername() != null) {
                sourceHelper = new WMSHttpHelper(wl.getHttpUsername(), wl.getHttpPassword(),
                        proxyUrl);
                log.debug("Using per-layer HTTP credentials for " + wl.getName() + ", "
                        + "username " + wl.getHttpUsername());
//            } else if (getGwcConfig().getHttpUsername() != null) {
//                sourceHelper = new WMSHttpHelper(getGwcConfig().getHttpUsername(),
//                        getGwcConfig().getHttpPassword(), proxyUrl);
//                log.debug("Using global HTTP credentials for " + wl.getName());
            } else {
                sourceHelper = new WMSHttpHelper(null, null, proxyUrl);
                log.debug("Not using HTTP credentials for " + wl.getName());
            }

            wl.setSourceHelper(sourceHelper);
//            wl.setLockProvider(getGwcConfig().getLockProvider());
        }
    }

    @Override
    public String getIdentifier() {
        return "Stratus Defaulting Configuration";
    }

    @Override
    public String getLocation() {
        //TODO: Reference application.yaml?
        return null;
    }

    @Override
    public void deinitialize() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
