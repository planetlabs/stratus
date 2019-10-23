/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import org.opengis.util.ProgressListener;

public class StringInputProvider implements InputProvider {

    String value;

    String inputId;

    public StringInputProvider(String value, String inputId) {
        this.value = value;
        this.inputId = inputId;
    }

    @Override
    public Object getValue(ProgressListener listener) throws Exception {
        return value;
    }

    @Override
    public String getInputId() {
        return inputId;
    }

    @Override
    public boolean resolved() {
        return true;
    }

    @Override
    public int longStepCount() {
        return 0;
    }

}
