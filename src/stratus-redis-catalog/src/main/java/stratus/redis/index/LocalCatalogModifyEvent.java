/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import org.geoserver.catalog.event.CatalogModifyEvent;

/**
 * ThreadLocal for storing {@link CatalogModifyEvent}, allowing later events to access what values were changed
 */
public class LocalCatalogModifyEvent {

    static ThreadLocal<CatalogModifyEvent> event = new ThreadLocal<>();

    public static void set(CatalogModifyEvent evt) {
        event.set(evt);
    }

    public static CatalogModifyEvent get() {
        return event.get();
    }

    public static void remove() {
        event.remove();
    }
}
