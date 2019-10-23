/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

/**
 * Exception thrown when there was an error caching the catalog by an {@link OWSCachingHandler}.
 */
public class OWSCachingException extends Exception {
    public OWSCachingException(Throwable e) {
        super(e);
    }
}
