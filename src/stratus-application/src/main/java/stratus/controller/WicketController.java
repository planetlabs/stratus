/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.controller;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by joshfix on 8/18/16.
 */
@AllArgsConstructor
@RestController
@ConditionalOnProperty(value = "stratus.admin-enabled", havingValue = "true")
public class WicketController {

    private final ServletWrappingController wicket;

    @RequestMapping(path = {"/web", "/web/**", "/web/resources/**"})
    public void doWicket(HttpServletRequest request, HttpServletResponse response) throws Exception {
        wicket.handleRequest(request, response);
    }
}
