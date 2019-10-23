/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.data;

import lombok.Data;
import org.springframework.data.redis.connection.DataType;

import java.util.Map;
import java.util.Set;

/**
 * @author joshfix
 * Created on 6/13/18
 */
@Data
public class DataModel {
    private DataType type;
    private String title;
    private String key;
    private String keyPrefix;
    private String value;
    private Set<String> set;
    private Map<String, String> hash;
}
