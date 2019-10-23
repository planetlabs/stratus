/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.web;

import org.geowebcache.rest.webresources.WebResourceBundle;

import java.net.URL;

/**
 * Adds Stratus branding to the GWC UI
 */
public class StratusGwcWebResourceBundle implements WebResourceBundle {
    @Override
    public URL apply(String s) {
        return StratusGwcWebResourceBundle.class.getResource(s);
    }
}
