/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * All business logic to construct models used by the data view
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Service
public class DefaultDataService implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String KEYS_TITLE = "Redis Keys";
    public static final String HASH_TITLE = "Hash";
    public static final String SET_TITLE = "Set";
    public static final String VALUE_TITLE = "Value";

    @Override
    public DataModel getData(String key) {

        if (key == null){
            return keys();
        }

        DataModel model = new DataModel();

        DataType type = redisTemplate.type(key);

        model.setType(type);

        switch (type) {
            case HASH: {
                model.setTitle(HASH_TITLE);
                model.setKey(key);

                Map<String, String> hash = new TreeMap<>(Collections.reverseOrder());
                Map<byte[], byte[]> byteMap = redisTemplate.getConnectionFactory().getConnection().hGetAll(key.getBytes());
                for (Map.Entry<byte[], byte[]> e : byteMap.entrySet()) {
                    hash.put(new String(e.getKey()), new String(e.getValue()));
                }

                model.setHash(hash);
                return model;
            }
            case SET: {
                model.setTitle(SET_TITLE);
                model.setKey(key);
                if (key.contains(":")) {
                    model.setKeyPrefix(key.split(":")[0]);
                }
                model.setSet(redisTemplate.opsForSet().members(key));
                return model;
            }
            case STRING: {
                model.setTitle(VALUE_TITLE);
                model.setKey(key);
                model.setValue((String)redisTemplate.opsForValue().get(key));
                return model;
            }
            case NONE: {
                model.setTitle("Key does not exist");
                return model;
            }
        }
        model.setTitle("could not determine key type");
        return model;
    }

    private DataModel keys() {
        DataModel model = new DataModel();
        model.setTitle(KEYS_TITLE);
        model.setType(DataType.SET);
        model.setSet(redisTemplate.keys("*"));
        return model;
    }

}
