/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles connecting to S3 and fetching parts
 */
public class S3Connector {
    private final static Logger LOGGER = Logger
            .getLogger(S3Connector.class.getName());

    private String regionString;
    private boolean useAnon = false;
    private String accessKey;
    private String secretKey;

    public S3Connector(String regionString, boolean useAnon, String accessKey, String secretKey) {
        this.regionString = regionString;
        this.useAnon = useAnon;
        this.accessKey=accessKey;
        this.secretKey=secretKey;
    }

    /**
     * Create an S3 connector from a URI-ish S3:// string. Notably, this constructor supports awsRegion and useAnon
     * as query parameters to control these settings.
     *
     * Also of note, this URL is largely ignored outside of the query parameters. Mainly this is used to control
     * authentication options
     *
     * @param input an s3:// style URL.
     */
    S3Connector(String input) {
        //Parse region and anon from URL
        try {
            URI s3Uri = new URI(input);
            List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(s3Uri, Charset.forName("UTF-8"));

            for (NameValuePair nvPair : nameValuePairs) {
                if ("awsRegion".equals(nvPair.getName())) {
                    this.regionString = nvPair.getValue();
                }

                if ("useAnon".equals(nvPair.getName())) {
                    this.useAnon = Boolean.parseBoolean(nvPair.getValue());
                }
            }

        } catch (URISyntaxException e) {
            LOGGER.finer("Error parsing S3 URL in order to parse query options");
        }
    }


    public AmazonS3 getS3Client() {
        Regions region;
        if (this.regionString != null) {
            try {
                regionString.replaceAll("-","_");
                region = Regions.valueOf(regionString);
            } catch (IllegalArgumentException e) {
                //probably not great to have a default, but we can't just blow up if this
                //property isn't set
                LOGGER.warning("AWS_REGION property is set, but not set correctly. "
                        + "Check that the AWS_REGION property matches the Regions enum");
                region = Regions.US_EAST_1;
            }
        } else {
            LOGGER.warning("No AWS_REGION property set, defaulting to US_EAST_1");
            region = Regions.US_EAST_1;
        }

        AmazonS3 s3;
        if (useAnon) {
            final AWSCredentialsProvider provider = new AWSCredentialsProvider() {
                private final AnonymousAWSCredentials credentials = new AnonymousAWSCredentials();
                @Override
                public AWSCredentials getCredentials() {
                    return credentials;
                }

                @Override
                public void refresh() {
                }
            };
            s3=AmazonS3ClientBuilder.standard()
                    .withCredentials(provider)
                    .withRegion(region)
                    .build();

        } else {
            if(accessKey!=null&&accessKey.length()>0&&secretKey!=null&&secretKey.length()>0) {
                try {
                    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
                    s3 = AmazonS3ClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                            .withRegion(region)
                            .build();
                }catch(SdkClientException se){
                    throw new RuntimeException("The provided Amazon credentials did not successfully generate an S3 connection.",se);
                }
            }else {
                try {
                    s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
                }catch (SdkClientException se){
                    throw new RuntimeException("No Amazon credentials configured and connection failed when attempting to use credential chain.",se);

                }
            }

        }


        return s3;
    }

    /**
     *
     * @param s3Path the s3:// url style path
     * @return bucket and key parts of the given S3 path, IN THAT ORDER
     */
    public static String[] getS3PathParts(String s3Path) {
        String[] parts = s3Path.split("/");
        if(parts==null||parts.length<2)
            return null;
        StringBuilder keyBuilder = new StringBuilder();

        String bucket = parts[2];
        for (int i=3; i < parts.length; i++ ) {
            keyBuilder.append("/").append(parts[i]);
        }
        String key = keyBuilder.toString();
        key = key.startsWith("/") ? key.substring(1) : key;

        return new String[] { bucket, key };
    }

    /**
     *
     * @param bucketName
     * @param key
     * @return A s3:// url style path
     */
    public static String createS3URL(String bucketName, String key){
        if(bucketName!=null&&key!=null){
            return "s3://"+bucketName+"/"+key;
        }else{
            throw new NullPointerException("Bucket and Key is required to build a S3 URL");
        }
    }

}
