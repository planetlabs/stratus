/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import org.apache.commons.lang.StringUtils;
import org.geoserver.config.ConfigurationListenerAdapter;
import org.geoserver.config.GeoServer;
import org.geoserver.config.ServiceInfo;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.platform.resource.FileLockProvider;
import org.geoserver.platform.resource.FileSystemResourceStore;
import org.geoserver.platform.resource.Resource;
import org.geoserver.wps.ProcessGroupInfo;
import org.geoserver.wps.ProcessGroupInfoImpl;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.executor.DefaultProcessManager;
import org.geoserver.wps.resource.DefaultProcessArtifactsStore;
import org.geoserver.wps.resource.WPSResourceManager;
import org.geotools.process.ProcessFactory;
import org.geotools.process.Processors;
import org.springframework.stereotype.Service;
import stratus.commons.PostGeoServerInitializer;
import stratus.wps.config.WPSConfigurationProperties;
import stratus.wps.executor.StratusWPSExecutionManager;
import stratus.wps.resource.S3ResourceStore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class WPSInitializerS3 implements PostGeoServerInitializer {

    public static final String DEFAULT_OUTPUT_DIRECTORY = "temp/wps";

    private final AmazonS3 amazonS3;

    private final StratusWPSExecutionManager executionManager;

    private final DefaultProcessManager processManager;

    private final WPSStorageCleaner cleaner;

    private final WPSResourceManager resources;

    private final GeoServerResourceLoader resourceLoader;

    private final GeoServer geoServer;

    private final WPSConfigurationProperties wpsConfigurationProperties;

    public WPSInitializerS3(
            AmazonS3 amazonS3,
            GeoServer geoServer,
            StratusWPSExecutionManager executionManager,
            DefaultProcessManager processManager,
            WPSStorageCleaner cleaner,
            WPSResourceManager resources,
            GeoServerResourceLoader resourceLoader,
            WPSConfigurationProperties wpsConfigurationProperties) {
        this.amazonS3 = amazonS3;
        this.geoServer = geoServer;
        this.executionManager = executionManager;
        this.processManager = processManager;
        this.cleaner = cleaner;
        this.resources = resources;
        this.resourceLoader = resourceLoader;
        this.wpsConfigurationProperties = wpsConfigurationProperties;
    }

    @Override
    public void initialize() {

        initWPS(geoServer.getService(WPSInfo.class), geoServer);

        geoServer.addListener(
                new ConfigurationListenerAdapter() {
                    @Override
                    public void handleServiceChange(ServiceInfo service, List<String> propertyNames,
                        List<Object> oldValues, List<Object> newValues) {
                        if (service instanceof WPSInfo) {
                            initWPS((WPSInfo) service, geoServer);
                        }
                    }
                });
    }

    void initWPS(WPSInfo info, GeoServer geoServer) {
        // Handle the http connection timeout.
        // The specified timeout is in seconds. Convert it to milliseconds
        double connectionTimeout = info.getConnectionTimeout();
        if (connectionTimeout > 0) {
            executionManager.setConnectionTimeout((int) connectionTimeout * 1000);
        } else {
            // specified timeout == -1 represents infinite timeout.
            // by convention, for infinite URLConnection timeouts, we need to use zero.
            executionManager.setConnectionTimeout(0);
        }

        // handle the resource expiration timeout
        int expirationTimeout = info.getResourceExpirationTimeout() * 1000;
        if (expirationTimeout < 0) {
            // use the default of five minutes
            expirationTimeout = 5 * 60 * 1000;
        }
        cleaner.setExpirationDelay(expirationTimeout);
        executionManager.setHeartbeatDelay(expirationTimeout / 2);

        // the max number of synch proceesses
        int defaultMaxProcesses = Runtime.getRuntime().availableProcessors() * 2;
        int maxSynch = info.getMaxSynchronousProcesses();
        if (maxSynch > 0) {
            processManager.setMaxSynchronousProcesses(maxSynch);
        } else {
            processManager.setMaxSynchronousProcesses(defaultMaxProcesses);
        }

        // the max number of asynch proceesses
        int maxAsynch = info.getMaxAsynchronousProcesses();
        if (maxAsynch > 0) {
            processManager.setMaxAsynchronousProcesses(maxAsynch);
        } else {
            processManager.setMaxAsynchronousProcesses(defaultMaxProcesses);
        }

        // update the location of the artifact storage in case we are using a file system based
        // one
        if (resources.getArtifactsStore() instanceof DefaultProcessArtifactsStore) {
            WPSInfo wps = geoServer.getService(WPSInfo.class);
            String outputStorageDirectory = wps.getStorageDirectory();

            //check configuration to see if Amazon S3 or local file system will be used to store files
            if(wpsConfigurationProperties.getFileStorage()!=null&&wpsConfigurationProperties.getFileStorage().equalsIgnoreCase("s3")) {
                S3ResourceStore resourceStore;
                //check if Amazon S3 bucket specified in the configuration exists, if not create it
                if(!amazonS3.doesBucketExist(wpsConfigurationProperties.getS3Bucket())){
                    try {
                        amazonS3.createBucket(wpsConfigurationProperties.getS3Bucket());
                    }catch(SdkClientException sce){
                        throw new RuntimeException("Amazon S3 bucket for WPS called: "+wpsConfigurationProperties.getS3Bucket()+" does not exist and not able to create it",sce);

                    }
                }

                String bucketStorageDirectory =
                    StringUtils.isNotEmpty(outputStorageDirectory) ? outputStorageDirectory : DEFAULT_OUTPUT_DIRECTORY;

                resourceStore = new S3ResourceStore(amazonS3, wpsConfigurationProperties.getS3Bucket(),
                    bucketStorageDirectory);
                DefaultProcessArtifactsStore artifactsStore =
                        (DefaultProcessArtifactsStore) resources.getArtifactsStore();
                artifactsStore.setResourceStore(resourceStore);

            }else{
                FileSystemResourceStore resourceStore;
                if (outputStorageDirectory == null || outputStorageDirectory.trim().isEmpty()) {
                    Resource temp = resourceLoader.get(DEFAULT_OUTPUT_DIRECTORY);
                    resourceStore = new FileSystemResourceStore(temp.dir());
                } else {
                    File storage = new File(outputStorageDirectory);
                    // if it's a path relative to the data directory, make it absolute
                    if (!storage.isAbsolute()) {
                        //storage = resourceLoader.url(outputStorageDirectory);
                        // TODO not sure what the right method is here
                        try {
                            storage = resourceLoader.find(outputStorageDirectory);
                        } catch (IOException e) {
                            throw new IllegalArgumentException("Invalid wps storage path.", e);
                        }
                    }
                    if(storage.exists() && !storage.isDirectory()) {
                        throw new IllegalArgumentException("Invalid wps storage path, "
                                + "it represents a file: " + storage.getPath());
                    }
                    if(!storage.exists()) {
                        if (!storage.mkdirs()) {
                            throw new IllegalArgumentException(
                                    "Invalid wps storage path, it does not exist and cannot be created: "
                                            + storage.getPath());
                        }
                    }
                    resourceStore = new FileSystemResourceStore(storage);
                }
                // use a clustering ready lock provider
                try {
                    Resource lockDirectory = resourceLoader.get("tmp");
                    resourceStore.setLockProvider(new FileLockProvider(lockDirectory.dir()));
                } catch (IllegalStateException e) {
                    throw new RuntimeException(
                            "Unexpected failure searching for tmp directory inside geoserver data dir",
                            e);
                }
                DefaultProcessArtifactsStore artifactsStore =
                        (DefaultProcessArtifactsStore) resources.getArtifactsStore();
                artifactsStore.setResourceStore(resourceStore);
            }



        }

        lookupNewProcessGroups(info, geoServer);
    }

    static void lookupNewProcessGroups(WPSInfo info, GeoServer geoServer) {
        List<ProcessGroupInfo> newGroups = new ArrayList();

        for (ProcessGroupInfo available : lookupProcessGroups()) {
            boolean found = false;
            for (ProcessGroupInfo configured : info.getProcessGroups()) {
                if (configured.getFactoryClass().equals(available.getFactoryClass())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // add it
                newGroups.add(available);
            }
        }

        // only save if we have anything new to add
        if (!newGroups.isEmpty()) {
            info.getProcessGroups().addAll(newGroups);
            geoServer.save(info);
        }
    }

    static List<ProcessGroupInfo> lookupProcessGroups() {
        List<ProcessGroupInfo> processFactories = new ArrayList<ProcessGroupInfo>();

        // here we build a full list of process factories infos, covering all available
        // factories: this makes sure the available factories are availablefrom both
        // GUI and REST configuration

        // get the full list of factories
        List<ProcessFactory> factories =
                new ArrayList<ProcessFactory>(Processors.getProcessFactories());

        // ensure there is a stable order across invocations, JDK and so on
        Collections.sort(
                factories,
                new Comparator<ProcessFactory>() {

                    @Override
                    public int compare(ProcessFactory o1, ProcessFactory o2) {
                        if (o1 == null) {
                            return o2 == null ? 0 : -1;
                        } else if (o2 == null) {
                            return 1;
                        } else {
                            return o1.getClass().getName().compareTo(o2.getClass().getName());
                        }
                    }
                });

        // build the result, adding the ProcessFactoryInfo as necessary for the factories
        // that do not already have a configuration
        for (final ProcessFactory pf : factories) {
            ProcessGroupInfo pfi = new ProcessGroupInfoImpl();
            pfi.setEnabled(true);
            pfi.setFactoryClass(pf.getClass());

            processFactories.add(pfi);
        }

        return processFactories;
    }
}
