/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import stratus.redis.rest.RedisCatalogRestConstants;

import java.util.List;

/**
 * Provides endpoint for accepting arbitrary redis commands.
 *
 * @author joshfix
 * Created on 6/14/18
 */
@Data
@RestController
@AllArgsConstructor
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class RedisCommandController {

    private RedisCommandService service;

    /**
     * Executes an arbitrary command against redis.
     *
     * @param body The redis command -- same text that would normally be typed into redis-cli
     * @return Redis output
     */
    @RequestMapping(path="/exec",
            method = RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> exec(@RequestBody String body) {
        return service.execute(body);
    }

}
