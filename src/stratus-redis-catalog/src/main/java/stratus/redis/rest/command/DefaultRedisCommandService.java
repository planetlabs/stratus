/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.command;

import io.lettuce.core.protocol.CommandType;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Business logic to execute redis commands and return an ArrayList where each item in the list mimics a line of output
 * from the redis-cli application.
 *
 * @author joshfix
 * Created on 6/13/18
 */
@Service
@AllArgsConstructor
public class DefaultRedisCommandService implements RedisCommandService {

    public final RedisTemplate<String, Object> redisTemplate;

    /**
     * Executes an arbitrary redis command.
     *
     * @param body The plain text input from the POST request containing the full redis command
     * @return
     */
    @Override
    public List<String> execute(String body) {

        // trim and strip any quotes
        body = body.trim().replaceAll("\"", "");

        // get the command and make sure it's not "monitor"
        String command = body.split(" ")[0];
        if (command.equalsIgnoreCase("monitor")) {
            throw new IllegalArgumentException("The monitor command is not supported through this interface.");
        }

        try {
            CommandType.valueOf(command.toUpperCase());
        } catch (Exception e) {
            return Arrays.asList("(error) ERR unknown command '" + command + "'");
        }

        // convert the arguments into an array of byte[]
        byte[][] argBytes = null;
        if (body.contains(" ")) {
            String[] args = body.substring(command.length() + 1).split(" ");
            argBytes = new byte[args.length][];
            for (int i = 0; i < args.length; i++) {
                argBytes[i] = args[i].getBytes();
            }
        }
        final byte[][] finalArgBytes = argBytes;

        // the execution callback
        return redisTemplate.execute((RedisCallback<List<String>>) connection -> {
            // execute the command
            Object response;
            try {
                response = connection.execute(command, finalArgBytes);
            } catch (Exception e) {
                String message = (null == e.getCause()) ? e.getMessage() : e.getCause().getMessage();
                return Arrays.asList("(error) " + message);
            }
            int count = 0;

            // handle the response based on the response class type
            if (response instanceof byte[]) {
                return Arrays.asList(new String((byte[]) response));
            } else if (response instanceof Long) {
                return Arrays.asList(Long.toString((Long) response));
            } else if (response instanceof String) {
                return Arrays.asList(((String)response));
            } else if (response instanceof Collection) {
                if (((Collection) response).isEmpty()) {
                    return Arrays.asList("(empty list or set)");
                }

                List<String> lines = new ArrayList<>();
                for (Object bytes : (Collection) response) {
                    if (bytes instanceof byte[]) {
                        String line = new String((byte[]) bytes);
                        lines.add(++count + ") " + line);
                    }
                }
                return lines;
            } else if (response instanceof LinkedHashMap) {
                if (((LinkedHashMap) response).isEmpty()) {
                    return Arrays.asList("(empty list or set)");
                }

                List<String> lines = new ArrayList<>();
                LinkedHashMap<byte[], byte[]> responseMap = (LinkedHashMap) response;
                for (Map.Entry<byte[], byte[]> entry : responseMap.entrySet()) {
                    String hashKey = new String(entry.getKey());
                    String value = new String(entry.getValue());
                    lines.add(++count + ") " + hashKey);
                    lines.add(++count + ") " +value);
                }
                return lines;
            } else {
                return Arrays.asList("(nil)");
            }
        });
    }

}
