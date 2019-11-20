/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import stratus.wps.model.StratusExecutionStatus;
import stratus.wps.redis.repository.StratusExecutionStatusRepository;

import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/rest/wps/{executionId}", produces = MediaType.APPLICATION_JSON_VALUE)
public class WPSWriteStatusController {
    private static final Logger LOGGER = org.geotools.util.logging.Logging.getLogger(WPSWriteStatusController.class);


    private final StratusExecutionStatusRepository wpsRepo;
    public WPSWriteStatusController(StratusExecutionStatusRepository wpsRepo){
        this.wpsRepo = wpsRepo;
    }

    @RequestMapping(method=RequestMethod.GET)
    public StratusExecutionStatus getStatus(@PathVariable String executionId){
        return wpsRepo.findById(executionId).orElse(null);
    }
}
