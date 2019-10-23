/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.GeoServerTileLayerRedisInfoImpl;
import stratus.gwc.redis.repository.GeoServerTileLayerRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.*;
import org.geoserver.gwc.GWC;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.gwc.layer.TileLayerInfoUtil;
import org.geoserver.ows.LocalPublished;
import org.geoserver.ows.LocalWorkspace;
import org.geoserver.platform.GeoServerExtensions;
import org.geowebcache.config.TileLayerConfiguration;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayer;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * GeoWebCache TileLayerConfiguration which stores GeoServerTileLayers in a SpringData repository.
 * 
 * @author smithkm
 *
 */
@Slf4j
@Primary
@Service("stratusGWCTileLayerConfiguration")
public class RedisGeoServerTileLayerConfiguration extends BaseRedisConfiguration implements TileLayerConfiguration {

    private final GeoServerTileLayerRepository tlRepository;
    private final Catalog catalog;
    private GridSetBroker gridSetBroker;

    /**
     * Ids of pending deletes
     */
    private final Set<String> pendingDeletes = new CopyOnWriteArraySet<String>();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    public RedisGeoServerTileLayerConfiguration(RedisRepositoryImpl repository, RedisConfigProps configProps, GeoServerTileLayerRepository tlRepository, Catalog catalog) {
        super(repository, configProps, "Stratus Tile Layer Catalog");
        this.tlRepository = tlRepository;
        this.catalog = catalog;
    }

    @Deprecated
    @Override
    public List<? extends TileLayer> getTileLayers() {
        return (List<? extends TileLayer>)getLayers();
    }

    @Override
    public Collection<? extends TileLayer> getLayers() {
        List<GeoServerTileLayer> layers = new ArrayList<>();
        for (GeoServerTileLayerRedisInfoImpl layer : tlRepository.findAll()) {
            GeoServerTileLayer resolvedLayer = resolve(layer);
            if (resolvedLayer != null) {
                layers.add(resolvedLayer);
            }
        }
        return layers;
    }

    @Override
    public Optional<TileLayer> getLayer(String layerName) {
        final WorkspaceInfo ws = LocalWorkspace.get();
        if (ws != null && !layerName.startsWith(ws.getName() + ":")) {
            layerName = ws.getName() + ":" + layerName;
        }
        Optional<GeoServerTileLayerRedisInfoImpl> tl = tlRepository.findById(layerName);
        if (tl.isPresent()) {
            return Optional.ofNullable(resolve(tl.get()));
        } else {
            return Optional.empty();
        }
    }

    @Deprecated
    @Override
    public TileLayer getTileLayer(String layerName) {
        return getLayer(layerName).orElse(null);
    }

    @Deprecated
    @Override
    public TileLayer getTileLayerById(String layerId) {
        return getTileLayer(layerId);
    }

    @Override
    public int getLayerCount() {
        return new Long(tlRepository.count()).intValue();
    }

    @Deprecated
    @Override
    public int getTileLayerCount() {
        return getLayerCount();
    }

    @Override
    public Set<String> getLayerNames() {
        return getLayers().stream().map(TileLayer::getName).collect(Collectors.toSet());
    }

    @Deprecated
    @Override
    public Set<String> getTileLayerNames() {
        return getLayerNames();
    }

    @Transactional
    @Override
    public void removeLayer(String layerName)
            throws NoSuchElementException, IllegalArgumentException {
        Optional<GeoServerTileLayerRedisInfoImpl> tileLayer = tlRepository.findById(layerName);
        if (tileLayer.isPresent()) {
            try {
                pendingDeletes.add(tileLayer.get().getId());
                GWC.get().layerRemoved(tileLayer.get().getName());
            } catch (RuntimeException e) {
                log.error("Error deleting tile layer '" + tileLayer.get().getName() + "' from cache", e);
            }
            pendingDeletes.remove(tileLayer.get().getId());
            tlRepository.delete(tileLayer.get());
        } else {
            throw new NoSuchElementException("Cannot delete tile layer " + layerName + " - layer doesn't exist");
        }
    }

