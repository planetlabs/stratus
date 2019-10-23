/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.cache;

/**
 * Bean which maintains a per request cache in a thread local
 * @author smithkm
 *
 */
public interface ThreadCachingBean {
    
    /**
     * Clear thread local caches for the current thread
     */
    public void clearThreadCache();
}
