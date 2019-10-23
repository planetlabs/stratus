/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

import java.util.Set;

/**
 * Created by aaryno on 1/10/17.
 */
public interface RedisSetRepository {


    long addToSet(String key, String value);

    Set<Object> getSetMembers(String key);

    Set<Object> getSetIntersection(String key1, String key2);

    Set<Object> getSetUnion(String key1, String key2);

    long removeFromSet(String key, String value);

//    List<String> scanSetKeys(String pattern);

}
