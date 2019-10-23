/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms;

/**
 * Created by tbarsballe on 2017-03-02.
 */

import lombok.extern.slf4j.Slf4j;
import org.geoserver.platform.resource.Resource;

import javax.servlet.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Filter} which instantiates local cache of legend sizes for getCapabilities generation
 */
@Slf4j
public class WMSCachingFilter implements Filter {

    public static ThreadLocal<Map<Resource,Dimension>> LEGEND_SIZE = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Map<Resource, Dimension> legendCache = new HashMap<>();
        LEGEND_SIZE.set(legendCache);

        try {
            chain.doFilter(request, response);
        }
        finally {
            LEGEND_SIZE.remove();
        }
    }

    @Override
    public void destroy() {

    }
}
