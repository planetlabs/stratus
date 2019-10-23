/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.info;

import stratus.redis.rest.RedisCatalogRestConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.Properties;

import static stratus.redis.rest.RedisCatalogRestConstants.TITLE_KEY;

/**
 * Controls the view and model attributes for the redis catalog info endpoint
 *
 * @author joshfix
 * Created on 6/13/18
 */
@Data
@Controller
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class InfoViewController {

    @Autowired
    private InfoService service;
    public static final String TITLE = "Stratus Redis Info";
    public static final String VIEW_NAME = "info";
    public static final String INFO_ATTRIBUTE_NAME = "info";

    @RequestMapping(method = RequestMethod.GET, path = VIEW_NAME)
    public String getInfo(Map<String, Object> model) {
        model.put(TITLE_KEY, TITLE);
        return VIEW_NAME;
    }

    @ModelAttribute(INFO_ATTRIBUTE_NAME)
    public Properties info() {
        return service.getInfo();
    }
}
