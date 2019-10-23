/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import org.geowebcache.GeoWebCacheException;
import org.geowebcache.config.BlobStoreConfigurationListener;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Aggregates {@link BlobStoreConfigurationListener}s and allows for their handlers to be called in
 * a consistent way. 
 * 
 * @author smithkm
 * 
 * TODO we might want to up-port this to community.
 *
 */
public class BlobStoreConfigurationListenerCollection {
    
    List<BlobStoreConfigurationListener> listeners = new LinkedList<>();
    
    /**
     * Add a listener
     * @param listener
     */
    synchronized public void add(BlobStoreConfigurationListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a listener
     * @param listener
     */
    synchronized public void remove(BlobStoreConfigurationListener listener) {
        listeners.remove(listener);
    }
    
    @FunctionalInterface
    public static interface HandlerMethod {
        void callOn(BlobStoreConfigurationListener listener) throws GeoWebCacheException, IOException;
    }
    
    /**
     * Perform an operation on each listener.  If one throws an exception, the others will still
     * execute.  If more than one exception is thrown, the last will be the one propagated, with the
     * others added as suppressed exceptions.  If an Error is thrown, it will be propagated
     * immediately.
     * @param method
     * @throws GeoWebCacheException
     * @throws IOException
     */
    synchronized public void safeForEach(HandlerMethod method) throws GeoWebCacheException, IOException {
        LinkedList<Exception> exceptions = listeners.stream()
            .map(l->{
                try {
                    method.callOn(l);
                    return Optional.<Exception>empty();
                } catch (Exception ex) {
                    return Optional.of(ex);
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.collectingAndThen(Collectors.toList(), LinkedList::new));
        if(!exceptions.isEmpty()) {
            Iterator<Exception> it = exceptions.descendingIterator();
            Exception ex = it.next();
            while(it.hasNext()) {
                ex.addSuppressed(it.next());
            }
            if(ex instanceof GeoWebCacheException) {
                throw (GeoWebCacheException) ex;
            } else if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw (RuntimeException) ex;
            }
        }
    }
}
