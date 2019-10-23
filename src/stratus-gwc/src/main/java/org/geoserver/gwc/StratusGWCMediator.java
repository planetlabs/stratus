/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;

import stratus.gwc.config.RedisGeoServerTileLayerConfiguration;
import org.geoserver.catalog.*;
import org.geoserver.gwc.config.GWCConfigPersister;
import org.geoserver.gwc.layer.CatalogConfiguration;
import org.geoserver.ows.Dispatcher;
import org.geowebcache.config.BaseConfiguration;
import org.geowebcache.diskquota.DiskQuotaMonitor;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.seed.TileBreeder;
import org.geowebcache.storage.BlobStoreAggregator;
import org.geowebcache.storage.DefaultStorageFinder;
import org.geowebcache.storage.StorageBroker;

/**
 * Extension of the GWC mediator to ovveride instance-of checks for {@link CatalogConfiguration} and other similar
 * implementation-specific limitations
 */
public class StratusGWCMediator extends GWC {

    private final TileLayerDispatcher tld;
    /**
     * Constructor for the GWC mediator
     *
     * @param gwcConfigPersister
     * @param sb                       The GeoWebCache StorageBroker
     * @param tld                      The GeoWebCache TileLayer Aggregator
     * @param gridSetBroker            The GeoWebCache GridSet Aggregator
     * @param tileBreeder              The GeoWebCache TileBreeder (Used for seeding)
     * @param monitor                  The GeoWebCache DiskQuota Monitor
     * @param owsDispatcher            The GeoServer OWS Service Dispatcher
     * @param catalog                  The GeoServer catalog, secured and filtered
     * @param rawCatalog               The raw GeoServer catalog, not secured. Use with extreme caution!
     * @param storageFinder            GeoWebcache system variable and configuration source
     * @param jdbcConfigurationStorage GeoServer integrator for GeoWebCache DiskQuota {@link JDBCConfiguration}
     * @param blobStoreAggregator      GeoWebCache BlobStore Aggregator
     */
    public StratusGWCMediator(GWCConfigPersister gwcConfigPersister, StorageBroker sb, TileLayerDispatcher tld, GridSetBroker gridSetBroker, TileBreeder tileBreeder, DiskQuotaMonitor monitor, Dispatcher owsDispatcher, Catalog catalog, Catalog rawCatalog, DefaultStorageFinder storageFinder, JDBCConfigurationStorage jdbcConfigurationStorage, BlobStoreAggregator blobStoreAggregator) {
        super(gwcConfigPersister, sb, tld, gridSetBroker, tileBreeder, monitor, owsDispatcher, catalog, rawCatalog, storageFinder, jdbcConfigurationStorage, blobStoreAggregator);
        this.tld = tld;
    }

    @Override
    public boolean hasTileLayer(CatalogInfo source) {
        final String tileLayerName;
        if (source instanceof ResourceInfo) {
            LayerInfo layerInfo = getCatalog().getLayerByName(
                    ((ResourceInfo) source).prefixedName());
            if (layerInfo == null) {
                return false;
            }
            tileLayerName = tileLayerName(layerInfo);
        } else if (source instanceof LayerInfo) {
            tileLayerName = tileLayerName((LayerInfo) source);
        } else if (source instanceof LayerGroupInfo) {
            tileLayerName = tileLayerName((LayerGroupInfo) source);
        } else {
            return false;
        }
        BaseConfiguration configuration;
        try {
            configuration = tld.getConfiguration(tileLayerName);
        } catch (IllegalArgumentException notFound) {
            return false;
        }
        return configuration instanceof RedisGeoServerTileLayerConfiguration;
    }
}
