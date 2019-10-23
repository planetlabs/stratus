/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.connection;

import lombok.Data;

/**
 * Models the data for a redis client.
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Data
public class ClientModel {
    private String id;
    private String addressAndPort;
    private long database;
    private long age;
    private long channelSubscriptions;
    private String flags;
    private long bufferLength;
    private long bufferFreeSpace;
    private long outputBufferLength;
    private long outputBufferMemUsage;
    private String events;
    private long idle;
    private String lastCommand;
}
