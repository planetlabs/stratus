/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import stratus.StratusApplicationTestSupport;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.CachingFilter;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.index.RedisLayerIndexFacade;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.LayerGroupInfoImpl;
import org.geoserver.data.test.MockData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerExtensions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.Filter;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration test for OWS cache preloading
 *
 * @see OWSCachingCallback
 */
public class StratusOwsCachingTest extends StratusApplicationTestSupport {

    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        testData.setUpDefaultRasterLayers();
    }

    @Test
    public void assertApplicationState() {
        //Catalog Caching Filter
        assertNotNull(GeoServerExtensions.bean(CachingFilter.class));
        //LayerIndexListener
        boolean hasLayerIndexListener = false;
        for (Object listener : catalogImpl.getListeners()) {
            if (listener instanceof LayerIndexListener) {
                hasLayerIndexListener = true;
            }
        }
        assertTrue(hasLayerIndexListener);
        //CachingCatalog
        assertNotNull(CachingCatalogFacade.unwrapCatalog(catalog));
        //OWSCachingCallback
        assertNotNull(GeoServerExtensions.bean(OWSCachingCallback.class));
        //Caching Handlers - WMS, WFS, WCS
        assertEquals(3, GeoServerExtensions.extensions(OWSCachingHandler.class).size());
        //Layer index
        RedisLayerIndexFacade indexFacade = GeoServerExtensions.bean(RedisLayerIndexFacade.class);
        assertNotNull(indexFacade);
        assertTrue(indexFacade.getLayerIndices().size() > 0);
    }

    @Test
    public void testVirtualServicesGetMap() throws Exception {
        LayerInfo layer = catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart());

        String name = layer.getName();
        String prefix = layer.getResource().getNamespace().getPrefix();
        String prefixedName = layer.prefixedName();

        String requestBase = "wms?bbox=-1.5,-0.5,1.5,1.5"
                + "&format=image%2Fpng&request=GetMap"
                + "&width=300&height=300&srs=EPSG:4326&layers=";

        //Regular wms request
        MockHttpServletResponse response = getAsServletResponse(requestBase + layer.prefixedName());
        assertEquals("image/png", response.getContentType());

        //Virtual wms request with qualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + prefixedName);
        assertEquals("image/png", response.getContentType());

        //Virtual wms request with unqualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + name);
        assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testVirtualServicesGetLegendGraphic() throws Exception {
        LayerInfo layer = catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart());

        String name = layer.getName();
        String prefix = layer.getResource().getNamespace().getPrefix();
        String prefixedName = layer.prefixedName();

        String requestBase = "wms?format=image%2Fpng&request=GetLegendGraphic&layer=";

        //Regular wms request
        MockHttpServletResponse response = getAsServletResponse(requestBase + layer.prefixedName());
        assertEquals("image/png", response.getContentType());

        //Virtual wms request with qualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + prefixedName);
        assertEquals("image/png", response.getContentType());

        //Virtual wms request with unqualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + name);
        assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testVirtualServicesGetFeature() throws Exception {
        LayerInfo layer = catalog.getLayerByName(MockData.FIFTEEN.getPrefix()+":"+MockData.FIFTEEN.getLocalPart());

        String name = layer.getName();
        String prefix = layer.getResource().getNamespace().getPrefix();
        String prefixedName = layer.prefixedName();

        String requestBase = "wfs?request=GetFeature&version=2.0.0&service=wfs&typenames=";

        //Regular wms request
        MockHttpServletResponse response = getAsServletResponse(requestBase + layer.prefixedName());
        assertEquals("application/gml+xml; version=3.2", response.getContentType());
        assertTrue(response.getContentAsString().contains("gml:id=\"Fifteen.0\""));

        //Virtual wms request with qualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + prefixedName);
        assertEquals("application/gml+xml; version=3.2", response.getContentType());
        assertTrue(response.getContentAsString().contains("gml:id=\"Fifteen.0\""));

        //Virtual wms request with unqualified layer name
        response = getAsServletResponse(prefix + "/" + requestBase + name);
        assertEquals("application/gml+xml; version=3.2", response.getContentType());
        assertTrue(response.getContentAsString().contains("gml:id=\"Fifteen.0\""));
    }

    @Test
    public void testNestedLayerGroups() throws Exception {

        WorkspaceInfo citeWs = catalog.getWorkspaceByName(MockData.CITE_PREFIX);

        LayerGroupInfo outer = new LayerGroupInfoImpl();
        LayerGroupInfo inner = new LayerGroupInfoImpl();

        outer.setName("alg");
        outer.setWorkspace(citeWs);

        outer.getLayers().add(catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart()));
        outer.getStyles().add(null);
        outer.getLayers().add(inner);
        outer.getStyles().add(null);

        inner.setName("zlg");
        inner.setWorkspace(citeWs);
        inner.getLayers().add(catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart()));
        inner.getStyles().add(null);

        catalogImpl.add(inner);
        catalogImpl.add(outer);

        String requestBase = "wms?bbox=-1.5,-0.5,1.5,1.5"
                + "&format=image%2Fpng&request=GetMap"
                + "&width=300&height=300&srs=EPSG:4326&layers=cite:alg";

        //Regular wms request
        MockHttpServletResponse response = getAsServletResponse(requestBase);
        assertEquals("image/png", response.getContentType());
    }

    //testNestedLayerGroups with order of catalog add reversed, to catch weird catalog behaviors
    @Test
    public void testNestedLayerGroupsReverse() throws Exception {

        WorkspaceInfo citeWs = catalog.getWorkspaceByName(MockData.CITE_PREFIX);

        LayerGroupInfo outer = new LayerGroupInfoImpl();
        LayerGroupInfo inner = new LayerGroupInfoImpl();

        outer.setName("alg");
        outer.setWorkspace(citeWs);

        outer.getLayers().add(catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart()));
        outer.getStyles().add(null);


        inner.setName("zlg");
        inner.setWorkspace(citeWs);
        inner.getLayers().add(catalog.getLayerByName(MockData.BASIC_POLYGONS.getPrefix()+":"+MockData.BASIC_POLYGONS.getLocalPart()));
        inner.getStyles().add(null);


        catalogImpl.add(outer);
        catalogImpl.add(inner);

        outer = catalogImpl.getLayerGroup(outer.getId());

        outer.getLayers().add(inner);
        outer.getStyles().add(null);

        catalogImpl.save(outer);

        String requestBase = "wms?bbox=-1.5,-0.5,1.5,1.5"
                + "&format=image%2Fpng&request=GetMap"
                + "&width=300&height=300&srs=EPSG:4326&layers=cite:alg";

        //Regular wms request
        MockHttpServletResponse response = getAsServletResponse(requestBase);
        assertEquals("image/png", response.getContentType());
    }

    @Test
    public void testCachingDoesntSwallowExceptions() throws Exception {
        //Malformed WCS request
        String wcsRequest =
                "<wcs:GetCoverage service=\"WCS\" version=\"1.1.1\">\n" +
                "  <wcs:Identifier>wcs:DEM</wcs:Identifier>\n" +
                "  <wcs:DomainSubset>\n" +
                "    <ows:WGS84BoundingBox>\n" +
                "\t<ows:LowerCorner>-180.01666666666665 -89.98333333331534</ows:LowerCorner>\n" +
                "\t<ows:UpperCorner>179.98333333329737 90.01666666666667</ows:UpperCorner>\n" +
                "\t</ows:WGS84BoundingBox>\n" +
                "  </wcs:DomainSubset>\n" +
                "  <wcs:Output format=\"image/tiff\"/>\n" +
                "</wcs:GetCoverage>";

        MockHttpServletResponse response = postAsServletResponse("wcs", wcsRequest, "application/xml");

        assertEquals(200, response.getStatus());
        String responseContent = response.getContentAsString();
        assertTrue(responseContent.contains("ows:Exception"));
        assertFalse(responseContent.contains("java.io.EOFException")); //This occurred when cache preloading crashed and didn't reset the inputstream properly
        assertTrue(responseContent.contains("org.xmlpull.v1.XmlPullParserException")); //This is the expected failure
    }

    protected List<Filter> getFilters() {
        return Collections.singletonList(GeoServerExtensions.bean(CachingFilter.class));
    }
}
