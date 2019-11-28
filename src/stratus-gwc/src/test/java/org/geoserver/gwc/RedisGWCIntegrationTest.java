/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.gwc;

import org.geoserver.catalog.*;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.gwc.layer.GeoServerTileLayer;
import org.geoserver.gwc.web.blob.BlobStoreType;
import org.geoserver.gwc.web.blob.BlobStoreTypes;
import org.geoserver.gwc.wmts.WMTSInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.TestSetup;
import org.geoserver.test.TestSetupFrequency;
import org.geotools.feature.NameImpl;
import org.geowebcache.GeoWebCacheException;
import org.geowebcache.GeoWebCacheExtensions;
import org.geowebcache.config.FileBlobStoreInfo;
import org.geowebcache.filter.parameters.CaseNormalizer;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetBroker;
import org.geowebcache.grid.GridSubset;
import org.geowebcache.layer.TileLayer;
import org.geowebcache.layer.TileLayerDispatcher;
import org.geowebcache.s3.S3BlobStoreInfo;
import org.geowebcache.storage.BlobStoreAggregator;
import org.geowebcache.storage.DefaultStorageFinder;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.config.GeoServerSystemImportResourcesConfig;
import stratus.gwc.GwcConfigProps;
import stratus.gwc.GwcLoader;
import stratus.gwc.config.*;
import stratus.gwc.redis.data.WMTSInfoClassRegisteringBean;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.catalog.config.StratusCatalogConfigProps;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.geoserver.data.test.MockData.BASIC_POLYGONS;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

@TestSetup(run = TestSetupFrequency.REPEAT)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GWCWithEmbeddedRedisConfig.class, GeoServerSystemImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisConfigProps.class, RedisLayerIndexFacade.class, CacheProperties.class,
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class, GwcLoader.class, GwcConfigProps.class,
        WMTSInfoClassRegisteringBean.class, WMSInfoClassRegisteringBean.class, WMSConfig.class,
        StratusCatalogConfigProps.class},
        properties = {"stratus.gwc.default-file-blob-store=true", "spring.main.allow-bean-definition-overriding=true"})

