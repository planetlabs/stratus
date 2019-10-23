/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.xml.v1_0_0;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.ServiceException;
import org.geoserver.wfs.WFSException;
import stratus.wfs.xml.WfsXmlParserHelper;
import org.geoserver.wfs.xml.v1_0_0.WfsXmlReader;
import org.geotools.xsd.Configuration;
import org.geotools.xsd.Parser;

import java.io.Reader;
import java.util.Map;

/**
 * Helper class for parsing WFS 1.0.0 XML requests (without involving the catalog)
 * Based on {@link WfsXmlReader}
 */
public class Wfs1_0_0XmlParserHelper extends WfsXmlParserHelper {

    Configuration configuration;

    public Wfs1_0_0XmlParserHelper(GeoServer geoserver, Configuration configuration) {
        super(geoserver);
        this.configuration = configuration;
    }
    @Override
    public Object parse(Object request, Reader reader, Map kvp) throws ServiceException {
        //create the parser instance
        Parser parser = new Parser(configuration);
        parser.setEntityResolver(entityResolverProvider.getEntityResolver());

        initRequestParser(parser, kvp);
        //parse
        Object parsed = null;

        try {
            parsed = parser.parse(reader);
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
