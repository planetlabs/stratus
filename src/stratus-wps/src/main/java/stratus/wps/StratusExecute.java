/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps;

import net.opengis.wps10.ExecuteResponseType;
import net.opengis.wps10.ExecuteType;
import net.opengis.wps10.OutputDefinitionType;
import net.opengis.wps10.ResponseDocumentType;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.WPSInfo;
import org.geoserver.wps.executor.ExecuteRequest;
import org.geotools.util.logging.Logging;
import org.springframework.context.ApplicationContext;
import stratus.wps.executor.StratusWPSExecutionManager;

import java.util.logging.Logger;

/**
 * Main class used to handle Execute requests
 * 
 * @author Lucas Reed, Refractions Research Inc
 * @author Andrea Aime, OpenGeo
 */
public class StratusExecute {

    static final Logger LOGGER = Logging.getLogger(StratusExecute.class);

    int connectionTimeout;

    WPSInfo wps;

    ApplicationContext context;

    StratusWPSExecutionManager executionManager;

    public StratusExecute(StratusWPSExecutionManager executionManager, ApplicationContext context) {
        this.context = context;
        this.executionManager = executionManager;
    }



    public ExecuteResponseType run(ExecuteType execute) {
        ResponseDocumentType responseDocument = null;
        OutputDefinitionType rawDataOutput = null;
        if (execute.getResponseForm() != null) {
            responseDocument = execute.getResponseForm().getResponseDocument();
            rawDataOutput = execute.getResponseForm().getRawDataOutput();
        }

        if (responseDocument != null && rawDataOutput != null) {
            throw new WPSException("Invalid request, only one of the raw data output or the "
                    + "response document should be specified in the request");
        }

        ExecuteRequest request = new ExecuteRequest(execute);

        ExecuteResponseType response = executionManager.submit(request, !request.isAsynchronous());
        return response;
    }

}
