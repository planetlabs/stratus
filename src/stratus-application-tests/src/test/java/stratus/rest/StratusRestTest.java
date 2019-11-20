/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.geoserver.data.test.SystemTestData;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import stratus.StratusApplicationTestSupport;

import static org.geoserver.data.test.CiteTestData.CITE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * General rest integration test
 *
 * See also: {@link StratusRestCachingTest}, {@link StratusRestCachingNullTest}
 */
public class StratusRestTest extends StratusApplicationTestSupport {
    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        testData.setUpDefaultRasterLayers();
    }

    @Test
    public void testWorkspaceJSON() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("rest/workspaces/"+CITE_PREFIX+".json");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        JSON json = JSONSerializer.toJSON(response.getContentAsString());
        assertTrue(json instanceof JSONObject);
        assertEquals(CITE_PREFIX, ((JSONObject) json).getJSONObject("workspace").getString("name"));
    }

    @Test
    public void testWorkspacesJSON() throws Exception {
        MockHttpServletResponse response = getAsServletResponse("rest/workspaces.json");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());

        JSON json = JSONSerializer.toJSON(response.getContentAsString());
        assertTrue(json instanceof JSONObject);
        JSONArray array = ((JSONObject) json).getJSONObject("workspaces").getJSONArray("workspace");
        assertEquals(6, array.size());
    }
}
