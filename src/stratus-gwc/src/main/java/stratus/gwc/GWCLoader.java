/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc;

import stratus.Initializer;
import stratus.redis.catalog.config.StratusCatalogConfigProps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.GeoServer;
import org.geoserver.gwc.ConfigurableBlobStore;
import org.geoserver.gwc.GWC;
import org.geowebcache.config.*;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.storage.BlobStoreAggregator;
import org.geowebcache.storage.CompositeBlobStore;
import org.geowebcache.storage.DefaultStorageFinder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Performs GWC context initialization each time a node starts up or the catalog is reloaded
 */
@Slf4j
@Primary
@Service
@AllArgsConstructor
public class GWCLoader implements Initializer {

    private final GWCProperties properties;

    private final GeoServer geoserver;
    private final CatalogImpl catalog;
    private final StratusCatalogConfigProps configProps;

    private final GWC gwc;
    private final GridSetBroker gridSetBroker;
    private final TileLayerDispatcher tileLayerDispatcher;
    private final BlobStoreAggregator blobStoreAggregator;

    private final ServerConfiguration serverConfiguration;
    private final TileLayerConfiguration tileLayerConfiguration;
    private final GridSetConfiguration gridSetConfiguration;

    private final DefaultStorageFinder defaultStorageFinder;
    private final ConfigurableBlobStore blobStore;

    //Called by StratusInitializer.run()
    public void init() {
        // initialize ConfigurableBlobStore
        blobStore.setChanged(gwc.getConfig(), true);

        // create a default file blob store for development purposes (should be turned off in production)
        if (properties.isDefaultFileBlobStore() && !blobStoreAggregator.blobStoreExists(CompositeBlobStore.DEFAULT_STORE_DEFAULT_ID)) {
            try {
                FileBlobStoreInfo config = new FileBlobStoreInfo();
                config.setName(CompositeBlobStore.DEFAULT_STORE_DEFAULT_ID);
                config.setEnabled(true);
                config.setDefault(true);
                config.setBaseDirectory(defaultStorageFinder.getDefaultPath());
                blobStoreAggregator.addBlobStore(config);

            } catch (ConfigurationException | IllegalArgumentException e) {
                log.error("Error creating default file blob store configuration", e);
            }
        }
    }
}
