/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest;

import stratus.StratusApplicationTestSupport;
import stratus.redis.cache.CachingCatalogFacade;
import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.index.RedisLayerIndexFacade;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.platform.GeoServerExtensions;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.geoserver.data.test.CiteTestData.BRIDGES;
import static org.geoserver.data.test.CiteTestData.DEFAULT_VECTOR_STYLE;
import static org.junit.Assert.*;

/**
 * Integration test for {@link RestCachingInterceptor}
 */
public class StratusRestCachingTest extends StratusApplicationTestSupport {
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
        //RestCachingInterceptor
        assertNotNull(GeoServerExtensions.bean(RestCachingInterceptor.class));
        //Layer index
        RedisLayerIndexFacade indexFacade = GeoServerExtensions.bean(RedisLayerIndexFacade.class);
        assertNotNull(indexFacade);
        assertTrue(indexFacade.getLayerIndices().size() > 0);
    }

    @Test
    public void testGetStyles() throws Exception {
        //All styles
        MockHttpServletResponse response = getAsServletResponse("rest/styles.xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());

        //One style
        response = getAsServletResponse("rest/styles/"+DEFAULT_VECTOR_STYLE+".xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());
    }

    @Test
    public void testGetLayers() throws Exception {
        //All layers
        MockHttpServletResponse response = getAsServletResponse("rest/layers.xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());

        //One layer
        response = getAsServletResponse("rest/layers/"+BRIDGES.getPrefix()+":"+BRIDGES.getLocalPart()+".xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());
    }

    @Test
    public void testGetFeatureTypes() throws Exception {
        //All feature types
        MockHttpServletResponse response = getAsServletResponse("rest/workspaces/"+BRIDGES.getPrefix()+"/featuretypes.xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());

        //One feature type
        response = getAsServletResponse("rest/workspaces/"+BRIDGES.getPrefix()+"/featuretypes/"+BRIDGES.getLocalPart()+".xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());
    }
}
