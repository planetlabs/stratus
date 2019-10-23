/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.lock;

/**
 * @author joshfix
 * Created on 11/20/17
 */
@FunctionalInterface
public interface InitializationProvider {
    void initialize();
}
