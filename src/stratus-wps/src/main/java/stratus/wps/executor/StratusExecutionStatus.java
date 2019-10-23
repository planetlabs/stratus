/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import org.geoserver.wps.executor.ExecutionStatus;
import org.geoserver.wps.executor.ProcessState;
import org.opengis.feature.type.Name;

public class StratusExecutionStatus extends ExecutionStatus {
    public StratusExecutionStatus(Name processName, String executionId, boolean asynchronous) {
        super(processName,executionId,asynchronous);
    }
    public StratusExecutionStatus(ExecutionStatus other) {
        super(other);
    }

    /**
     * Process execution status (as a percentage between 0 and 100)
     */
    float progress;

    /**
     * What is the process currently working on
     */
    String task;
    /**
     * Current execution status
     */
    ProcessState phase;
}
