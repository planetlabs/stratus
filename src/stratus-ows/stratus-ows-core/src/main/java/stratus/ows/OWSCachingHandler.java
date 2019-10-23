/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import org.geoserver.ows.Request;
import org.geoserver.ows.util.RequestUtils;

import javax.xml.namespace.QName;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static stratus.ows.OWSCachingCallback.normalize;

/**
 * Abstract class for identifying handler beans used by {@link OWSCachingCallback}
 */
public abstract class OWSCachingHandler {

    /**
     * Handles the passed request if the request parameters are applicable to this handler.
     *
     * @param serviceName Name of the OWS service, e.g. WMS. Not case sensitive.
     * @param versionName Name of the OWS service version, e.g. 1.1.0.
     * @param requestName Name of the OWS request, e.g. GetCapabilities. Not case sensitive.
     * @param virtualWsName Name of the virtual workspace (or global layer group), or null if none
     * @param virtualLayerName Name of the virtual layer, or null if none. Ignored if virtualWsName is null.
     * @param request Request
     */
    public abstract void handle(String serviceName, String versionName, String requestName,
                String virtualWsName, String virtualLayerName, Request request) throws OWSCachingException;

    /**
     * Parses parameter values from the kvp map for an OWS request.
     *
     * @param kvp KVP map for the request
     * @param key Parameter key
     * @return Values for the given key. Returns an empty list if no values are found
     */
    protected List<String> getValues(Map kvp, String key) {
        List<String> values = new ArrayList<>();

        Object value = kvp.get(key);
        if (value != null) {
            if(value instanceof String) {
                String string = normalize((String)value);
                if (string != null) {
                    if (string.contains(",")) {
                        values.addAll(Arrays.asList(string.split(",")));
                    } else {
                        values.add(string);
                    }
                }
            } else if (value instanceof String[]) {
                for (String string : (String[])value) {
                    string = normalize(string);
                    if (string != null && !"".equals(string)) {
                        values.add(string);
                    }
                }
            } else if (value instanceof List) {
                List list = (List) value;
                //Handle unwrapping WFS 2.0.0 typenames
                if (list.get(0) instanceof List && list.size() == 1) {
                    value = list.get(0);
                    list = (List) list.get(0);
                }
                if (list.size() > 0) {
                    Object sample = list.get(0);
                    if (sample instanceof String) {
                        for (String string : (List<String>) value) {
                            string = normalize(string);
                            if (string != null && !"".equals(string)) {
                                values.add(string);
                            }
                        }
                    } else if (sample instanceof QName) {
                        for (QName qName : (List<QName>) value) {
                            values.add((null == qName.getPrefix() || "".equals(qName.getPrefix())) ?
                                    qName.getLocalPart() : qName.getPrefix() + ":" + qName.getLocalPart());
                        }
                    }
                }

            }
        }
        return values;
    }

    /**
     *
     * Allows for reading of the {@link Request} body while retaining an input stream for future processing.
     *
     * Reads the content of {@link Request#getInput()} into an array. Constructs two BufferedReaders from this array.
     * Assigns one to {@link Request#setInput(BufferedReader)} and returns the other.
     *
     * @param request
     * @return
     * @throws IOException
     */
    protected BufferedReader splitRequestReader(Request request) throws IOException {
        if (request.getInput() == null || request.getHttpRequest() == null) {
            return null;
        }
        //Read the request body into memory, so that we can read it here and still read it later, when the request is handled
        //TODO: Find a better way of doing this, ideally not involving reading the whole request into memory
        if (request.getHttpRequest().getContentLength() >= 0) {
            //if we know the content length, just read that many bytes

            char[] content = new char[request.getHttpRequest().getContentLength()];
            request.getInput().read(content, 0, request.getHttpRequest().getContentLength());

            BufferedReader currentReader = RequestUtils.getBufferedXMLReader(new CharArrayReader(content), 8192);
            BufferedReader nextReader = RequestUtils.getBufferedXMLReader(new CharArrayReader(content), 8192);
            request.getInput().close();
            request.setInput(nextReader);

            return currentReader;

        } else {
            //otherwise, read the stream until we reach the end

            int totalSize = 0;
            int blockSize = 512;
            char[] content = new char[blockSize];
            BufferedReader reader = request.getInput();

            int read = reader.read(content, 0, blockSize);
            while (read > 0) {
                totalSize += read;
                if (read == blockSize) {
                    char[] temp = new char[totalSize + blockSize];
                    System.arraycopy(content, 0, temp, 0, content.length);
                    content = temp;
                    read = reader.read(content, totalSize, blockSize);
                } else {
                    char[] temp = new char[totalSize];
                    System.arraycopy(content, 0, temp, 0, totalSize);
                    content = temp;
                    break;
                }
            }

            BufferedReader currentReader = RequestUtils.getBufferedXMLReader(new CharArrayReader(content), 8192);
            BufferedReader nextReader = RequestUtils.getBufferedXMLReader(new CharArrayReader(content), 8192);
            request.getInput().close();
            request.setInput(nextReader);

            return currentReader;
        }
    }
}
