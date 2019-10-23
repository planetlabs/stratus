/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest;

import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.security.GeoServerSecurityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prevents access to any /rest/redis endpoint without admin role
 *
 * @author joshfix
 * Created on 6/11/18
 */
@Component
public class CatalogRestInterceptor extends HandlerInterceptorAdapter {

    public final static String[] PATHS = {"/rest/redis/**"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!GeoServerExtensions.bean(GeoServerSecurityManager.class).checkAuthenticationForAdminRole()) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Full authentication is required to access this resource");
            return false;
        }
        return true;
    }

}
