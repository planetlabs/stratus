/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.jndi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshfix on 09/01/17.
 */
@Getter
@Setter
@NoArgsConstructor
public class JndiSource {

    private String name;
    private String type = DataSource.class.getName();
    private Map<String, String> properties = new HashMap<>();

}
