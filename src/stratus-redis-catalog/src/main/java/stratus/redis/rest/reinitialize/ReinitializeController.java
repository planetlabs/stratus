/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.reinitialize;

import stratus.redis.rest.RedisCatalogRestConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static stratus.redis.rest.RedisCatalogRestConstants.TITLE_KEY;

/**
 * Controls the view and model attributes for the redis catalog reinitialize endpoint
 *
 * @author joshfix
 * Created on 6/15/18
 */
@Data
@Controller
@AllArgsConstructor
@RequestMapping(RedisCatalogRestConstants.BASE_PATH)
public class ReinitializeController {

    public ReinitializeService service;
    public static final String TITLE = "Stratus Reinitialize";
    public static final String VIEW_NAME = "reinitialize";
    public static final String MESSAGE_ATTRIBUTE = "message";
    public static final String MISSING_VERIFY_MESSAGE = "This endpoint will re-execute the initialization sequence " +
            "and recreate the default GeoServer objects that would normally be generated on a fresh Stratus deployment " +
            "with an empty Redis instance.  This should only be performed as a final measure if Stratus is producing " +
            "unexpected errors.  To proceed with this operation, please add the parameter '?verify=TRUE' to the URL.";
    public static final String INVALID_VERIFY_VALUE_MESSAGE = "The verify parameter must be 'TRUE' (case sensitive) " +
            "to proceed with this operation.";
    public static final String SUCCESS_MESSAGE = "The GeoServer subsystem has been reinitialized.  Please refer to " +
            "the Stratus logs for more details.";

    @GetMapping(VIEW_NAME)
    public String reinitialize(Map<String, Object> model,
                               @RequestParam(value = "verify", required = false) String verify) {
        model.put(TITLE_KEY, TITLE);
        if (null == verify) {
            model.put(MESSAGE_ATTRIBUTE, MISSING_VERIFY_MESSAGE);
        } else if (!verify.equals("TRUE")) {
            model.put(MESSAGE_ATTRIBUTE, INVALID_VERIFY_VALUE_MESSAGE);
        } else {
            service.reinitialize();
            model.put(MESSAGE_ATTRIBUTE, SUCCESS_MESSAGE);
        }
        return VIEW_NAME;
    }
}
