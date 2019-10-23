/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import lombok.Data;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps a layer group name to a list of layer names and a list of layer group
 * {@link IndexEntry} objects, representing the layers and layer groups
 * it contains, respectively.
 */
@Data
@RedisHash("LayerGroupIndex")
public class LayerGroupIndex implements Serializable {
    private String id;
    @Id private String name;

    private Set<String> layers = new HashSet<>();
    private Set<IndexEntry> layerGroups = new HashSet<>();

    /**
     * Used by redis for deserialization. Should not be called directly, use {@link #LayerGroupIndex(LayerGroupInfo)} instead.
     */
    public LayerGroupIndex() { }

    public LayerGroupIndex(LayerGroupInfo layerGroup) {
        id = layerGroup.getId();
        name = layerGroup.prefixedName();
        layers = expandLayers(layerGroup);
        layerGroups = expandLayerGroups(layerGroup);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getLayers() {
        return layers;
    }

    public Set<IndexEntry> getLayerGroups() {
        return layerGroups;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected static Set<String> expandLayers(PublishedInfo info) {
        Set<String> layerNames = new HashSet<>();

        if (info instanceof LayerInfo) {
            layerNames.add(info.prefixedName());
        } else if (info instanceof LayerGroupInfo) {
            for (PublishedInfo i : ((LayerGroupInfo)info).getLayers()) {
                layerNames.addAll(expandLayers(i));
            }
        }
        return layerNames;
    }

    protected static Set<IndexEntry> expandLayerGroups(PublishedInfo info) {
        Set<IndexEntry> layerGroups = new HashSet<>();

        if (info instanceof LayerGroupInfo) {
            layerGroups.add(new IndexEntry(LayerGroupInfo.class, info.getId()));
            for (PublishedInfo i : ((LayerGroupInfo)info).getLayers()) {
                layerGroups.addAll(expandLayerGroups(i));
            }
        }
        return layerGroups;
    }
}
