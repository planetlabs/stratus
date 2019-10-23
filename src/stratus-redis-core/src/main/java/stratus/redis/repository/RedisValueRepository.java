/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

/**
 * Created by aaryno on 1/10/17.
 */
public interface RedisValueRepository {

    String getValue(String key);

    String getSetValue(String key, String value);

    void setValue(String key, String value);

    Boolean setIfAbsent(String key, String value);

}
