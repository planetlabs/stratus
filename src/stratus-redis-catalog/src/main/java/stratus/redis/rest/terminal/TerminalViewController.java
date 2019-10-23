/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.terminal;

import stratus.redis.rest.RedisCatalogRestConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import static stratus.redis.rest.RedisCatalogRestConstants.TITLE_KEY;


/**
 * Controls the view and model attributes for the redis catalog terminal endpoint
 *
 * @author joshfix
 * Created on 6/13/18
 */
@Data
@Controller
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class TerminalViewController {

    @Autowired
    private TerminalService service;
    public static final String TITLE = "Stratus Redis Terminal";
    public static final String VIEW_NAME = "terminal";
    public static final String REDIS_CONFIG_ATTRIBUTE_NAME = "redisConfig";

    public TerminalViewController(TerminalService service) {
        this.service = service;
    }
    @RequestMapping(method = RequestMethod.GET, path = VIEW_NAME)
    public String terminal(Map<String, Object> model) {
        model.put(TITLE_KEY, TITLE);
        return VIEW_NAME;
    }

    @ModelAttribute(REDIS_CONFIG_ATTRIBUTE_NAME)
    public TerminalModel terminalModel() {
        return service.getTerminalModel();
    }

}
