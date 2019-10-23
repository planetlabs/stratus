/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.lock;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author joshfix
 * Created on 9/5/17
 */
@Slf4j
@Data
public class LockingInitializer {

    private int attempt;
    private int lockKeyIndex;
    private int minWait;
    private int maxWait;
    private int timeout;
    private boolean lockAcquired = false;
    private final String lockKeyPrefix;
    private final String initializedKey;
    private final StratusLockProvider lockProvider;
    private final Random random = new Random();
    private final String identifier = UUID.randomUUID().toString();

    public LockingInitializer(StratusLockProvider lockProvider, LockingInitializerConfig config) {
        this.lockProvider = lockProvider;
        this.minWait = config.getMinWait();
        this.maxWait = config.getMaxWait();
        this.timeout = config.getTimeout();
        this.lockKeyPrefix = config.getLockKeyPrefix();
        this.initializedKey = config.getInitializedKey();

        int lockKeyInitialCount = lockProvider.getLockKeyCount();
        if (lockKeyInitialCount == 0) {
            // if this is the first attempt and no other Stratus instance has attempted, use 0
            lockKeyIndex = 0;
        } else if (lockKeyInitialCount > 0) {
            // if this is the first attempt and other Stratus instances have attempted, use the last attempted value
            lockKeyIndex = lockKeyInitialCount - 1;
        }
    }

    public boolean acquireLock() {
        String lockKey = lockKeyPrefix + lockKeyIndex++;
        log.info("Attempting to acquire initialization lock for key: " + lockKeyPrefix);
        lockAcquired = lockProvider.acquireLock(lockKey, identifier);

        if (lockAcquired) {
            // add the successful lockKey to a set identified by the lockKeyPrefix
            lockProvider.addSuccessfulLockKey(lockKey);
            log.info("Lock acquired on initialization attempt " + lockKeyIndex +".");
        } else {
            log.info("Failed to acquire lock for key: " + lockKeyPrefix + ". Another instance has already acquired this lock.");
        }
        return lockAcquired;
    }

    public void clearLocks() {
        lockProvider.clearLocks();
    }

    public boolean isInitialized() {
        return lockProvider.isInitialized();
    }

    public void setInitialized(boolean initialized) {
        lockProvider.setInitialized(initialized);
    }

    public void execute(InitializationProvider initializationProvider) {
        if (isInitialized()) {
             log.info(lockKeyPrefix + " has been previously initialized.");
        }

        // continue to loop as long as target is not initialized and this Stratus instance doesn't have the initialization lock
        while (!isInitialized() && !lockAcquired) {

            Instant startTime = Instant.now();
            if (acquireLock()) {
                initializationProvider.initialize();
            } else {
                log.info("Entering wait cycle for other instance to complete initialization.");
                log.info("This instance will attempt to reacquire lock if initialization has not completed after " + timeout + "ms");
                log.info("Attempt " + lockKeyIndex + " - Lock identifier: " + identifier);

                // continue to wait while the initializer lock is still true
                while (!isInitialized() && !(Duration.between(startTime, Instant.now()).toMillis() > timeout)) {
                    int randomWait = random.nextInt(maxWait - minWait) + minWait;
                    log.info("Initialization not complete - waiting " + randomWait + "ms");
                    try {
                        TimeUnit.MILLISECONDS.sleep(randomWait);
                    } catch (InterruptedException e) {
                        log.info("Failed waiting for initialization of " + lockKeyPrefix + lockKeyIndex + " to complete.", e);
                    }
                }

                if (isInitialized()) {
                    log.info("The instance that acquired the lock has completed initialization.");
                } else {
                    log.info("The instance that acquired the lock has failed to complete initialization within the defined timeout period of " + timeout + "ms. Reattempting to acquire initialization lock.");
                }

            }
        }
        log.debug("Finished initialization loop.");
    }
}