    @Transactional
    @Override
    public void modifyLayer(TileLayer tl) throws NoSuchElementException {
        if(tl instanceof GeoServerTileLayer) {
            if (containsLayer(tl.getName())) {
                save((GeoServerTileLayer) tl);
            } else {
                throw new NoSuchElementException("Cannot modify tile layer " + tl.getName() + " - layer doesn't exist");
            }
        } else {
            throw new IllegalArgumentException("Cannot modify tile layer " + tl.getName() + " - layer is not a GeoServerTileLayer");
        }
    }

    @Transactional
    @Override
    public void renameLayer(String oldName, String newName)
            throws NoSuchElementException, IllegalArgumentException {

        GeoServerTileLayerRedisInfoImpl oldLayer = tlRepository.findById(oldName).orElseThrow(() ->
                new NoSuchElementException("Cannot rename tile layer " + oldName + " - layer doesn't exist"));
        if (tlRepository.findById(newName).isPresent()) {
            throw new IllegalArgumentException("Cannot rename tile layer " + oldName + " to " + newName + " - layer already exists");
        }
        try {
            GWC.get().layerRenamed(oldName, newName);
        } catch (RuntimeException e) {
            log.error("Error renaming tile layer '" + oldName + "' to '" + newName + "' in cache", e);
        }
        tlRepository.findById(oldName).ifPresent(tlRepository::delete);
        oldLayer.setName(newName);
        tlRepository.save(oldLayer);
    }

    @Transactional
    @Override
    public void addLayer(TileLayer tl) throws IllegalArgumentException {
        if(tl instanceof GeoServerTileLayer) {
            if (containsLayer(tl.getName())) {
                throw new IllegalArgumentException("Cannot add tile layer " + tl.getName() + " - layer already exists");
            }
            save((GeoServerTileLayer) tl);
        } else {
            throw new IllegalArgumentException("Cannot add tile layer " + tl.getName() + " - layer is not a GeoServerTileLayer");
        }
    }

    @Override
    public boolean containsLayer(String layerName) {
        return tlRepository.findById(layerName).isPresent();
    }

    @Override
    public boolean canSave(TileLayer tl) {
        return tl instanceof GeoServerTileLayer;
    }

    @Override
    public void setGridSetBroker(GridSetBroker broker) {
        this.gridSetBroker = broker;
    }

    /**
     * Converts a {@link GeoServerTileLayer} to a {@link GeoServerTileLayerRedisInfoImpl} and saves it to
     * the {@link #tlRepository}
     *
     * @param tileLayer The layer to save
     */
    protected void save(GeoServerTileLayer tileLayer) {
        // TODO this tileLayer instance seems to already be a GeoServerTileLayerRedisInfoImpl when saving the web form?
        GeoServerTileLayerRedisInfoImpl redisInfo = new GeoServerTileLayerRedisInfoImpl();
        BeanUtils.copyProperties(tileLayer.getInfo(), redisInfo);
        tlRepository.save(redisInfo);
    }

