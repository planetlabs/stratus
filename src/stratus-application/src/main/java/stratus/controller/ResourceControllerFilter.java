/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author joshfix
 * Created on 10/18/17
 */
@Component
@WebFilter
public class ResourceControllerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String path = request.getPathInfo();
        if (null != path && path.startsWith("/rest/") && !path.startsWith("/rest/manage/")) {
            ServletRequest wrapper = new ResourceHttpServletRequestWrapper(request);
            filterChain.doFilter(wrapper, servletResponse);
        } else if (null != path && path.startsWith("/gwc/rest/")) {
            ServletRequest wrapper = new ResourceHttpServletRequestWrapper(request, "/gwc");
            filterChain.doFilter(wrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {}
}

class ResourceHttpServletRequestWrapper extends HttpServletRequestWrapper {

    final String servletPath;

    public ResourceHttpServletRequestWrapper(HttpServletRequest request) {
        this(request, "/rest");
    }

    public ResourceHttpServletRequestWrapper(HttpServletRequest request, String servletPath) {
        super(request);
        this.servletPath = servletPath;
    }

    @Override
    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        if (pathInfo.contains(servletPath + "/")) {
            pathInfo = pathInfo.replace(servletPath + "/", "/");
        }
        return pathInfo;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }
}