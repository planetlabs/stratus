/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.lock;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import stratus.commons.lock.LockingInitializerConfig;
import stratus.commons.lock.StratusLockProvider;
import stratus.redis.repository.RedisRepository;

/**
 * @author joshfix
 * Created on 9/28/17
 */
@Slf4j
@AllArgsConstructor
public class RedisStratusLockProvider implements StratusLockProvider {

    private LockingInitializerConfig config;
    private RedisRepository repository;

    private RedisStratusLockProvider() {}

    @Override
    public int getLockKeyCount() {
        return repository.getRedisSetRepository().getSetMembers(config.getLockKeyPrefix()).size();
    }

    @Override
    public boolean acquireLock(String lockKey, String identifier) {
        if (repository.isReadOnly()) {
            log.info("This Stratus instance is connected to a read-only redis slave.  Will not attempt to acquire lock.");
            return false;
        }
        return repository.getRedisValueRepository().setIfAbsent(lockKey, identifier);
    }

    @Override
    public void addSuccessfulLockKey(String lockKey) {
        repository.getRedisSetRepository().addToSet(config.getLockKeyPrefix(), lockKey);
    }

    @Override
    public void clearLocks() {
        repository.getRedisSetRepository().getSetMembers(config.getLockKeyPrefix()).forEach(key ->
                repository.deleteKey((String)key));
        repository.deleteKey(config.getLockKeyPrefix());
    }

    @Override
    public boolean isInitialized() {
        return Boolean.valueOf(repository.getRedisValueRepository().getValue(config.getInitializedKey()));
    }

    @Override
    public void setInitialized(boolean initialized) {
        repository.getRedisValueRepository().setValue(config.getInitializedKey(), Boolean.toString(initialized));
    }
}
