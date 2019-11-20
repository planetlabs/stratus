/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import lombok.Data;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import stratus.redis.catalog.impl.CatalogInfoConvert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps a layer name to a list of {@link IndexEntry} objects, representing all {@link org.geoserver.catalog.CatalogInfo}
 * objects that are referenced by the layer.
 */
@Data
@RedisHash("LayerIndex")
public class LayerIndex implements Serializable {

    private IndexEntry layerId;
    @Id private String name;


    private IndexEntry workspaceId;
    private IndexEntry namespaceId;
    private IndexEntry resourceId; //FeatureTypeInfo or CoverageInfo
    private IndexEntry storeId; //DataStoreInfo or CoverageStoreInfo
    private IndexEntry defaultStyleId;
    private List<IndexEntry> styleIds = new ArrayList<>();

    /**
     * Used by redis for deserialization. Should not be called directly, use {@link #LayerIndex(LayerInfo)} instead.
     */
    public LayerIndex() { }

    public LayerIndex(LayerInfo info) {
        name = info.prefixedName();

        layerId = new IndexEntry(LayerInfo.class, info.getId());

        workspaceId = new IndexEntry(WorkspaceInfo.class, info.getResource().getStore().getWorkspace().getId());
        namespaceId = new IndexEntry(NamespaceInfo.class, info.getResource().getNamespace().getId());
        resourceId = new IndexEntry(CatalogInfoConvert.root(info.getResource().getClass()), info.getResource().getId());
        storeId = new IndexEntry(CatalogInfoConvert.root(info.getResource().getStore().getClass()), info.getResource().getStore().getId());
        defaultStyleId = new IndexEntry(StyleInfo.class, info.getDefaultStyle().getId());

        for (StyleInfo s : info.getStyles()) {
            styleIds.add(new IndexEntry(StyleInfo.class, s.getId()));
        }
    }

    /**
     * Gets the list of ids
     * @return
     */
    public List<IndexEntry> getIds() {
        List<IndexEntry> ids = new ArrayList<>();

        ids.add(layerId);
        ids.add(workspaceId);
        ids.add(namespaceId);
        ids.add(resourceId);
        ids.add(storeId);
        ids.add(defaultStyleId);
        ids.addAll(styleIds);
        return ids;
    }

    public IndexEntry getLayerId() {
        return layerId;
    }

    public String getName() {
        return name;
    }

    public IndexEntry getWorkspaceId() {
        return workspaceId;
    }

    public IndexEntry getNamespaceId() {
        return namespaceId;
    }

    public IndexEntry getResourceId() {
        return resourceId;
    }

    public IndexEntry getStoreId() {
        return storeId;
    }

    public IndexEntry getDefaultStyleId() {
        return defaultStyleId;
    }

    public List<IndexEntry> getStyleIds() {
        return styleIds;
    }

}
