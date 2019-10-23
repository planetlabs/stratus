/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import lombok.Data;
import org.geoserver.catalog.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps a workspace or global layer group name to a list of layer names, representing the layers it contains.
 *
 * If a workspace and a layer group share a name, the workspace takes precedence.
 */
@Data
@RedisHash("VirtualServiceIndex")
public class VirtualServiceIndex implements Serializable {
    private String id;
    @Id
    private String name;

    private Type type;

    private Set<String> layers = new HashSet<>();
    private Set<IndexEntry> layerGroups = new HashSet<>();

    public enum Type {
        WORKSPACE,
        LAYER_GROUP
    }

    /**
     * Used by redis for deserialization. Should not be called directly, use {@link #VirtualServiceIndex(CatalogInfo, Catalog)} instead.
     */
    public VirtualServiceIndex() { }

    /**
     * Constructs a new VirtualServiceIndex entry
     *
     * @param info A Workspace or global LayerGroup to construct the entry from
*    * @param catalog Catalog to query for layers
     */
    public VirtualServiceIndex(CatalogInfo info, Catalog catalog) {
        if (info == null) {
            throw new NullPointerException("info must not be null");
        }
        //info = ModificationProxy.unwrap(info);
        id = info.getId();
        if (info instanceof WorkspaceInfo) {
            WorkspaceInfo wsInfo = (WorkspaceInfo) info;
            name = wsInfo.getName();
            NamespaceInfo nsInfo = catalog.getNamespaceByPrefix(wsInfo.getName());
            if (nsInfo != null) {
                //TODO: Remove the catalog lookups here? When creating a ws VSI, there should be no layers.
                //TODO: If the ws already exists, we should fetch the VSI and modify it.
                for (ResourceInfo resource : catalog.getResourcesByNamespace(nsInfo, ResourceInfo.class)) {
                    for (LayerInfo layer : catalog.getLayers(resource)) {
                        layers.add(layer.prefixedName());
                    }
                }
            }
            type = Type.WORKSPACE;

        } else if (info instanceof LayerGroupInfo) {
            LayerGroupInfo lgInfo = (LayerGroupInfo) info;
            if (lgInfo.getWorkspace() != null) {
                throw new IllegalArgumentException("info cannot be a workspaced layer group; must be global. Has workspace "
                        + lgInfo.getWorkspace().getName());
            }
            name = lgInfo.prefixedName();
            layers = LayerGroupIndex.expandLayers(lgInfo);
            layerGroups = LayerGroupIndex.expandLayerGroups(lgInfo);
            type = Type.LAYER_GROUP;
        } else {
            throw new IllegalArgumentException("info must be one of WorkspaceInfo or LayerGroupInfo. Was "
                    + info.getClass().getSimpleName());
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Set<String> getLayers() {
        return layers;
    }

    public Set<IndexEntry> getLayerGroups() {
        return layerGroups;
    }

    public void add(LayerInfo layer) {
        layers.add(layer.prefixedName());
    }
    public void add(LayerGroupInfo layerGroup) {
        layerGroups.addAll(LayerGroupIndex.expandLayerGroups(layerGroup));
    }
}