    /**
     * Converts a {@link GeoServerTileLayerRedisInfoImpl} retrieved from the {@link #tlRepository} into a
     * {@link GeoServerTileLayer} that can be returned
     *
     * @param tileLayerRedis layer retrieved from redis
     * @return layer to be used by geoserver
     * @throws IllegalArgumentException if the Layer or LayerGroup associated with the tile layer doesn't exist
     */
    protected GeoServerTileLayer resolve(GeoServerTileLayerRedisInfoImpl tileLayerRedis) throws IllegalArgumentException {
        if (tileLayerRedis == null) {
            return null;
        }
        //return a layer so that the blob store has something to find
        if (pendingDeletes.contains(tileLayerRedis.getId())) {
            return new GeoServerTileLayer(catalog, tileLayerRedis.getId(), gridSetBroker, tileLayerRedis);
        }
        //tileLayerRedis
        PublishedInfo layer = catalog.getLayer(tileLayerRedis.getId());
        if (layer == null) {
            layer = catalog.getLayerGroup(tileLayerRedis.getId());
        }
        // let's see if this a virtual service request
        WorkspaceInfo localWorkspace = LocalWorkspace.get();
        PublishedInfo localPublished = LocalPublished.get();
        if (layer != null) {
            if (localWorkspace != null) {
                // yup this is a virtual service request, so we need to filter layers per workspace
                WorkspaceInfo layerWorkspace;
                if (layer instanceof LayerInfo) {
                    // this is a normal layer
                    layerWorkspace = ((LayerInfo) layer).getResource().getStore().getWorkspace();
                } else {
                    // this is a layer group
                    layerWorkspace = ((LayerGroupInfo) layer).getWorkspace();
                }
                // check if the layer doesn't have an workspace (this is possible for layer groups)
                if (layerWorkspace == null) {
                    // no workspace means that it doesn't belong to this workspace
                    return null;
                }
                // if the layer matches the virtual service workspace we return the layer otherwise NULL is returned
                if (!localWorkspace.getName().equals(layerWorkspace.getName())) {
                    return null;
                }

                // are we in a layer specific case too?
                if (localPublished != null && !localPublished.getName().equals(layer.getName())) {
                    return null;
                }
            } else if (localPublished != null) {
                // this implies we're looking at a global layer group, there is no such a thing
                // as a global layer
                //TODO: This is an error, virtual global layer group should also support containing layers?
                if (!(layer instanceof LayerGroupInfo)) {
                    return null;
                } else {
                    LayerGroupInfo lg = (LayerGroupInfo) layer;
                    if (lg.getWorkspace() != null || !lg.getName().equals(localPublished.getName())) {
                        return null;
                    }
                }
            }
            ParameterFilter styleParameterFilter = tileLayerRedis.getParameterFilter("STYLES");
            if (styleParameterFilter != null) {
                try {
                    styleParameterFilter.getLegalValues();
                } catch (IllegalStateException e) {
                    log.debug("Style parameter filter for tile layer '" + tileLayerRedis.toString()
                            + "'not initialized, initializing using the styles of "
                            + (layer instanceof LayerInfo ? "layer '" : "layer group '")
                            + layer.getName() + "'.");
                    TileLayerInfoUtil.checkAutomaticStyles(layer, tileLayerRedis);
                }
                styleParameterFilter = styleParameterFilter.clone();
            }
            //Use the constructor that sets the catalog, this is needed to remove the ws prefix (see GeoServerTileLayer#getNoPrefixedNameIfVirtualService())
            GeoServerTileLayer tileLayer = new GeoServerTileLayer(catalog, layer.getId(), gridSetBroker, tileLayerRedis.geoServerTileLayerInfo());

            //Set the PublishedInfo on the TileLayer
            //Temporarily unset the local workspace so the publishedInfo isn't wrapped in a NameDequalifyingProxy
            synchronized (LocalWorkspace.class) {
                if (localWorkspace != null) {
                    LocalWorkspace.set(null);
                }
                tileLayer.getPublishedInfo();
                if (localWorkspace != null) {
                    LocalWorkspace.set(localWorkspace);
                }
            }
            //As a side effect, setting the published info will update the availableStyles in the StyleParameterFilter
            //We want to wait for the CatalogLayerEventListener to trigger to do this, so we reset the StylePatameter filter
            if (styleParameterFilter != null) {
                tileLayer.getInfo().removeParameterFilter("STYLES");
                tileLayer.getInfo().addParameterFilter(styleParameterFilter);
            }
            return tileLayer;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        setGridSetBroker(GeoServerExtensions.bean(GridSetBroker.class));
    }
}
