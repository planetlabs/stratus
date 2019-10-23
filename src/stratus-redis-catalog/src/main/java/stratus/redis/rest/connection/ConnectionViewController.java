/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.connection;

import stratus.redis.rest.RedisCatalogRestConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

import static stratus.redis.rest.RedisCatalogRestConstants.TITLE_KEY;

/**
 * Controls the view and model attributes for the redis catalog connection endpoint
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Slf4j
@Data
@Controller
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class ConnectionViewController {

    @Autowired
    private ConnectionService service;

    public static final String TITLE = "Redis Server Clients";
    public static final String VIEW_NAME = "connection";
    public static final String POOL_STATUS_ATTRIBUTE_NAME = "poolStatus";
    public static final String POOL_CONFIG_ATTRIBUTE_NAME = "poolConfig";
    public static final String CLIENTS_ATTRIBUTE_NAME = "clients";

    @ModelAttribute(POOL_STATUS_ATTRIBUTE_NAME)
    public ConnectionPoolStatusModel connectionPoolStatus() throws Exception {
        log.error("2");
        return service.connectionPoolStatus();
    }

    @ModelAttribute(POOL_CONFIG_ATTRIBUTE_NAME)
    public ConnectionPoolConfigModel connectionPoolConfig() {
        log.error("3");
        return service.connectionPoolConfig();
    }

    @ModelAttribute(CLIENTS_ATTRIBUTE_NAME)
    public List<ClientModel> clients() {
        log.error("4");
        return service.getClients();
    }

    @RequestMapping(method = RequestMethod.GET, path = VIEW_NAME)
    public String getClientInfo(Map<String, Object> model) {
        log.error("5");
        model.put(TITLE_KEY, TITLE);
        return VIEW_NAME;
    }


}
