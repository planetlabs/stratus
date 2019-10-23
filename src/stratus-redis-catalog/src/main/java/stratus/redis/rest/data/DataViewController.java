/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.data;

import stratus.redis.rest.RedisCatalogRestConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static stratus.redis.rest.RedisCatalogRestConstants.TITLE_KEY;

/**
 * Controls the view and model attributes for the redis catalog data endpoint
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Data
@Controller
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class DataViewController {

    @Autowired
    private DataService service;
    private static final String TITLE = "Stratus Redis Data";
    public static final String VIEW_NAME = "data";
    public static final String DATA_ATTRIBUTE_NAME = "data";

    @RequestMapping(method = RequestMethod.GET, path = VIEW_NAME)
    public String getData(@RequestParam(value = "key", required = false) String key, Map<String, Object> model) {
        model.put(TITLE_KEY, TITLE);
        model.put(DATA_ATTRIBUTE_NAME, service.getData(key));
        return VIEW_NAME;
    }

}
