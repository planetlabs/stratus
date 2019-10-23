/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.lock;

/**
 * @author joshfix
 * Created on 9/28/17
 */
public interface StratusLockProvider {

    int getLockKeyCount();
    boolean acquireLock(String lockKey, String identifier);
    void addSuccessfulLockKey(String lockKey);
    void clearLocks();
    boolean isInitialized();
    void setInitialized(boolean initialized);

}
