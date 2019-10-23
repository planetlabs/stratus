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

import static org.geoserver.data.test.CiteTestData.*;
import static org.junit.Assert.*;

/**
 * Integration test for {@link RestCachingInterceptor}
 *
 * Variation of {@link StratusRestCachingTest}, but with a (mostly) empty data directory, for testing the unique caching
 * failures that can occur in those conditions
 */
public class StratusRestCachingNullTest extends StratusApplicationTestSupport {

    @Override
    protected void setUpTestData(SystemTestData testData) throws Exception {
        testData.setUpSecurity();
        //add some default styles, but otherwise leave the test data empty
        testData.addStyle(DEFAULT_VECTOR_STYLE, catalog);
        testData.addStyle(DEFAULT_RASTER_STYLE, catalog);
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
    public void testGetLayers404() throws Exception {
        //All styles
        MockHttpServletResponse response = getAsServletResponse("rest/layers.xml");
        assertEquals(200, response.getStatus());
        assertEquals("application/xml", response.getContentType());

        //One style
        response = getAsServletResponse("rest/layers/"+BRIDGES.getPrefix()+":"+BRIDGES.getLocalPart()+".xml");
        assertEquals(404, response.getStatus());
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
        assertEquals(0, indexFacade.getLayerIndices().size());
    }
}
