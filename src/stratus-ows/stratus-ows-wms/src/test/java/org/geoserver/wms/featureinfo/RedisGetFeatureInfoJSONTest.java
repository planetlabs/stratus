/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.featureinfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.wfs.json.JSONType;
import org.geotools.util.factory.Hints;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.ows.OWSCachingCallback;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.LayerIndexListener;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.wms.StratusWmsTestSupport;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import java.util.TimeZone;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, RedisRepositoryImpl.class,
        WMSConfig.class, RedisLayerIndexFacade.class, LayerIndexListener.class,
        CacheProperties.class, OWSCachingCallback.class, StratusWmsTestSupport.class,
        SimpleImportResourcesConfig.class, WMSInfoClassRegisteringBean.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisGetFeatureInfoJSONTest extends GetFeatureInfoJSONTest {

    @Autowired
    private StratusWmsTestSupport testSupport;

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        testSupport.configureWmsTestPreSetup(applicationContext, getCatalog());
        super.onSetUp(testData);
        testSupport.configureWmsTestPostSetup(applicationContext, getCatalog());
    }

    // Copy of the GeoServer test that explicitly sets "org.geotools.dateTimeFormatHandling" to "true" rather than
    // removing the property to fix errors caused by test execution order.
    @Override
    public void testDateTimeFormattingEnabled() throws Exception {
        TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-05:00"));
        try {
            //Force org.geotools.dateTimeFormatHandling to true
            System.setProperty("org.geotools.dateTimeFormatHandling", "true");
            System.setProperty("org.geotools.localDateTimeHandling", "true");
            Hints.scanSystemProperties();
            String layer = getLayerId(TEMPORAL_DATA);
            String request =
                    "wms?version=1.1.1&bbox=39.73245,2.00342,39.732451,2.003421&styles=&format=jpeg"
                            + "&request=GetFeatureInfo&layers="
                            + layer
                            + "&query_layers="
                            + layer
                            + "&width=10&height=10&x=5&y=5"
                            + "&info_format="
                            + JSONType.json;

            // JSON
            MockHttpServletResponse response = getAsServletResponse(request, "");

            // MimeType
            assertEquals(JSONType.json, response.getContentType());

            // Check if the character encoding is the one expected
            assertTrue("UTF-8".equals(response.getCharacterEncoding()));

            // Content
            String result = response.getContentAsString();
            assertNotNull(result);

            JSONObject rootObject = JSONObject.fromObject(result);
            assertEquals(rootObject.get("type"), "FeatureCollection");
            JSONArray featureCol = rootObject.getJSONArray("features");
            JSONObject aFeature = featureCol.getJSONObject(0);
            JSONObject properties = aFeature.getJSONObject("properties");
            assertNotNull(properties);
            assertEquals("2006-06-27T22:00:00-05:00", properties.getString("dateTimeProperty"));
            assertEquals("2006-12-12", properties.getString("dateProperty"));
        } finally {
            TimeZone.setDefault(defaultTimeZone);
            System.getProperties().remove("org.geotools.localDateTimeHandling");
        }
    }

}
