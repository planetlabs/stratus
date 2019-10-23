/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import org.opengis.util.ProgressListener;

public interface InputProvider {
    /**
     * Returns the value associated with this provider
     *
     * @param subListener
     */
    public Object getValue(ProgressListener subListener) throws Exception;

    /** Returns the input id for this value */
    public String getInputId();

    /** Returns true if the value has already been parsed */
    public boolean resolved();

    /**
     * Returns the number of "long" steps to be carried out in order to get this input. A long step
     * is either executing a sub-process, or having to fetch a remote data set
     */
    int longStepCount();
}
