/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.data.test.SystemTestData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import stratus.StratusApplicationTestSupport;
import stratus.wps.config.WPSConfigurationProperties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

@Slf4j
public class WPSTest extends StratusApplicationTestSupport {

    /*
    The following environment variables are required to run this test:
    file-storage: s3 #anything other than "s3" here defaults to local file storage, which may not work well for multi-instance deployments
    s3-region: US_EAST_1 #Underscores or dashes as the separator here are acceptable
    s3-bucket: stratus-wps #This bucket will be created if it doesn't already exist
    access-key: #optional if there is an Amazon credential chain external to Stratus
    secret-key: #optional if there is an Amazon credential chain external to Stratus
     */

    @Override
    public void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);
        testData.setUpDefaultRasterLayers();

    }

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    WPSConfigurationProperties wpsConfigurationProperties;

    @Before
    public void checkAWS(){
        try {
            assumeTrue(amazonS3.listObjects(wpsConfigurationProperties.getS3Bucket()).getBucketName().equals(wpsConfigurationProperties.getS3Bucket()));
        } catch (AmazonS3Exception e) {
            log.info("Failure connecting to Amazon S3 - Skipping WPSTest. Have you configured your AWS Keys?");
            assumeNoException(e);
        }
    }
    @Autowired
    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    @Before
    public void mockRestSetUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testVectorWPSCall() throws Exception {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("wps_get_feature_count.xml").toURI());  //this file is in src/test/resources

        StringBuilder data = new StringBuilder();
        Stream<String> lines = Files.lines(path);
        lines.forEach(line -> data.append(line).append("\n"));
        lines.close();
        String wpsRequest = data.toString();


        //WPS request base
        String requestBase = "ows?service=wps&version=1.0.0&request=Execute";

        //Get response
        MockHttpServletResponse response = postAsServletResponse(requestBase,wpsRequest,"xml");

        assertEquals(200, response.getStatus());
        assertEquals(true,response.getContentAsString().contains("statusLocation"));

        Document doc = loadXMLFromString(response.getContentAsString());

        String downloadurl = doc.getDocumentElement().getAttribute("statusLocation");
        MockHttpServletResponse downloadResponse = getAsServletResponse(downloadurl.substring(downloadurl.indexOf("ows")));
        assertEquals(true,downloadResponse.getContentAsString().contains("vec:Count"));



    }

    @Test
    public void testRasterCall() throws Exception {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("wps_crop_world_raster.xml").toURI());  //this file is in src/test/resources

        StringBuilder data = new StringBuilder();
        Stream<String> lines = Files.lines(path);
        lines.forEach(line -> data.append(line).append("\n"));
        lines.close();
        String wpsRequest = data.toString();


        //WPS request base
        String requestBase = "ows?service=wps&version=1.0.0&request=Execute";

        //Get response
        MockHttpServletResponse response = postAsServletResponse(requestBase,wpsRequest,"xml");

        assertEquals(200, response.getStatus());
        assertEquals(true,response.getContentAsString().contains("ProcessSucceeded"));
    }

    public Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}