/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.LocalWorkspace;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import stratus.StratusApplicationTestSupport;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests integration between {@link org.geoserver.ows.Dispatcher} and {@link stratus.controller.OwsController}
 */
public class OWSIntegrationTest extends StratusApplicationTestSupport {

    @Test
    public void testInvalidVirtualService() throws Exception {
        //try a known virtual service
        String requestBase = "wms?request=GetCapabilities";

        //Regular wms virtual service requests
        MockHttpServletResponse response = getAsServletResponse("cite/wms?request=GetCapabilities");
        assertEquals(200, response.getStatus());
        assertEquals("text/xml", response.getContentType());

        response = getAsServletResponse("cite/Bridges/wms?request=GetCapabilities");
        assertEquals(200, response.getStatus());
        assertEquals("text/xml", response.getContentType());

        //Invalid wms virtual service requests
        response = getAsServletResponse("foo/wms?request=GetCapabilities");
        assertEquals(404, response.getStatus());

        response = getAsServletResponse("cite/bar/wms?request=GetCapabilities");
        assertEquals(404, response.getStatus());


    }

    @Test
    public void testWmtsService() throws Exception {
        // getting the capabilities document
        MockHttpServletResponse response = getAsServletResponse("/gwc/service/wmts?request=GetCapabilities");
        // check that the request was successful
        assertThat(response.getStatus(), is(200));

        // test virtual workspace
        WorkspaceInfo workspace = catalog.getWorkspaceByName("cite");
        assertThat(workspace, notNullValue());
        LocalWorkspace.set(workspace);
        try {
            response = getAsServletResponse("cite/gwc/service/wmts?request=GetCapabilities");
            assertThat(response.getStatus(), is(200));
        } finally {
            // make sure we remove the local workspace
            LocalWorkspace.set(null);
        }

        // invalid virtual service
        response = getAsServletResponse("foo/gwc/service/wmts?request=GetCapabilities");
        assertThat(response.getStatus(), is(404));
    }
}
