/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.geoserver.ows.FilePublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by joshfix on 8/22/16.
 */
@AllArgsConstructor
@RestController
public class FilePublisherController {

    private final FilePublisher filePublisher;

    @RequestMapping(path = {"/temp/**", "/styles/**", "/www/**"})
    public void doFilePublisher(HttpServletRequest request, HttpServletResponse response) throws Exception {
        filePublisher.handleRequest(request, response);
    }

}
