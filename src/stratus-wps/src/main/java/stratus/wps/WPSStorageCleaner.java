/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps;

import com.amazonaws.SdkClientException;
import org.geoserver.wcs.response.WCSStorageCleaner;
import org.geoserver.wps.executor.ProcessStatusTracker;
import org.geoserver.wps.resource.WPSResourceManager;
import org.geotools.util.logging.Logging;
import org.springframework.stereotype.Service;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cleans up the temporary storage directory for WPS, as well as the storage process statuses
 *
 */
@Service
public class WPSStorageCleaner extends TimerTask {
    private static AtomicInteger AWSCONNECTIONERRORCOUNTER = new AtomicInteger(0);
    Logger LOGGER = Logging.getLogger(WPSStorageCleaner.class);

    long expirationDelay;

    WPSResourceManager resourceManager;

    ProcessStatusTracker statusTracker;


    public WPSStorageCleaner(WPSResourceManager resourceManager, ProcessStatusTracker statusTracker)
            throws IOException, ConfigurationException {
        this.resourceManager = resourceManager;
        this.statusTracker = statusTracker;

    }

    @Override
    public void run() {
        try {
            if (resourceManager.getArtifactsStore() == null || expirationDelay == 0) {
                return;
            }

            // ok, now scan for existing files there and clean up those that are too old
            long expirationThreshold = System.currentTimeMillis() - expirationDelay;
            statusTracker.cleanExpiredStatuses(expirationThreshold);
            resourceManager.cleanExpiredResources(expirationThreshold, statusTracker);
            AWSCONNECTIONERRORCOUNTER.set(0);//successfully connected to S3 bucket during cleanup, so reset the counter
        }catch(SdkClientException sce){
            int awsErrorCount = AWSCONNECTIONERRORCOUNTER.incrementAndGet();//Cleanup was leading to a stack being logged every minute.  This limits the logging to five times and then cancels the task.
            if(awsErrorCount<=4){
                LOGGER.log(Level.SEVERE,"Cannot connect to S3 bucket.  Will try "+(5-awsErrorCount)+" more times.",sce);
            }else{
                LOGGER.log(Level.INFO, "Tried 5 times to connect to the S3 bucket for WPS cleaning but could not connect.  Will attempt to cancel the scheduled WPS cleaning job.",sce);
                this.cancel();
            }
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    "Error occurred while trying to clean up " + "old coverages from temp storage",
                    e);
        }
    }

    /**
     * The file expiration delay in milliseconds. A file will be deleted when it's been around more
     * than expirationDelay
     */
    public long getExpirationDelay() {
        return expirationDelay;
    }

    /**
     * Sets the temp file expiration delay
     *
     * @param expirationDelay
     */
    public void setExpirationDelay(long expirationDelay) {
        this.expirationDelay = expirationDelay;
    }
}
