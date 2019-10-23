/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("WorkspaceInfo")
public class WorkspaceInfoRedisImpl implements WorkspaceInfo, Serializable {
    private static final long serialVersionUID = -1754488599360427994L;

    public static final String NO_WORKSPACE_ID = "";

    @Id
    private String id;

    @Indexed
    private String name;

    private MetadataMap metadata;

    private boolean isolated;

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof WorkspaceInfo))
            return false;

        final WorkspaceInfo other = (WorkspaceInfo) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        return true;
    }

}