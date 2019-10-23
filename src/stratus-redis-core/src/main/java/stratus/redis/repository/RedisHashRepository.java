/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

import java.util.Map;

/**
 * Created by aaryno on 1/9/17.
 */
public interface RedisHashRepository {
    Map<Object, Object> getHash(String key);

    Object getHashById(String key, String id);

    void setHash(String key, String id, Object info);

    void deleteHash(String key, String id);

    int hashLength(String key);
}
