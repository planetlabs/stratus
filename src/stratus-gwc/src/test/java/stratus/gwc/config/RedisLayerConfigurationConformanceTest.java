/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.redis.config.RedisConfigProps;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.easymock.EasyMock;
import org.geoserver.catalog.CatalogFacade;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.catalog.impl.DefaultCatalogFacade;
import org.geoserver.gwc.config.GWCConfig;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geowebcache.config.LayerConfigurationTest;
import org.geowebcache.config.TileLayerConfiguration;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.layer.TileLayer;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

/**
 * Redis integration test for {@link RedisGeoServerTileLayerConfiguration}
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisLayerConfigurationConformanceTest extends LayerConfigurationTest {

    @Autowired
    private RedisGWCTestSupport redisTestSupport;

    private CatalogImpl catalog;
    private CatalogFacade catalogFacade;

    private boolean failNextRead = false;
    private boolean failNextWrite = false;

    @Before
    @Override
    public void setUpTestUnit() throws Exception {
        // (re)set the catalog
        catalog = new CatalogImpl();
        catalogFacade = new DefaultCatalogFacade(catalog) {
            //Fake the layer portion of the catalog so it works with mocks
            Map<String, LayerInfo> layers = new HashMap<>();
            @Override
            public LayerInfo add(LayerInfo layer) {
                layers.put(layer.getId(), layer);
                return layer;
            }

            @Override
            public LayerInfo getLayer(String id) {
                return layers.get(id);
            }
        };
        catalog.setFacade(catalogFacade);

        super.setUpTestUnit();
    }
    @After
    public void tearDown() {
        // Clear the gwc config
        redisTestSupport.repository.flush();
    }

    @Override
    public void assertNameSetMatchesCollection() {
        //TODO stop turning this off.
    }
    
    @Override
    protected void doModifyInfo(TileLayer info, int rand) throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected TileLayer getGoodInfo(String id, int rand) throws Exception {


        FeatureTypeInfo ftInfo = EasyMock.createMock(FeatureTypeInfo.class); 
        LayerInfo layerInfo = EasyMock.createMock(LayerInfo.class); 
        GWCConfig gwcConfig = EasyMock.createMock(GWCConfig.class);
        GridSetBroker gsBroker = EasyMock.createMock(GridSetBroker.class);
        
        EasyMock.expect(ftInfo.getPrefixedName()).andStubReturn(id);
        EasyMock.expect(ftInfo.prefixedName()).andStubReturn(id);
        
        Map<String, Serializable> metadata = new HashMap<>();
        EasyMock.expect(layerInfo.getMetadata()).andStubReturn(new MetadataMap(metadata));
        EasyMock.expect(layerInfo.getResource()).andStubReturn(ftInfo);
        EasyMock.expect(layerInfo.getId()).andStubReturn("ID_FOR_"+id);
        
        EasyMock.expect(gwcConfig.isCacheLayersByDefault()).andStubReturn(true);
        EasyMock.expect(gwcConfig.isCacheNonDefaultStyles()).andStubReturn(false);
        EasyMock.expect(gwcConfig.getDefaultCachingGridSetIds()).andStubReturn(Collections.singleton("EPSG:4326"));
        EasyMock.expect(gwcConfig.getDefaultOtherCacheFormats()).andStubReturn(Collections.singleton("image/png"));
        EasyMock.expect(gwcConfig.getGutter()).andStubReturn(0);
        EasyMock.expect(gwcConfig.getMetaTilingX()).andStubReturn(4);
        EasyMock.expect(gwcConfig.getMetaTilingY()).andStubReturn(4);
        EasyMock.expect(gwcConfig.getDefaultVectorCacheFormats()).andStubReturn(Collections.singleton("image/png"));
        
        EasyMock.replay(ftInfo, layerInfo, gwcConfig, gsBroker);

        //Add layer to the facade so we skip validation
        catalogFacade.add(layerInfo);

        GeoServerTileLayer tl = new GeoServerTileLayer(layerInfo, gwcConfig, gsBroker);
        tl.setBlobStoreId(Integer.toString(rand));
        return tl;
    }
    
    @Override
    protected TileLayer getBadInfo(String id, int rand) throws Exception {
        TileLayer tl = EasyMock.createNiceMock(TileLayer.class);
        EasyMock.replay(tl);
        return tl;
    }
    
    @Override
    protected String getExistingInfo() {
        Assume.assumeFalse(true);
        return null;
    }
    
    @Override
    protected TileLayerConfiguration getConfig() throws Exception {
        TileLayerConfiguration config = new RedisGeoServerTileLayerConfiguration(redisTestSupport.repository, redisTestSupport.configProps, redisTestSupport.tlRepository, catalog);
        config.setGridSetBroker(EasyMock.createMock(GridSetBroker.class));
        
        return config;
    }
    
    @Override
    protected TileLayerConfiguration getSecondConfig() throws Exception {
        //getConfig creates a fresh config each time
        return getConfig();
    }
    
    @Override
    protected Matcher<TileLayer> infoEquals(TileLayer expected) {
        return both(Matchers.<TileLayer>hasProperty("name", equalTo(expected.getName())))
                .and(hasProperty("blobStoreId", equalTo(expected.getBlobStoreId())));
    }
    
    @Override
    protected Matcher<TileLayer> infoEquals(int rand) {
        return hasProperty("blobStoreId", equalTo(Integer.toString(rand)));
    }
    
    @Override
    public void failNextRead() {
        Assume.assumeFalse(true);
    }

    @Override
    public void failNextWrite() {
        Assume.assumeFalse(true);
    }
    
}
