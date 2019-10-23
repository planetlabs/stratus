/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps;

import stratus.wps.executor.StratusWPSExecutionManager;
import net.opengis.wps10.ExecuteResponseType;
import net.opengis.wps10.ExecuteType;
import org.geoserver.config.GeoServer;
import org.geoserver.wps.DefaultWebProcessingService;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.executor.ProcessStatusTracker;
import org.geoserver.wps.executor.WPSExecutionManager;
import org.geoserver.wps.resource.WPSResourceManager;

public class StratusDefaultWebProcessingService extends DefaultWebProcessingService {
    public StratusDefaultWebProcessingService(GeoServer gs, WPSExecutionManager executionManager,
                                       WPSResourceManager resources, ProcessStatusTracker tracker) {
        super(gs,executionManager,resources,tracker);


    }
    /**
     * @see org.geoserver.wps.WebProcessingService#execute
     */
    public ExecuteResponseType execute(ExecuteType request) throws WPSException {
        return new StratusExecute((StratusWPSExecutionManager)executionManager, context).run(request);
    }
}
