/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import net.opengis.wps10.ComplexDataType;
import net.opengis.wps10.DataType;
import net.opengis.wps10.InputType;
import net.opengis.wps10.LiteralDataType;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.ppio.*;
import org.geoserver.wps.process.ByteArrayRawData;
import org.geoserver.wps.process.StringRawData;
import org.geotools.data.Base64;
import org.opengis.util.ProgressListener;

import java.io.ByteArrayInputStream;

public class SimpleInputProvider  extends AbstractInputProvider {

    public SimpleInputProvider(InputType input, ProcessParameterIO ppio) {
        super(input, ppio);
    }

    @Override
    protected Object getValueInternal(ProgressListener listener) throws Exception {
        // actual data, figure out which type
        DataType data = input.getData();
        Object result = null;

        if (data.getLiteralData() != null) {
            LiteralDataType literal = data.getLiteralData();
            result = ((LiteralPPIO) ppio).decode(literal.getValue());
        } else if (data.getComplexData() != null) {
            ComplexDataType complex = data.getComplexData();
            if (ppio instanceof RawDataPPIO) {
                Object inputData = complex.getData().get(0);
                String encoding = complex.getEncoding();
                byte[] decoded = null;
                if (encoding != null) {
                    if ("base64".equals(encoding)) {
                        String input = inputData.toString();
                        decoded = Base64.decode(input);
                    } else {
                        throw new WPSException("Unsupported encoding " + encoding);
                    }
                }

                if (decoded != null) {
                    return new ByteArrayRawData(decoded, complex.getMimeType());
                } else {
                    return new StringRawData(inputData.toString(), complex.getMimeType());
                }

            } else {
                Object inputData = complex.getData().get(0);
                String encoding = complex.getEncoding();
                byte[] decoded = null;
                if (encoding != null) {
                    if ("base64".equals(encoding)) {
                        String input = inputData.toString();
                        decoded = Base64.decode(input);
                    } else {
                        throw new WPSException("Unsupported encoding " + encoding);
                    }
                }

                if (decoded != null) {
                    result = ((ComplexPPIO) ppio).decode(new ByteArrayInputStream(decoded));
                } else {
                    result = ((ComplexPPIO) ppio).decode(inputData);
                }
            }
        } else if (data.getBoundingBoxData() != null) {
            result = ((BoundingBoxPPIO) ppio).decode(data.getBoundingBoxData());
        }

        return result;

    }

    @Override
    public int longStepCount() {
        return 0;
    }
}