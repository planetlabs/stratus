/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import org.geoserver.catalog.*;
import org.geoserver.catalog.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {@link CatalogListener} which maintains the {@link LayerIndex} and {@link LayerGroupIndex} indices by listening to
 * {@link CatalogEvent}s.
 */
public class LayerIndexListener implements CatalogListener {

    @Autowired
    @Qualifier("catalog")
    Catalog catalog;
    @Autowired
    RedisLayerIndexFacade indexFacade;


    public LayerIndexListener() { }

    public LayerIndexListener(Catalog catalog, RedisLayerIndexFacade indexFacade) {
        this.catalog = catalog;
        this.indexFacade = indexFacade;
    }

    @PostConstruct
    private void setListener() {
        catalog.addListener(this);
    }

    private String prefixedName(WorkspaceInfo workspace, String name) {
        return workspace != null ? workspace.getName() + ":" + name : name;
    }

    private boolean contains(PublishedInfo group, PublishedInfo layer) {
        if (group instanceof LayerGroupInfo) {
            LayerGroupInfo layerGroup = (LayerGroupInfo) group;
            for (PublishedInfo info : layerGroup.getLayers()) {
                if (info.getId().equals(layer.getId())) {
                    return true;
                }
                if (contains(info, layer)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<LayerGroupIndex> getContainingLayerGroupIndices(PublishedInfo layer) {
        List<LayerGroupIndex> indices = new ArrayList<>();
        for (LayerGroupInfo layerGroup : catalog.getLayerGroups()) {
            if (contains(layerGroup, layer)) {
                indices.add(new LayerGroupIndex(layerGroup));
            }
        }
        return indices;
    }
    private List<VirtualServiceIndex> getContainingVirtualServiceIndicies(LayerInfo layer) {
        List<String> vsiNames = new ArrayList<>();
        vsiNames.add(layer.getResource().getStore().getWorkspace().getName());
        for (LayerGroupInfo layerGroup : catalog.getLayerGroups()) {
            if (contains(layerGroup, layer) && layerGroup.getWorkspace() == null &&
                    !layerGroup.getName().equals(layer.getResource().getStore().getWorkspace().getName())) {
                vsiNames.add(layerGroup.prefixedName());
            }
        }
        return indexFacade.getVirtualServiceIndices(vsiNames);
    }

    @Override
    public void handleAddEvent(CatalogAddEvent event) throws CatalogException {
        if (event.getSource() instanceof WorkspaceInfo) {
            indexFacade.save(new VirtualServiceIndex(event.getSource(), catalog));
        }
        if (event.getSource() instanceof LayerInfo) {
            LayerInfo layer = (LayerInfo) event.getSource();
            indexFacade.save(new LayerIndex(layer));

            VirtualServiceIndex wsVsi = indexFacade.getVirtualServiceIndex(layer.getResource().getStore().getWorkspace().getName());
            if (wsVsi == null || wsVsi.getType() == VirtualServiceIndex.Type.LAYER_GROUP) {
                throw new IllegalStateException("Trying to add a layer without an indexed workspace");
            }
            wsVsi.add(layer);
            indexFacade.save(wsVsi);
        }
        if (event.getSource() instanceof LayerGroupInfo) {
            LayerGroupInfo layerGroup = (LayerGroupInfo) event.getSource();

            indexFacade.save(new LayerGroupIndex((LayerGroupInfo) event.getSource()));

            if (layerGroup.getWorkspace() == null) {
                VirtualServiceIndex vsi = indexFacade.getVirtualServiceIndex(layerGroup.getName());
                //Don't overwrite ws indices
                if (vsi == null || vsi.getType() == VirtualServiceIndex.Type.LAYER_GROUP) {
                    indexFacade.save(new VirtualServiceIndex(layerGroup, catalog));
                }
            }
        }
    }

    @Override
    public void handleRemoveEvent(CatalogRemoveEvent event) throws CatalogException {
        if (event.getSource() instanceof WorkspaceInfo) {
            //remove from index
            indexFacade.deleteVirtualServiceIndexByName(((WorkspaceInfo) event.getSource()).getName());
        }
        if (event.getSource() instanceof LayerInfo) {
            LayerInfo layer = (LayerInfo) event.getSource();
            //remove from index
            indexFacade.delete(new LayerIndex(layer));

            indexFacade.saveVirtualServiceIndices(getContainingVirtualServiceIndicies(layer));
        }
        if (event.getSource() instanceof LayerGroupInfo) {
            LayerGroupInfo layerGroup = (LayerGroupInfo) event.getSource();
            //remove from index
            indexFacade.delete(new LayerGroupIndex(layerGroup));

            if (layerGroup.getWorkspace() == null) {
                VirtualServiceIndex vsi = indexFacade.getVirtualServiceIndex(layerGroup.getName());
                //Don't delete ws indices
                if (vsi != null && vsi.getType() == VirtualServiceIndex.Type.LAYER_GROUP) {
                    indexFacade.deleteVirtualServiceIndexByName(layerGroup.prefixedName());
                }
            }
        }
        if (event.getSource() instanceof PublishedInfo) {
            //search for anything containing this or parent, regenerate all those indices
            indexFacade.saveLayerGroupIndices(getContainingLayerGroupIndices((PublishedInfo) event.getSource()));
        }
    }

    @Override
    public void handleModifyEvent(CatalogModifyEvent event) throws CatalogException {
        //Store change information to be used by CatalogPostModifyEvent, below
        //Only use relevant event types - note that namespace will get modified in between a workspace rename
        CatalogInfo info = event.getSource();
        if (info instanceof WorkspaceInfo || info instanceof  LayerInfo || info instanceof LayerGroupInfo) {
            LocalCatalogModifyEvent.set(event);
        }
    }

    @Override
    public void handlePostModifyEvent(CatalogPostModifyEvent event) throws CatalogException {
        CatalogModifyEvent modifyEvent = LocalCatalogModifyEvent.get();

        CatalogInfo info = event.getSource();
        if (info instanceof WorkspaceInfo || info instanceof  LayerInfo || info instanceof LayerGroupInfo) {
            //Only check this for relevant event types - note that namespace will get modified in between a workspace rename
            if (!Objects.equals(event.getSource().getId(), modifyEvent.getSource().getId())) {
                throw new IllegalStateException("Local CatalogModifyEvent does not match current PostModifyEvent. Expected " +
                        event.getSource().getId() + ", was " + modifyEvent.getSource().getId());
            }
            if (info instanceof WorkspaceInfo) {
                WorkspaceInfo workspace = (WorkspaceInfo) event.getSource();
                int nameIndex = modifyEvent.getPropertyNames().indexOf("name");
                //If name has been changed
                if (nameIndex >= 0 && !(modifyEvent.getOldValues().get(nameIndex).equals(modifyEvent.getNewValues().get(nameIndex)))) {
                    String oldName = (String) modifyEvent.getOldValues().get(nameIndex);


                    indexFacade.deleteVirtualServiceIndexByName(oldName);
                    indexFacade.save(new VirtualServiceIndex(workspace, catalog));
                }

            }
            if (info instanceof LayerInfo) {
                LayerInfo layer = (LayerInfo) event.getSource();
                int nameIndex = modifyEvent.getPropertyNames().indexOf("name");
                int wsIndex = modifyEvent.getPropertyNames().indexOf("workspace");

                boolean nameChanged = !(nameIndex >= 0 && Objects.equals(modifyEvent.getOldValues().get(nameIndex), modifyEvent.getNewValues().get(nameIndex))) ||
                        !(wsIndex >= 0 && Objects.equals(modifyEvent.getOldValues().get(wsIndex), modifyEvent.getNewValues().get(wsIndex)));

                //layer renamed; regenerate layer index
                if (nameChanged || modifyEvent.getPropertyNames().contains("defaultLayer") || modifyEvent.getPropertyNames().contains("layers")) {
                    indexFacade.save(new LayerIndex(layer));
                }

                //layer renamed; regenerate all containing indices
                if (nameChanged) {
                    indexFacade.saveLayerGroupIndices(getContainingLayerGroupIndices(layer));

                    indexFacade.saveVirtualServiceIndices(getContainingVirtualServiceIndicies(layer));
                }
            }

            if (info instanceof LayerGroupInfo) {
                LayerGroupInfo layerGroup = (LayerGroupInfo) event.getSource();
                int nameIndex = modifyEvent.getPropertyNames().indexOf("name");
                int wsIndex = modifyEvent.getPropertyNames().indexOf("workspace");
                int layersIndex = modifyEvent.getPropertyNames().indexOf("layers");

                String oldName = layerGroup.prefixedName();
                String newName = layerGroup.prefixedName();

                if (nameIndex >= 0 && wsIndex >= 0) {
                    oldName = prefixedName((WorkspaceInfo) modifyEvent.getOldValues().get(wsIndex), (String) modifyEvent.getOldValues().get(nameIndex));
                    newName = prefixedName((WorkspaceInfo) modifyEvent.getNewValues().get(wsIndex), (String) modifyEvent.getNewValues().get(nameIndex));
                } else if (nameIndex >= 0) {
                    oldName = prefixedName(layerGroup.getWorkspace(), (String) modifyEvent.getOldValues().get(nameIndex));
                    newName = prefixedName(layerGroup.getWorkspace(), (String) modifyEvent.getNewValues().get(nameIndex));
                } else if (wsIndex >= 0) {
                    oldName = prefixedName((WorkspaceInfo) modifyEvent.getOldValues().get(wsIndex), layerGroup.getName());
                    newName = prefixedName((WorkspaceInfo) modifyEvent.getNewValues().get(wsIndex), layerGroup.getName());
                }

                //layer group renamed; delete index, regenerate
                LayerGroupIndex layerGroupIndex = new LayerGroupIndex(layerGroup);
                layerGroupIndex.setName(newName);
                if (!newName.equals(oldName)) {
                    indexFacade.deleteLayerGroupIndexByName(oldName);

                    //If layers have changed, index will get regenerated below
                    if (layersIndex < 0 || Objects.equals(modifyEvent.getOldValues().get(layersIndex), modifyEvent.getNewValues().get(layersIndex))) {
                        indexFacade.save(layerGroupIndex);
                    }

                    VirtualServiceIndex vsi = indexFacade.getVirtualServiceIndex(oldName);
                    //Don't delete ws indices
                    if (vsi != null && vsi.getType() == VirtualServiceIndex.Type.LAYER_GROUP) {
                        indexFacade.deleteVirtualServiceIndexByName(oldName);
                    }
                    vsi = indexFacade.getVirtualServiceIndex(newName);
                    //Don't overwrite ws indices
                    if (layerGroup.getWorkspace() == null && (vsi == null || vsi.getType() == VirtualServiceIndex.Type.LAYER_GROUP)) {
                        indexFacade.save(new VirtualServiceIndex(layerGroup, catalog));
                    }
                }
                //layer group layers change, regenerate self and all containing indices
                if (!(layersIndex >= 0 && Objects.equals(modifyEvent.getOldValues().get(layersIndex), modifyEvent.getNewValues().get(layersIndex)))) {
                    List<LayerGroupIndex> indices = getContainingLayerGroupIndices(layerGroup);
                    indices.add(layerGroupIndex);
                    indexFacade.saveLayerGroupIndices(indices);
                }
            }
            LocalCatalogModifyEvent.remove();
        }
    }

    @Override
    public void reloaded() {

    }
}