public class RedisGWCIntegrationTest extends GWCIntegrationTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;
    @Autowired
    private RedisGWCTestSupport redisGWCTestSupport;
    @Autowired
    private GeoServerSystemImportResourcesConfig importResourcesConfig;
    @Autowired
    private DefaultStorageFinder storageFinder;
    @Autowired
    GwcLoader gwcLoader;

    public static final String DEFAULT_STORE_DEFAULT_TEST_ID = "_DEFAULT_STORE_TEST_";

    @Override
    protected void setUpSpring(List<String> springContextLocations) {
        importResourcesConfig.buildFilteredApplicationContextXmlResourceList(springContextLocations, Arrays.asList("classpath*:/applicationContext.xml", "classpath*:/applicationSecurityContext.xml", "gwc-integration-test.xml"));
    }

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        redisTestSupport.repository.flush();

        RedisGeoServerTileLayerConfiguration layerConfiguration = new RedisGeoServerTileLayerConfiguration(redisGWCTestSupport.repository, redisGWCTestSupport.configProps, redisGWCTestSupport.tlRepository, getCatalog());

        applicationContext.getBeanFactory().registerSingleton("stratusGWCServerConfiguration",
                new RedisServerConfiguration(redisGWCTestSupport.repository, redisGWCTestSupport.configProps, redisGWCTestSupport.serverRepository));
        applicationContext.getBeanFactory().registerSingleton("stratusGWCBlobStoreConfiguration",
                new RedisBlobStoreConfiguration(redisGWCTestSupport.repository, redisGWCTestSupport.configProps, redisGWCTestSupport.bsRepository));
        applicationContext.getBeanFactory().registerSingleton("stratusGWCTileLayerConfiguration", layerConfiguration);
        applicationContext.getBeanFactory().registerSingleton("stratusGWCGridSetConfiguration", new RedisGridSetConfiguration(redisGWCTestSupport.repository, redisGWCTestSupport.configProps, redisGWCTestSupport.gsRepository));
        applicationContext.getBeanFactory().registerSingleton("stratusDefaultingConfiguration", new StratusDefaultingConfiguration());

        layerConfiguration.afterPropertiesSet();

        GeoWebCacheExtensions.bean(TileLayerDispatcher.class, applicationContext).afterPropertiesSet();
        GeoWebCacheExtensions.bean(GridSetBroker.class, applicationContext).afterPropertiesSet();
        GeoWebCacheExtensions.bean(BlobStoreAggregator.class, applicationContext).reInit();

        GWC.set(GeoServerExtensions.bean(GWC.class, applicationContext));
        GWC gwc = GWC.get();

        GeoWebCacheExtensions.bean(ConfigurableBlobStore.class, applicationContext).setChanged(gwc.getConfig(), true);

        //add a default blobstore to handle caching
        FileBlobStoreInfo config = new FileBlobStoreInfo();
        config.setName(DEFAULT_STORE_DEFAULT_TEST_ID);
        config.setEnabled(true);
        config.setDefault(true);
        config.setBaseDirectory(new File(getResourceLoader().getBaseDirectory(), "gwc").getPath());
        gwc.addBlobStore(config);

        redisTestSupport.setCatalogFacade(applicationContext);
        redisTestSupport.setGeoServerFacade(applicationContext);

        gwc.getConfig().getDefaultCoverageCacheFormats().add("image/png");
        gwc.getConfig().getDefaultVectorCacheFormats().add("image/jpeg");

        //gwc.removeTileLayers(new ArrayList<>(gwc.getTileLayerNames()));

        //Copy the layers from catalog into the tile catalog
        for (LayerInfo layer : getCatalog().getLayers()) {
            //Only tile layers with geometry
            if (((FeatureTypeInfo)layer.getResource()).getFeatureType().getGeometryDescriptor() != null) {
                gwc.add(new GeoServerTileLayer(layer, gwc.getConfig(), gwc.getGridSetBroker()));
            }
        }

        super.onSetUp(testData);

        //legacy setup with full catalog reload
        Catalog catalog = getCatalog();
        testData.addWorkspace(TEST_WORKSPACE_NAME, TEST_WORKSPACE_URI, catalog);
        WorkspaceInfo wi = catalog.getWorkspaceByName(TEST_WORKSPACE_NAME);
        testData.addStyle(
                wi, WORKSPACED_STYLE_NAME, WORKSPACED_STYLE_FILE, this.getClass(), catalog);
        assertThat(
                catalog.getStyleByName(wi, WORKSPACED_STYLE_NAME),
                Matchers.describedAs(
                        "Style %0 should be in workspace %1.",
                        (not(nullValue())), WORKSPACED_STYLE_NAME, TEST_WORKSPACE_NAME));
        Map<SystemTestData.LayerProperty, Object> props = new HashMap<>();
        props.put(SystemTestData.LayerProperty.STYLE, WORKSPACED_STYLE_NAME);
        testData.addVectorLayer(WORKSPACED_LAYER_QNAME, props, this.getClass(), catalog);
        LayerInfo li = catalog.getLayerByName(getLayerId(WORKSPACED_LAYER_QNAME));
        li.setDefaultStyle(catalog.getStyleByName(wi, WORKSPACED_STYLE_NAME));
        catalog.save(li);
        // add a simple layer group with two layers

        // get layers that match the layers names
        List<LayerInfo> layers =
                Arrays.stream(new QName[]{MockData.BUILDINGS, MockData.BRIDGES})
                        .map(layerName -> getCatalog().getLayerByName(new NameImpl(layerName)))
                        .collect(Collectors.toList());
        // create a new layer group using the provided name
        LayerGroupInfo layerGroup = getCatalog().getFactory().createLayerGroup();
        layerGroup.setName(SIMPLE_LAYER_GROUP);
        // add the provided layers
        for (LayerInfo layerInfo : layers) {
            layerGroup.getLayers().add(layerInfo);
            layerGroup.getStyles().add(null);
        }
        // set the layer group bounds by merging all layers bounds
        CatalogBuilder catalogBuilder = new CatalogBuilder(getCatalog());
        catalogBuilder.calculateLayerGroupBounds(layerGroup);
        getCatalog().add(layerGroup);


        GWC.get().getConfig().setDirectWMSIntegrationEnabled(false);
        // add a layer with no native CRS
        props = new HashMap<>();
        props.put(SystemTestData.LayerProperty.SRS, "4326");
        props.put(SystemTestData.LayerProperty.PROJECTION_POLICY, ProjectionPolicy.FORCE_DECLARED);
        testData.addVectorLayer(
                BASIC_POLYGONS_NO_CRS,
                null,
                "BasicPolygonsNoCrs.properties",
                this.getClass(),
                catalog);

        // add a style group (any caps request would fail without the fix in GEOS-9111)
        testData.addStyle("stylegroup", "stylegroup.sld", RedisGWCIntegrationTest.class, catalog);
        final LayerGroupInfo group = catalog.getFactory().createLayerGroup();
        group.getLayers().add(null);
        group.getStyles().add(catalog.getStyleByName("stylegroup"));
        group.setName("stylegroup");
        new LayerGroupHelper(group).calculateBounds();
        catalog.add(group);
        // clean up the recorded http requests
        HttpRequestRecorderCallback.reset();

        //Enable WMTS
        WMTSInfo wmts = getGeoServer().getService(WMTSInfo.class);
        wmts.setEnabled(true);
        getGeoServer().save(wmts);
    }

    @Override
    public void cleanup() throws Exception {
        //Stops super.cleanup() being invoked via @Before, as it interferes with a full catalog reload
    }

    //Replace CatalogConfiguration with RedisGeoServerTileLayerConfiguration
    @Override
    public void testMissingGeoServerLayerAtStartUp() throws Exception {

        Catalog catalog = getCatalog();
        GWC mediator = GWC.get();

        final String layerName = getLayerId(BASIC_POLYGONS);
        LayerInfo layerInfo = catalog.getLayerByName(layerName);
        assertNotNull(layerInfo);

        TileLayer tileLayer = mediator.getTileLayerByName(layerName);
        assertNotNull(tileLayer);
        assertTrue(tileLayer.isEnabled());

        getCatalog().remove(layerInfo);

        //Remove old test catalog so that reload doesn't try to read from it
        getCatalog().getResourceLoader().remove( "catalog.xml" );
        getCatalog().getResourceLoader().remove("layers");
        getCatalog().getResourceLoader().remove("workspaces");
        getGeoServer().reload();

        assertNull(catalog.getLayerByName(layerName));

        RedisGeoServerTileLayerConfiguration config = GeoServerExtensions.bean(RedisGeoServerTileLayerConfiguration.class);

        assertFalse(config.getLayer(layerName).isPresent());
        try {
            mediator.getTileLayerByName(layerName);
            fail("Expected IAE");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    //Need to actually save the changed ResourceInfos for this test to work with Stratus
    @Override
    public void testDirectWMSIntegrationMaxAge() throws Exception {
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final String layerName = BASIC_POLYGONS.getPrefix() + ":" + BASIC_POLYGONS.getLocalPart();
        final String path = buildGetMap(true, layerName, "EPSG:4326", null) + "&tiled=true";
        final String qualifiedName = super.getLayerId(BASIC_POLYGONS);
        final GeoServerTileLayer tileLayer = (GeoServerTileLayer) gwc.getTileLayerByName(qualifiedName);
        ((LayerInfo)tileLayer.getPublishedInfo()).getResource().getMetadata().put(ResourceInfo.CACHING_ENABLED, "true");
        ((LayerInfo)tileLayer.getPublishedInfo()).getResource().getMetadata().put(ResourceInfo.CACHE_AGE_MAX, 3456);
        getCatalog().save(((LayerInfo)tileLayer.getPublishedInfo()).getResource());

        MockHttpServletResponse response = getAsServletResponse(path);
        String cacheControl = response.getHeader("Cache-Control");
        assertEquals("max-age=3456", cacheControl);
        assertNotNull(response.getHeader("Last-Modified"));

        ((LayerInfo)tileLayer.getPublishedInfo()).getResource().getMetadata().put(ResourceInfo.CACHING_ENABLED, "false");
        getCatalog().save(((LayerInfo)tileLayer.getPublishedInfo()).getResource());

        response = getAsServletResponse(path);
        cacheControl = response.getHeader("Cache-Control");
        assertEquals("no-cache", cacheControl);

        // make sure a boolean is handled, too - see comment in CachingWebMapService
        ((LayerInfo)tileLayer.getPublishedInfo()).getResource().getMetadata().put(ResourceInfo.CACHING_ENABLED, Boolean.FALSE);
        getCatalog().save(((LayerInfo)tileLayer.getPublishedInfo()).getResource());

        response = getAsServletResponse(path);
        cacheControl = response.getHeader("Cache-Control");
        assertEquals("no-cache", cacheControl);
    }

    private String buildGetMap(final boolean directWMSIntegrationEndpoint, final String layerName,
                               final String gridsetId, String styles) {

        final GWC gwc = GWC.get();
        final TileLayer tileLayer = gwc.getTileLayerByName(layerName);
        return buildGetMap(directWMSIntegrationEndpoint, layerName, gridsetId, styles, tileLayer);
    }

    private String buildGetMap(final boolean directWMSIntegrationEndpoint,
                               final String queryLayerName, final String gridsetId, String styles,
                               final TileLayer tileLayer) {

        final GridSubset gridSubset = tileLayer.getGridSubset(gridsetId);

        long[] coverage = gridSubset.getCoverage(0);
        long[] tileIndex = { coverage[0], coverage[1], coverage[4] };
        BoundingBox bounds = gridSubset.boundsFromIndex(tileIndex);

        final String endpoint = directWMSIntegrationEndpoint ? "wms" : "gwc/service/wms";

        StringBuilder sb = new StringBuilder(endpoint);
        sb.append("?service=WMS&request=GetMap&version=1.1.1&format=image/png");
        sb.append("&layers=").append(queryLayerName);
        sb.append("&srs=").append(gridSubset.getSRS());
        sb.append("&width=").append(gridSubset.getGridSet().getTileWidth());
        sb.append("&height=").append(gridSubset.getGridSet().getTileHeight());
        sb.append("&styles=");
        if (styles != null) {
            sb.append(styles);
        }
        sb.append("&bbox=").append(bounds.toString());
        return sb.toString();
    }

    //Test assumed lg would automatically get the new workspace name; now fetches it from catalog for Stratus compatibility
    @Override
    public void testLayerGroupInWorkspace() throws Exception {
        // the workspace for the tests
        String workspaceName = MockData.BASIC_POLYGONS.getPrefix();

        // build a flat layer group with them, in the test workspace
        LayerGroupInfo lg = getCatalog().getFactory().createLayerGroup();
        lg.setName(WORKSPACED_LAYER_GROUP);
        String bpLayerId = getLayerId(MockData.BASIC_POLYGONS);
        String mpLayerId = getLayerId(MockData.LAKES);
        lg.getLayers().add(getCatalog().getLayerByName(bpLayerId));
        lg.getLayers().add(getCatalog().getLayerByName(mpLayerId));
        lg.getStyles().add(null);
        lg.getStyles().add(null);

        lg.setWorkspace(getCatalog().getWorkspaceByName(workspaceName));
        new CatalogBuilder(getCatalog()).calculateLayerGroupBounds(lg);
        getCatalog().add(lg);

        // wmts request, use the qualified name, first request, works, but it's a cache miss of
        // course
        String request = "gwc/service/wmts?request=GetTile&layer="
                + workspaceName
                + ":"
                + WORKSPACED_LAYER_GROUP
                + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        MockHttpServletResponse sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals("image/png", sr.getContentType());
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("MISS"));

        // run again, it should be a hit
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals("image/png", sr.getContentType());
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));

        // try direct integration too
        final GWC gwc = GWC.get();
        gwc.getConfig().setDirectWMSIntegrationEnabled(true);
        final TileLayer tileLayer = gwc.getTileLayerByName(lg.prefixedName());
        request = buildGetMap(true, lg.prefixedName(), "EPSG:4326", null, tileLayer)
                + "&tiled=true";
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals("image/png", sr.getContentType());
        assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));

        // and direct integration against the workspace local name
        request = workspaceName + "/"
                + buildGetMap(true, lg.getName(), "EPSG:4326", null, tileLayer) + "&tiled=true";
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));

        // now change the workspace name
        WorkspaceInfo ws = getCatalog().getWorkspaceByName(workspaceName);
        String newWorkspaceName = workspaceName + "_renamed";
        ws.setName(newWorkspaceName);
        getCatalog().save(ws);

        //Update the layer group with the new workspace
        lg = getCatalog().getLayerGroup(lg.getId());

        // prepare the wmts request anew, it should be a hit, the cache should be preserved
        request = "gwc/service/wmts?request=GetTile&layer="
                + newWorkspaceName
                + ":"
                + WORKSPACED_LAYER_GROUP
                + "&format=image/png&tilematrixset=EPSG:4326&tilematrix=EPSG:4326:0&tilerow=0&tilecol=0";
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals("image/png", sr.getContentType());
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));

        // and now direct integration
        String newQualifiedName = newWorkspaceName + ":" + lg.getName();
        request = buildGetMap(true, newQualifiedName, "EPSG:4326", null, tileLayer) + "&tiled=true";
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals("image/png", sr.getContentType());
        assertEquals(lg.prefixedName(), sr.getHeader("geowebcache-layer"));
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));

        // and direct integration against the workspace local name
        request = newWorkspaceName + "/"
                + buildGetMap(true, lg.getName(), "EPSG:4326", null, tileLayer) + "&tiled=true";
        sr = getAsServletResponse(request);
        assertEquals(200, sr.getStatus());
        assertEquals(newQualifiedName, sr.getHeader("geowebcache-layer"));
        assertThat(sr.getHeader("geowebcache-cache-result"), equalToIgnoringCase("HIT"));
    }

    //Extra test for testing deserialization of parameter filters
    @Test
    public void testSaveRegexParameterFilter() throws Exception {
        final String layerName = BASIC_POLYGONS.getPrefix() + ":" + BASIC_POLYGONS.getLocalPart();
        final GWC gwc = GWC.get();
        GeoServerTileLayer tl = (GeoServerTileLayer) gwc.getTileLayerByName(layerName);
        RegexParameterFilter filter = new RegexParameterFilter();
        filter.setKey("regex");
        filter.setDefaultValue("");
        filter.setRegex(".*");
        filter.setNormalize(new CaseNormalizer(CaseNormalizer.Case.NONE, Locale.CANADA));
        tl.getInfo().addParameterFilter(filter);

        gwc.save(tl);
        tl = (GeoServerTileLayer) gwc.getTileLayerByName(layerName);
        assertNotNull(tl);
        ParameterFilter parameterFilter = tl.getInfo().getParameterFilter("regex");
        assertTrue(parameterFilter instanceof RegexParameterFilter);
        RegexParameterFilter regexFilter = (RegexParameterFilter) parameterFilter;

        assertEquals(filter.getNormalize().getCase(), CaseNormalizer.Case.NONE);
        assertEquals(filter.getNormalize().getLocale(), Locale.CANADA);

    }

    // Make sure we don't get "IllegalStateException: Found no configuration of type org.geowebcache.config.XMLConfiguration"
    // when trying to remove a gridset via the GWC mediator
    @Test
    public void testRemoveGridsets() throws IOException, GeoWebCacheException {
        GWC gwc = GWC.get();

        GridSet testGridSet = namedGridsetCopy("TEST", gwc.getGridSetBroker().getDefaults().worldEpsg4326());
        gwc.addGridSet(testGridSet);

        int numGridSets = gwc.getGridSetBroker().getGridSets().size();

        Set<String> gridSetsToRemove = new HashSet<>();
        gridSetsToRemove.add("TEST");

        gwc.removeGridSets(gridSetsToRemove);

        assertEquals(numGridSets-1, gwc.getGridSetBroker().getGridSetNames().size());
    }

    @Test
    public void testRegisterS3BlobStore() {
        List<BlobStoreType<?>> blobStoreTypes = BlobStoreTypes.getAll();
        boolean foundS3BlobStore = false;
        for (BlobStoreType type : blobStoreTypes) {
            if (type.getConfigClass() == S3BlobStoreInfo.class) {
                foundS3BlobStore = true;
            }
        }
        assertTrue("Could not find S3 BlobStore among BlobStoreTypes: "+blobStoreTypes, foundS3BlobStore);
    }


}
