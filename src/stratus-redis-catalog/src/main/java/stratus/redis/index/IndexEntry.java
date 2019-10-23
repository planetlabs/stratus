/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import stratus.redis.catalog.RedisCatalogUtils;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.Info;

import java.io.Serializable;

/**
 * A pair containing a {@link Class} and a {@link String}, representing the interface and id of a {@link CatalogInfo}
 * object.
 */
public class IndexEntry implements Serializable {

    String clazz;
    String id;

    /**
     * Used by redis for deserialization. Should not be called directly, use {@link #IndexEntry(Class, String)} instead.
     */
    public IndexEntry() { }

    /**
     * Construct an IndexEntry object
     * @param clazz {@link Info} interface of the catalog object
     * @param id id of the catalog object
     */
    public IndexEntry(Class clazz, String id) {
        this.clazz = clazz.getCanonicalName();
        try {
            Class.forName(this.clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid class: "+this.clazz, e);
        }
        this.id = id;
    }

    public Class getClazz() {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid class: "+this.clazz, e);
        }
    }

    public String getId() {
        return id;
    }

    public String buildKey() {
        try {
            return RedisCatalogUtils.buildKey((Class<? extends Info>) Class.forName(clazz), id, null);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid class: "+this.clazz, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexEntry entry = (IndexEntry) o;

        if (clazz != null ? !clazz.equals(entry.clazz) : entry.clazz != null) return false;
        return id != null ? id.equals(entry.id) : entry.id == null;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
