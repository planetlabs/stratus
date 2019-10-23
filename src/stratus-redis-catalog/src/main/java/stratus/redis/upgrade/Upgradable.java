/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade;

/**
 * Marks any classes that must be upgraded before they can be used in the current Stratus environment.
 * T refers to the upgraded class.
 */
public interface Upgradable<T> {

    /**
     * Returns version of the current class that is compatible with the Stratus environment
     */
    T upgrade();

}
