/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.geowebcache.GeoWebCacheDispatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author joshfix
 * Created on 11/16/17
 */
@RestController
@RequestMapping("/gwc")
@AllArgsConstructor
public class GwcController {

    private final GeoWebCacheDispatcher gwcDispatcher;

    // All other requests use the GeoWebCacheDispatcher
    @RequestMapping({"/"+GeoWebCacheDispatcher.TYPE_DEMO+"/**", "/"+GeoWebCacheDispatcher.TYPE_HOME+"/**", "/"+GeoWebCacheDispatcher.TYPE_REST+"/**"})
    public void handleGWCRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        gwcDispatcher.handleRequest(httpServletRequest, httpServletResponse);
    }
}
