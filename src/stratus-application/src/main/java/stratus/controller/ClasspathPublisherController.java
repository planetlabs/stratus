/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.geoserver.ows.ClasspathPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by joshfix on 8/22/16.
 */
@AllArgsConstructor
@RestController
public class ClasspathPublisherController {

    private final ClasspathPublisher classpathPublisher;

    @RequestMapping(path = {"/openlayers/**", "/options.png", "/openlayers3/**", "/schemas/**", "/j_acegi_security_check", "/login", "/j_spring_security_check"})
    public void doClasspathPublisher(HttpServletRequest request, HttpServletResponse response) throws Exception {
        classpathPublisher.handleRequest(request, response);
    }
}
