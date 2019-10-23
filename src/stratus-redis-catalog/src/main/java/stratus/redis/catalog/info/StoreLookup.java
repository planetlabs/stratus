/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Data;
import org.geoserver.catalog.CatalogInfo;
import org.geoserver.catalog.CatalogVisitor;
import org.geoserver.catalog.StoreInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * @author joshfix
 * Created on 9/26/17
 */
@Data
@RedisHash("StoreLookup")
public class StoreLookup implements CatalogInfo, Serializable {

    private static final long serialVersionUID = -4105790694320469686L;
    @Id
    private String id;
    @Indexed
    private String workspaceId;
    @Indexed
    private String name;

    public static StoreLookup newInstance(StoreInfo store) {
        StoreLookup storeLookup = new StoreLookup();
        storeLookup.setWorkspaceId(store.getWorkspace().getId());
        storeLookup.setName(store.getName());
        return storeLookup;
    }

    public void update(StoreInfo store) {
        this.setWorkspaceId(store.getWorkspace().getId());
        this.setName(store.getName());
    }

    @Override
    public void accept(CatalogVisitor catalogVisitor) {}
}
