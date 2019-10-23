/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.terminal;

import lombok.Data;

/**
 * @author joshfix
 * Created on 6/15/18
 */
@Data
public class TerminalModel {
    private String redisHost;
    private int redisPort;
}
