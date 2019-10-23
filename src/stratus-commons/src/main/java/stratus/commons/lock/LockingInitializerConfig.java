/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.lock;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author joshfix
 * Created on 9/5/17
 */
@Data
@AllArgsConstructor
public class LockingInitializerConfig {

    private int minWait;
    private int maxWait;
    private int timeout;
    private String lockKeyPrefix;
    private String initializedKey;

}
