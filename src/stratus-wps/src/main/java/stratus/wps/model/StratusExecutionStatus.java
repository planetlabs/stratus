/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.model;

import net.opengis.wps10.ExecuteType;
import org.geoserver.wps.executor.ExecutionStatus;
import org.geoserver.wps.executor.ProcessState;
import org.geoserver.wps.executor.ProcessStatusTracker;
import org.geotools.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

@RedisHash("ExecutionStatus")
public class StratusExecutionStatus {

    static final Logger LOGGER = Logging.getLogger(StratusExecutionStatus.class);

    private static final long serialVersionUID = -2433524030271115410L;


    public static final String NODE_IDENTIFIER = getNodeIdentifier();

    private static String getNodeIdentifier() {
        try {
            return getLocalAddress().getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    private static InetAddress getLocalAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
                    .hasMoreElements();) {
                NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
                if (ni.getName() != null && ni.getName().startsWith("vmnet")) {
                    // skipping vmware interfaces
                    continue;
                }
                // each interface can have more than one address
                for (Enumeration inetAddrs = ni.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    // we are not interested in loopback
                    if (!inetAddr.isLoopbackAddress() && !(inetAddr instanceof Inet6Address)) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // Fall back to whatever localhost provides
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException(
                        "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address");
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    /**
     * The process being executed
     */
    String processName;

    /**
     * The execution id, can be used to retrieve the process results
     */
    @Id
    String executionId;

    /**
     * If the request was asynchronous, or not
     */
    boolean asynchronous;

    /**
     * Current execution status
     */
    String phase;

    /**
     * True or False if the process is complete
     */
    Boolean completed;

    /**
     * Process execution status (as a percentage between 0 and 100)
     */
    float progress;

    /**
     * The name of the user that requested the process
     */
    String userName;

    /**
     * Request creation time
     */
    Date creationTime;

    /**
     * Request completion time
     */
    Date completionTime = null;

    /**
     * A heartbeat field, used when clustering nodes
     */
    Date lastUpdated;

    /**
     * What is the process currently working on
     */
    String task;

    /**
     * The process failure
     */
    Throwable exception;

    /**
     * Node identifier
     */
    String nodeId;

    public StratusExecutionStatus(){

    }

    public StratusExecutionStatus(String processName, String executionId, boolean asynchronous, boolean completed) {
        this.processName = processName;
        this.executionId = executionId;
        this.completed = completed;
        setPhase(ProcessState.QUEUED.toString());
        this.creationTime = new Date();
        this.lastUpdated = this.creationTime;
        this.asynchronous = asynchronous;

        // grab the user name that made the request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            this.userName = authentication.getName();
        }

        // grab the node id
        this.nodeId = NODE_IDENTIFIER;
    }

    public StratusExecutionStatus(StratusExecutionStatus other) {
        this.processName = other.processName;
        this.executionId = other.executionId;
        this.completed = other.completed;
        this.phase = (other.phase);
        this.progress = other.progress;
        this.task = other.task;
        this.exception = other.exception;
        this.creationTime = other.creationTime;
        this.completionTime = other.completionTime;
        this.asynchronous = other.asynchronous;
        this.userName = other.userName;
        this.nodeId = other.nodeId;
        this.lastUpdated = other.lastUpdated;
    }


    public ExecutionStatus toExecutionStatus(ExecuteType executeType){
        ExecutionStatus executionStatus = new ExecutionStatus(stringToName(this.processName), this.executionId, this.asynchronous);
        executionStatus.setPhase(stringToState(this.phase));
        executionStatus.setProgress(this.progress);
        executionStatus.setTask(this.task);
        if (this.exception != null) {
            executionStatus.setException(this.exception);
        }
        executionStatus.setCreationTime(this.creationTime);
        executionStatus.setCompletionTime(this.completionTime);
        executionStatus.setRequest(executeType);
        executionStatus.setUserName(this.userName);
        executionStatus.setLastUpdated(this.lastUpdated);

        return executionStatus;
    }

    public static StratusExecutionStatus toStratusExecutionStatus(ExecutionStatus other){
        StratusExecutionStatus stratusExecutionStatus = new StratusExecutionStatus(other.getProcessName().toString(), other.getExecutionId(), other.isAsynchronous(), other.getPhase().isExecutionCompleted());
        stratusExecutionStatus.setPhase(other.getPhase().toString());
        stratusExecutionStatus.setProgress(other.getProgress());
        stratusExecutionStatus.setTask(other.getTask());
        stratusExecutionStatus.setException(other.getException());
        stratusExecutionStatus.setCreationTime(other.getCreationTime());
        stratusExecutionStatus.setCompletionTime(other.getCompletionTime());
        stratusExecutionStatus.setUserName(other.getUserName());
        stratusExecutionStatus.setLastUpdated(other.getLastUpdated());

        return stratusExecutionStatus;
    }

    private Name stringToName(String processString){
        Name toName = new Name() {
            @Override
            public boolean isGlobal() {
                return false;
            }

            @Override
            public String getNamespaceURI() {
                return null;
            }

            @Override
            public String getSeparator() {
                return null;
            }

            @Override
            public String getLocalPart() {
                return processString;
            }

            @Override
            public String getURI() {
                return null;
            }

            @Override
            public String toString() {
                return getLocalPart();
            }
        };
        return toName;
    }

    private ProcessState stringToState(String processState){
        switch (processState){
            case "QUEUED": return ProcessState.QUEUED;
            case "RUNNING": return ProcessState.RUNNING;
            case "DISMISSING": return ProcessState.DISMISSING;
            case "SUCCEEDED": completed = true; return ProcessState.SUCCEEDED;
        }
        completed = true;
        return ProcessState.FAILED;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
        if (exception != null) {
            setPhase(ProcessState.FAILED.toString());
        }
    }

    public String getProcessName() {
        return processName.toString();
    }

    public String getSimpleProcessName() {
        return processName.toString();
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getPhase() {
        return phase;
    }

    /**
     * Returns the progress percentage, as a number between 0 and 100
     *
     *
     */
    public float getProgress() {
        return progress;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public void setPhase(String phase) {
        this.phase = phase;
        if (phase != null && this.completed
                //if there is already a completionTime don't overwrite it!
                &&this.completionTime==null) {
            this.completionTime = new Date();
        }
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public Throwable getException() {
        return exception;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionTime = completionTime;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public String getNodeId() {
        return nodeId;
    }

    /**
     * Last time this bean has been updated
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the last updated time. Only the {@link ProcessStatusTracker} should call this method
     *
     * @param lastUpdated
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "ExecutionStatus [processName=" + processName + ", executionId=" + executionId
                + ", asynchronous=" + asynchronous + ", phase=" + phase + ", progress=" + progress
                + ", userName=" + userName + ", creationTime=" + creationTime + ", completionTime="
                + completionTime + ", lastUpdated=" + lastUpdated + ", task=" + task
                + ", exception=" + exception + ", nodeId=" + nodeId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (asynchronous ? 1231 : 1237);
        result = prime * result + ((completionTime == null) ? 0 : completionTime.hashCode());
        result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
        result = prime * result + ((executionId == null) ? 0 : executionId.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        result = prime * result + ((phase == null) ? 0 : phase.hashCode());
        result = prime * result + ((processName == null) ? 0 : processName.hashCode());
        result = prime * result + Float.floatToIntBits(progress);
        result = prime * result + ((task == null) ? 0 : task.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }



}
