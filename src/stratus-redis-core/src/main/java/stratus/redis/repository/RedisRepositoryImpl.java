/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author joshfix
 */
@Data
@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    protected final RedisTemplate<String, Object> redisTemplate;
    @NonNull protected RedisValueRepository redisValueRepository;
    @NonNull protected RedisHashRepository redisHashRepository;
    @NonNull protected RedisSetRepository redisSetRepository;
    private RedisConnection scanConnection;

    @Override
    public void deleteKey(String key) {
        log.debug("deleteKey key: " + key);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isGeoServerInitialized() {
        return Boolean.valueOf(redisValueRepository.getValue("GeoServer:Initialized"));
    }

    @Override
    public void setGeoServerInitialized(boolean init) {
        redisValueRepository.setValue("GeoServer:Initialized", Boolean.toString(init));
    }

    @Override
    public void setStoreInitialized(boolean init) {
        redisValueRepository.setValue("ResourceStore:Initialized", Boolean.toString(init));
    }

    @Override
    public boolean getStoreInitialized() {
        return Boolean.valueOf(redisValueRepository.getValue("ResourceStore:Initialized"));
    }

    @Override
    public List<String> scanKeys(String pattern) {
        List<String> results = new ArrayList<>();

        ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions();
        builder.match(pattern);

        if (null == scanConnection || scanConnection.isClosed()) {
            scanConnection = redisTemplate.getConnectionFactory().getConnection();
        }

        Iterator<byte[]> cursor = scanConnection.scan(builder.build());
        while (cursor.hasNext()) {
            String nextValue = new String(cursor.next());
            results.add(nextValue);
        }

        return results;

        // return new ArrayList<>(redisTemplate.keys(pattern));

    }

    @Override
    public boolean keyExists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    @Override
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public void deleteKeys(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    @Override
    public void renameKey(String key, String newKey) {
        redisTemplate.rename(key, newKey);
    }

    @Override
    public void flush() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Override
    public boolean isReadOnly() {
        return "1".equals(redisTemplate.getConnectionFactory().getConnection().info().getProperty("slave_read_only"));
    }
   
}
