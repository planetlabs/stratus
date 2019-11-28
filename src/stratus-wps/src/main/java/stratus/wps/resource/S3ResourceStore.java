/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.resource;

import alex.mojaki.s3upload.MultiPartOutputStream;
import alex.mojaki.s3upload.StreamTransferManager;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.platform.resource.*;
import stratus.wps.s3.S3Connector;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
/**
 * Amazon S3 specific ResourceStore with methods to put, move, remove and retrieve objects. Also
 * includes methods to get input and output filestreams.
 */
public class S3ResourceStore implements ResourceStore {

    private final AmazonS3 amazonS3;

    private final String keyPrefix;

    private final String bucketName;

    public S3ResourceStore(AmazonS3 amazonS3, String bucketName, String keyPrefix) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        if (keyPrefix != null && !keyPrefix.endsWith("/")) keyPrefix = keyPrefix + "/";
        this.keyPrefix = keyPrefix != null ? keyPrefix : "";
    }

    protected ResourceNotificationDispatcher watcher;
    /** LockProvider used to secure resources for exclusive access */
    protected LockProvider lockProvider = new NullLockProvider();

    @Override
    public Resource get(String path) {
        try {
            return new S3Resource(bucketName, keyPrefix, path);
        } catch (SdkClientException sce) {
            log.error("Failed to retrieve object at path:" + path, sce);
            return null;
        } catch (NullPointerException npe) {
            log.error("Failed to get object due to missing bucket name or key:" + path, npe);
            return null;
        }
    }

    @Override
    public boolean remove(String path) {
        try {
            String[] pathParts = S3Connector.getS3PathParts(path);
            amazonS3.deleteObject(pathParts[0], keyPrefix + pathParts[1]);
        } catch (SdkClientException sce) {
            log.error("Failed to remove object at path:" + path, sce);
            return false;
        } catch (NullPointerException npe) {
            log.error("Failed to remove object due to missing bucket name or key:" + path, npe);
            return false;
        }
        return true;
    }

    @Override
    public boolean move(String path, String target) {
        try {
            String[] pathParts = S3Connector.getS3PathParts(path);
            String[] targetParts = S3Connector.getS3PathParts(target);
            amazonS3.copyObject(
                    pathParts[0],
                    keyPrefix + pathParts[1],
                    targetParts[0],
                    keyPrefix + targetParts[1]);
            amazonS3.deleteObject(pathParts[0], keyPrefix + pathParts[1]);
        } catch (SdkClientException sce) {
            log.error("Failed to move object at path:" + path + " to target: " + target, sce);
            return false;
        } catch (NullPointerException npe) {
            log.error(
                    "Failed to move object due to missing bucket name or key:"
                            + path
                            + " to target: "
                            + target,
                    npe);
            return false;
        }

        return true;
    }

    @Override
    public ResourceNotificationDispatcher getResourceNotificationDispatcher() {
        if (watcher == null) {
            watcher = new S3Watcher(amazonS3);
        }
        return watcher;
    }

    public class S3Resource implements Resource, Closeable {

        public String keyPrefix;                // path to the resource, with a trailing slash
        public String bucketName;               // name of the S3 bucket
        public String key;                      // name of the resource - name of the key in s3 is keyPrefix + key
        private boolean objectExists = false;   // does the object exist in S3? If not, assume directory
        private S3Object s3Object = null;       // active connection to S3, if applicable

        public S3Resource(String bucketName, String keyPrefix, String key) {
            this.bucketName = bucketName;
            this.keyPrefix = keyPrefix != null ? keyPrefix : "";
            this.key = key;
            if (bucketName == null) {
                throw new NullPointerException("Bucket is required");
            }
            try {
                if (this.key != null && !this.key.equals("")) {
                    this.objectExists = amazonS3.doesObjectExist(this.bucketName, this.keyPrefix + this.key);
                }
            } catch (SdkClientException sce) {
                log.info(
                        "Unable to retrieve object from s3 with bucket: "
                                + bucketName
                                + " and key: "
                                + keyPrefix
                                + key);
                log.debug(sce.getMessage() + " " + sce, sce);
            }
        }

        /**
         * Fetches the S3Object from Amazon if it exists, otherwise throws an exception.
         * Calling methods should invoke {@link #close()} when they are done with the object
         * @return the S3Object if it exists
         * @throws {@link IllegalStateException} if the S3Object could not be retrieved
         */
        private S3Object s3Object() {
            if (s3Object == null) {
                if (this.key != null && !this.key.equals("")) {
                    int count = 0;
                    int maxTries = 10;
                    while (true) {
                        try {
                            this.s3Object =
                                    amazonS3.getObject(this.bucketName, this.keyPrefix + this.key);
                            if (this.s3Object != null) {
                                log.info(
                                        "Retrieved file "
                                                + bucketName
                                                + " key: "
                                                + keyPrefix
                                                + key
                                                + " from S3 on attempt number "
                                                + String.valueOf(count + 1));
                                return s3Object;
                            }
                        } catch (SdkClientException e) {
                            if (++count == maxTries) { // stop trying after 3 tries/30 seconds
                                throw new IllegalStateException(
                                        "File not found in bucket after "
                                                + String.valueOf(maxTries)
                                                + " tries: "
                                                + bucketName
                                                + " key: "
                                                + keyPrefix
                                                + key);
                            }
                            try {
                                log.info(
                                        "Failed to retrieve file "
                                                + bucketName
                                                + " key: "
                                                + keyPrefix
                                                + key
                                                + " from S3 on attempt number "
                                                + String.valueOf(count)
                                                + " pausing for 10 seconds before retry");
                                Thread.sleep(10000); // wait 10 seconds to try again
                            } catch (InterruptedException ie) {
                                // Restore the interrupted status
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }
                throw new IllegalStateException(
                        "File not found bucket: " + bucketName + " key: " + keyPrefix + key);
            }
            return s3Object;
        }

        @Override
        public String path() {
            return this.keyPrefix;
        }

        @Override
        public String name() {
            return this.key;
        }

        @Override
        public Lock lock() {
            return lockProvider.acquire(keyPrefix + key);
        }

        @Override
        public void addListener(ResourceListener listener) {
            getResourceNotificationDispatcher().addListener(keyPrefix + key, listener);
        }

        @Override
        public void removeListener(ResourceListener listener) {
            getResourceNotificationDispatcher().removeListener(keyPrefix + key, listener);
        }

        @Override
        public InputStream in() {
            return s3Object().getObjectContent();
        }

        @Override
        public OutputStream out() {

            try {
                // first save to a temp file
                final File temp;
                synchronized (this) {
                    File tryTemp = null;
                    UUID uuid = UUID.randomUUID();
                    try {
                        tryTemp = File.createTempFile(uuid.toString(), ".tmp");
                    } catch (IOException ioe) {
                        throw new IllegalStateException("Cannot access " + tryTemp);
                    }

                    temp = tryTemp;
                }
                // OutputStream wrapper used to write to a temporary file
                // (and only lock during move to actualFile)
                return new OutputStream() {

                    int numStreams = 1;
                    int numUploadThreads = 1;
                    int queueCapacity = 1;
                    int partSize = 5;

                    final StreamTransferManager manager =
                            new StreamTransferManager(
                                    bucketName,
                                    keyPrefix + key,
                                    amazonS3,
                                    numStreams,
                                    numUploadThreads,
                                    queueCapacity,
                                    partSize);
                    final List<MultiPartOutputStream> streams = manager.getMultiPartOutputStreams();
                    MultiPartOutputStream delegate = streams.get(0);

                    @Override
                    public void close() throws IOException {
                        delegate.close();
                        manager.complete();
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        delegate.write(b, off, len);
                        try {
                            delegate.checkSize();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(
                                    "Upload to "
                                            + bucketName
                                            + ":"
                                            + keyPrefix
                                            + key
                                            + " was interrupted",
                                    e);
                        }
                    }

                    @Override
                    public void flush() throws IOException {
                        delegate.flush();
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        delegate.write(b);
                        try {
                            delegate.checkSize();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(
                                    "Upload to "
                                            + bucketName
                                            + ":"
                                            + keyPrefix
                                            + key
                                            + " was interrupted",
                                    e);
                        }
                    }

                    @Override
                    public void write(int b) throws IOException {
                        delegate.write(b);
                        try {
                            delegate.checkSize();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(
                                    "Upload to "
                                            + bucketName
                                            + ":"
                                            + keyPrefix
                                            + key
                                            + " was interrupted",
                                    e);
                        }
                    }
                };
            } catch (SdkClientException e) {
                throw new IllegalStateException("Cannot write " + bucketName + ":" + key, e);
            }
        }

        @Override
        public File file() {

            try {
                InputStream in = s3Object().getObjectContent();
                File tmp = File.createTempFile("s3test", "");
                java.nio.file.Files.copy(in, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                closeQuitely();
                return tmp;
            } catch (IOException ioe) {
                throw new IllegalStateException("Cannot write temp file ", ioe);
            } catch (SdkClientException e) {
                throw new IllegalStateException("Cannot read " + bucketName + ":" + key, e);
            } catch (NullPointerException npe) {
                throw new IllegalStateException("Cannot read " + bucketName + ":" + key, npe);
            }
        }

        @Override
        public File dir() {
            return null;
        }

        @Override
        public long lastmodified() {
            if (objectExists) {
                return amazonS3.getObjectMetadata(this.bucketName, this.keyPrefix + this.key).getLastModified().getTime();
            } else {
                return -1;
            }
        }

        @Override
        public Resource parent() {
            return null;
        }

        @Override
        public Resource get(String resourcePath) {
            if (resourcePath == null) {
                throw new NullPointerException("Resource path required");
            }
            if ("".equals(resourcePath)) {
                return this;
            }
            return S3ResourceStore.this.get(resourcePath);
        }

        @Override
        public List<Resource> list() {
            if (objectExists) {
                return Collections.emptyList();
            } else {
                String listPrefix = keyPrefix;
                if (key != null && !key.equals("")) {
                    listPrefix = keyPrefix + key + "/";
                }
                ListObjectsRequest listObjectsRequest =
                        new ListObjectsRequest()
                                .withBucketName(bucketName).withPrefix(listPrefix).withDelimiter("/");

                ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
                List<Resource> resources = new ArrayList<>();
                // resources
                for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
                    String key = s3ObjectSummary.getKey();
                    if (key.startsWith(listObjectsRequest.getPrefix())) {
                        key = key.substring(listPrefix.length());
                    }
                    S3Resource resource =
                            new S3Resource(bucketName, listPrefix, key);
                    resources.add(resource);
                }
                // directories
                for (String prefix : objectListing.getCommonPrefixes()) {
                    // remove trailing '/'
                    String key = prefix.substring(0, prefix.length() - 1);
                    if (key.startsWith(listPrefix)) {
                        key = key.substring(listPrefix.length());
                    }
                    S3Resource resource =
                            new S3Resource(bucketName, listPrefix, key);
                    resources.add(resource);
                }
                return resources;
            }
        }

        @Override
        public Type getType() {
            if (objectExists) return Type.RESOURCE;
            else return Type.DIRECTORY;
        }

        @Override
        public boolean delete() {
            Lock lock = lock();
            try {
                amazonS3.deleteObject(bucketName, keyPrefix + key);
                return true;
            } catch (SdkClientException sce) {
                log.error(
                        "Could not delete s3 object in bucket: "
                                + bucketName
                                + " key: "
                                + keyPrefix
                                + key, sce);
                return false;
            } finally {
                lock.release();
            }
        }

        @Override
        public boolean renameTo(Resource dest) {
            Lock lock = lock();
            try {
                CopyObjectResult copyObjectResult =
                        amazonS3.copyObject(bucketName, keyPrefix + key, bucketName, dest.name());
                amazonS3.deleteObject(bucketName, keyPrefix + key);
                s3Object = amazonS3.getObject(bucketName, keyPrefix + dest.name());
                this.key = dest.name();
                closeQuitely();
                return true;
            } catch (SdkClientException sce) {
                log.error(
                        "Could not rename s3 object in bucket: "
                                + bucketName
                                + " key: "
                                + keyPrefix
                                + key
                                + "to: "
                                + keyPrefix
                                + dest.name());
                return false;
            } finally {
                lock.release();
            }
        }

        @Override
        public void close() throws IOException {
            if (s3Object != null) {
                s3Object.close();
                s3Object = null;
            }
        }

        /**
         * {@link #close()}, but only log exceptions, don't throw them
         */
        private void closeQuitely() {
            try {
                close();
            } catch (IOException e) {
                log.warn("Error closing connection to S3 object: " + keyPrefix + key, e);
            }
        }
    }
}
