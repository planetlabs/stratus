/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.xml.v2_0;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.WFSException;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.xml.WFSXmlUtils;
import org.geoserver.wfs.xml.v2_0.WfsXmlReader;
import org.geotools.wfs.v2_0.WFSConfiguration;
import org.geotools.xsd.Parser;
import stratus.wfs.xml.WfsXmlParserHelper;

import java.io.Reader;
import java.util.Map;

/**
 * Helper class for parsing WFS 2.0.0 XML requests (without involving the catalog)
 * Based on {@link WfsXmlReader}
 */
public class Wfs2_0XmlParserHelper extends WfsXmlParserHelper {
    public Wfs2_0XmlParserHelper(GeoServer geoserver) {
        super(geoserver);
    }

    @Override
    public Object parse(Object request, Reader reader, Map kvp) throws ServiceException {
        WFSConfiguration config = new WFSConfiguration();

        Parser parser = new Parser(config);
        parser.setEntityResolver(entityResolverProvider.getEntityResolver());

        WFSInfo wfs = wfs();

        initRequestParser(parser, kvp);
        Object parsed = null;
        try {
            parsed = WFSXmlUtils.parseRequest(parser, reader, wfs);
        } catch(Exception e) {
            //check the exception, and set code to OperationParsingFailed if code not set
            if (!(e instanceof ServiceException) || ((ServiceException)e).getCode() == null) {
                e = new WFSException("Request parsing failed", e, "OperationParsingFailed");
            }
            throw (ServiceException) e;
        }
        return parsed;
    }
}
