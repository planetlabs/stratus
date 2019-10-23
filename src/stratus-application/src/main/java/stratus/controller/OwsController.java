/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.geoserver.ows.Dispatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author joshfix
 */
@AllArgsConstructor
@RestController
public class OwsController {
 
    private final Dispatcher dispatcher;

    @RequestMapping(path = {"/ows", "/*/ows", "/*/*/ows", "/wfs", "/*/wfs", "/*/*/wfs","/wfs/*", "/*/wfs/*", "/*/*/wfs/*", "/wms", "/*/wms", "/*/*/wms", "/wcs", "/*/wcs", "/*/*/wcs", "/wps", "/csw", "/*/wps", "/*/wps/*", "/*/*/wps/*", "/animate", "/kml", "/kml/*", "/kml/icon/**/*"})
    public void ows(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.handleRequest(request, response);
    }

}
