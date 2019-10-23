/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.service.rest;

import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.config.SimpleImportResourcesConfig;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import org.custommonkey.xmlunit.XMLAssert;
import org.geoserver.config.GeoServer;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.rest.service.WMSSettingsControllerTest;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wms.WMSInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeoServerWithEmbeddedRedisConfig.class, SimpleImportResourcesConfig.class,
        RedisRepositoryImpl.class, RedisLayerIndexFacade.class, CacheProperties.class, RedisFacadeTestSupport.class,
        WMSInfoClassRegisteringBean.class, WMSConfig.class}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public class RedisWMSSettingsTest extends WMSSettingsControllerTest {

    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    @Override
    protected void onTearDown(SystemTestData testData) {
        redisTestSupport.repository.flush();
    }

    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        redisTestSupport.setCatalogFacade(GeoServerSystemTestSupport.applicationContext);
        redisTestSupport.setGeoServerFacade(GeoServerSystemTestSupport.applicationContext);
    }

    //Test for redis compatibility with srs getter/setter naming
    @Test
    public void testPutSrsList() throws Exception {
        GeoServer geoServer = this.getGeoServer();
        WMSInfo i = (WMSInfo)geoServer.getService(WMSInfo.class);
        i.setEnabled(true);
        geoServer.save(i);
        String xml =
                "<wms>" +
                    "<id>wms</id>" +
                    "<name>WMS</name>" +
                    "<title>GeoServer Web Map Service</title>" +
                    "<maintainer>http://geoserver.org/comm</maintainer>" +
                    "<srs>\n" +
                        "<string>4326</string>\n" +
                        "<string>3857</string>\n" +
                        "<string>900913</string>\n" +
                    "</srs>" +
                "</wms>";
        MockHttpServletResponse response = this.putAsServletResponse("/rest/services/wms/settings", xml, "text/xml");
        Assert.assertEquals(200L, (long)response.getStatus());
        Document dom = this.getAsDOM("/rest/services/wms/settings.xml");
        XMLAssert.assertXpathEvaluatesTo("true", "/wms/enabled", dom);
        XMLAssert.assertXpathEvaluatesTo("WMS", "/wms/name", dom);
        i = (WMSInfo)geoServer.getService(WMSInfo.class);
        Assert.assertTrue(i.isEnabled());
        Assert.assertEquals(3, i.getSRS().size());
        Assert.assertTrue(i.getSRS().contains("900913"));
    }

}
