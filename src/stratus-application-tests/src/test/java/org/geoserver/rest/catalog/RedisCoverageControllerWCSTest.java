/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.rest.catalog;

import stratus.config.StratusConfigProps;
import stratus.config.WebXmlConfig;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.CachingFilter;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.cache.rest.preloaders.CoveragePreloader;
import stratus.redis.cache.rest.preloaders.CoveragesPreloader;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.wcs.WCSConfig;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.WFSConfig;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import javax.servlet.Filter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        /* Catalog */
        RedisCatalogFacade.class, RedisGeoServerFacade.class, RedisLayerIndexFacade.class,
        CacheProperties.class, CatalogImpl.class, GWCWithEmbeddedRedisConfig.class, StratusConfigProps.class,
        RedisCatalogImportResourcesConfig.class, RedisRepositoryImpl.class, RedisConfigProps.class,
        /* OWS */
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        WMSConfig.class, WFSConfig.class, WCSConfig.class,
        /* GWC */
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class,
        /* Cache preloaders */
        WebXmlConfig.class, RestCachingInterceptor.class, CoveragePreloader.class, CoveragesPreloader.class},
        properties = {"stratus.catalog.redis.caching.enable-rest-caching=true", "spring.main.allow-bean-definition-overriding=true"})
public class RedisCoverageControllerWCSTest extends CoverageControllerWCSTest {
    @Autowired
    private RedisFacadeTestSupport redisTestSupport;

    @Autowired
    CachingFilter cachingFilter;

    @Autowired
    private RedisCatalogImportResourcesConfig importResourcesConfig;

    @Override
    protected void setUpSpring(List<String> springContextLocations) {
        importResourcesConfig.buildFilteredApplicationContextXmlResourceList(springContextLocations);
    }

