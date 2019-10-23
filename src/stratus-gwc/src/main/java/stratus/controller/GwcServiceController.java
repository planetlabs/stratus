/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.geoserver.ows.Dispatcher;
import org.geowebcache.GeoWebCacheDispatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tbarsballe
 */
@RestController
@RequestMapping({"/gwc", "/*/gwc", "/*/*/gwc"}) //support virtual services
@AllArgsConstructor
public class GwcServiceController {

    private final Dispatcher dispatcher;

    // GWC Service requests go through the GeoServer OWS Dispatcher
    @RequestMapping("/"+GeoWebCacheDispatcher.TYPE_SERVICE+"/**")
    public void handleServiceRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        dispatcher.handleRequest(httpServletRequest, httpServletResponse);
    }
}
