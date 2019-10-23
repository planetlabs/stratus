/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.NamespaceInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@RedisHash("NamespaceInfo")
public class NamespaceInfoRedisImpl implements NamespaceInfo, Serializable {

	private static final long serialVersionUID = 8742281956191030453L;

	@Id
    private String id;

    @Indexed
    private String prefix;
    
    @Indexed
    private String uri;

    private boolean isolated = false;
    
    private MetadataMap metadata;

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    }
    
    public String getName() {
        return getPrefix();
    }

    @Override
    public boolean isIsolated() {
        return isolated;
    }

    @Override
    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('[').append(prefix).append(':')
                .append(uri).append(']').toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof NamespaceInfo ) ) {
            return false;
        }

        final NamespaceInfo other = (NamespaceInfo) obj;
        if (prefix == null) {
            if (other.getPrefix() != null)
                return false;
        } else if (!prefix.equals(other.getPrefix()))
            return false;
        if (uri == null) {
            if (other.getURI() != null)
                return false;
        } else if (!uri.equals(other.getURI()))
            return false;

        return true;
    }

}