    @Override
    protected void onTearDown(SystemTestData testData) {
        redisTestSupport.repository.flush();
    }

    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        redisTestSupport.repository.flush();
        redisTestSupport.setCatalogFacade(GeoServerSystemTestSupport.applicationContext);
        redisTestSupport.setGeoServerFacade(GeoServerSystemTestSupport.applicationContext);
    }

    @Override
    void addCoverageStore(boolean autoConfigureCoverage) throws Exception {
        URL zip = RedisCoverageTest.class.getResource("test-data/usa.zip");
        //byte[] bytes = FileUtils.readFileToByteArray(DataUtilities.urlToFile(zip));
        byte[] bytes = IOUtils.toByteArray(zip.openStream());
        MockHttpServletResponse response = this.putAsServletResponse("/rest/workspaces/gs/coveragestores/usaWorldImage/file.worldimage" + (!autoConfigureCoverage ? "?configure=none" : ""), bytes, "application/zip");
        assertEquals(201L, (long)response.getStatus());
    }

    @Override
    protected List<Filter> getFilters() {
        return Collections.singletonList(cachingFilter);
    }

    /*
* Off-by-one error:
* assertXpathEvaluatesTo("983 598", "/coverage/grid/range/high", dom); ->
* assertXpathEvaluatesTo("984 599", "/coverage/grid/range/high", dom);
*/
    @Override
    public void testPostAsXML() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:usa" +
                "&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" +
                "&gridbasecrs=EPSG:4326&store=true";

        Document dom = getAsDOM( req );
        assertEquals( "ows:ExceptionReport", dom.getDocumentElement().getNodeName());

        addCoverageStore(false);
        dom = getAsDOM( "/rest/workspaces/gs/coveragestores/usaWorldImage/coverages.xml");
        assertEquals( 0, dom.getElementsByTagName( "coverage").getLength() );

        String xml =
                "<coverage>" +
                        "<name>usa</name>"+
                        "<title>usa is a A raster file accompanied by a spatial data file</title>" +
                        "<description>Generated from WorldImage</description>" +
                        "<srs>EPSG:4326</srs>" +
                /*"<latLonBoundingBox>"+
                  "<minx>-130.85168</minx>"+
                  "<maxx>-62.0054</maxx>"+
                  "<miny>20.7052</miny>"+
                  "<maxy>54.1141</maxy>"+
                "</latLonBoundingBox>"+
                "<nativeBoundingBox>"+
                  "<minx>-130.85168</minx>"+
                  "<maxx>-62.0054</maxx>"+
                  "<miny>20.7052</miny>"+
                  "<maxy>54.1141</maxy>"+
                  "<crs>EPSG:4326</crs>"+
                "</nativeBoundingBox>"+
                "<grid dimension=\"2\">"+
                    "<range>"+
                      "<low>0 0</low>"+
                      "<high>983 598</high>"+
                    "</range>"+
                    "<transform>"+
                      "<scaleX>0.07003690742624616</scaleX>"+
                      "<scaleY>-0.05586772575250837</scaleY>"+
                      "<shearX>0.0</shearX>"+
                      "<shearX>0.0</shearX>"+
                      "<translateX>-130.81666154628687</translateX>"+
                      "<translateY>54.08616613712375</translateY>"+
                    "</transform>"+
                    "<crs>EPSG:4326</crs>"+
                "</grid>"+*/
                        "<supportedFormats>"+
                        "<string>PNG</string>"+
                        "<string>GEOTIFF</string>"+
                        "</supportedFormats>"+
                        "<requestSRS>"+
                        "<string>EPSG:4326</string>"+
                        "</requestSRS>"+
                        "<responseSRS>"+
                        "<string>EPSG:4326</string>"+
                        "</responseSRS>"+
                        "<store>usaWorldImage</store>"+
                        "<namespace>gs</namespace>"+
                        "</coverage>";
        MockHttpServletResponse response =
                postAsServletResponse( "/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/", xml, "text/xml");

        assertEquals( 201, response.getStatus() );
        assertNotNull( response.getHeader( "Location") );
        assertTrue( response.getHeader("Location").endsWith( "/workspaces/gs/coveragestores/usaWorldImage/coverages/usa" ) );

        dom = getAsDOM( req );
        assertEquals( "wcs:Coverages", dom.getDocumentElement().getNodeName() );

        dom = getAsDOM("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/usa.xml");
        assertXpathEvaluatesTo("-130.85168", "/coverage/latLonBoundingBox/minx", dom);

        // TODO this value fluctuates between "984 599", "983 598", and "982 587" -- need to test possible fluctuations?
        String highRange = getNodeValue("/coverage/grid/range/high", dom);
        Assert.assertThat(highRange, CoreMatchers.anyOf(CoreMatchers.is("984 599"), CoreMatchers.is("983 598"), CoreMatchers.is("982 597")));
        //assertXpathEvaluatesTo("984 599", "/coverage/grid/range/high", dom);

    }

    @Override
    public void testPostAsXMLWithNativeName() throws Exception {
        removeStore("gs", "usaWorldImage");
        String req = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:differentName" +
                "&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff" +
                "&gridbasecrs=EPSG:4326&store=true";

        Document dom = getAsDOM( req );
        assertEquals( "ows:ExceptionReport", dom.getDocumentElement().getNodeName());

        addCoverageStore(false);
        dom = getAsDOM( "/rest/workspaces/gs/coveragestores/usaWorldImage/coverages.xml");
        assertEquals( 0, dom.getElementsByTagName( "coverage").getLength() );

        String xml =
                "<coverage>" +
                        "<name>differentName</name>"+
                        "<title>usa is a A raster file accompanied by a spatial data file</title>" +
                        "<description>Generated from WorldImage</description>" +
                        "<srs>EPSG:4326</srs>" +
                        "<supportedFormats>"+
                        "<string>PNG</string>"+
                        "<string>GEOTIFF</string>"+
                        "</supportedFormats>"+
                        "<requestSRS>"+
                        "<string>EPSG:4326</string>"+
                        "</requestSRS>"+
                        "<responseSRS>"+
                        "<string>EPSG:4326</string>"+
                        "</responseSRS>"+
                        "<store>usaWorldImage</store>"+
                        "<namespace>gs</namespace>"+
                        "<nativeCoverageName>usa</nativeCoverageName>"+
                        "</coverage>";
        MockHttpServletResponse response =
                postAsServletResponse( "/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/", xml, "text/xml");

        assertEquals( 201, response.getStatus() );
        assertNotNull( response.getHeader( "Location") );
        assertTrue( response.getHeader("Location").endsWith( "/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName" ) );

        dom = getAsDOM( req );
        assertEquals( "wcs:Coverages", dom.getDocumentElement().getNodeName() );

        dom = getAsDOM("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/differentName.xml");
        assertXpathEvaluatesTo("-130.85168", "/coverage/latLonBoundingBox/minx", dom);
        // TODO this value fluctuates between "984 599", "983 598", and "982 587" -- need to test possible fluctuations?
        String highRange = getNodeValue("/coverage/grid/range/high", dom);
        Assert.assertThat(highRange, CoreMatchers.anyOf(CoreMatchers.is("984 599"), CoreMatchers.is("983 598"), CoreMatchers.is("982 597")));
        //assertXpathEvaluatesTo("984 599", "/coverage/grid/range/high", dom);
    }

    private String getNodeValue(String xpath, Document dom) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xp = xpf.newXPath();
        return xp.evaluate(xpath, dom.getDocumentElement());
    }

    @Test
    @Override
    public void testPostAsJSON() throws Exception {
        this.removeStore("gs", "usaWorldImage");
        String request = "wcs?service=wcs&request=getcoverage&version=1.1.1&identifier=gs:usa&boundingbox=-100,30,-80,44,EPSG:4326&format=image/tiff&gridbasecrs=EPSG:4326&store=true";
        Document document = this.getAsDOM(request);
        Assert.assertEquals("ows:ExceptionReport", document.getDocumentElement().getNodeName());
        this.addCoverageStore(false);
        JSONObject json = (JSONObject)this.getAsJSON("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages.json");
        Assert.assertThat(json.getString("coverages").isEmpty(), CoreMatchers.is(true));
        String content = "{    \"coverage\": {        \"description\": \"Generated from WorldImage\",        \"name\": \"usa\",        \"namespace\": \"gs\",        \"requestSRS\": {            \"string\": [                \"EPSG:4326\"            ]        },        \"responseSRS\": {            \"string\": [                \"EPSG:4326\"            ]        },        \"srs\": \"EPSG:4326\",        \"store\": \"usaWorldImage\",        \"supportedFormats\": {            \"string\": [                \"PNG\",                \"GEOTIFF\"            ]        },        \"title\": \"usa is a A raster file accompanied by a spatial data file\"    }}";
        MockHttpServletResponse response = this.postAsServletResponse("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/", content, "application/json");
        Assert.assertEquals(201L, (long)response.getStatus());
        Assert.assertNotNull(response.getHeader("Location"));
        Assert.assertTrue(response.getHeader("Location").endsWith("/workspaces/gs/coveragestores/usaWorldImage/coverages/usa"));
        document = this.getAsDOM(request);
        Assert.assertEquals("wcs:Coverages", document.getDocumentElement().getNodeName());
        json = (JSONObject)this.getAsJSON("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages/usa.json");
        Assert.assertThat(json.getJSONObject("coverage").getString("name"), CoreMatchers.is("usa"));
        Assert.assertThat(json.getJSONObject("coverage").getJSONObject("latLonBoundingBox").getString("minx"), CoreMatchers.is("-130.85168"));
        Assert.assertThat(json.getJSONObject("coverage").getJSONObject("grid").getJSONObject("range").getString("high"),
                CoreMatchers.anyOf(CoreMatchers.is("984 599"), CoreMatchers.is("983 598"), CoreMatchers.is("982 597")));
        json = (JSONObject)this.getAsJSON("/rest/workspaces/gs/coveragestores/usaWorldImage/coverages.json");
        JSONArray coverages = json.getJSONObject("coverages").getJSONArray("coverage");
        Assert.assertThat(coverages.size(), CoreMatchers.is(1));
        Assert.assertThat(coverages.getJSONObject(0).getString("name"), CoreMatchers.is("usa"));


    }
}
