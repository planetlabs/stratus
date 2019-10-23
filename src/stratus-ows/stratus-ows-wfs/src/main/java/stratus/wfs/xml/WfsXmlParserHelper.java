/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wfs.xml;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.ServiceException;
import org.geoserver.util.EntityResolverProvider;
import org.geoserver.wfs.WFSInfo;
import org.geoserver.wfs.xml.WFSXmlUtils;
import org.geotools.xsd.Parser;

import java.io.Reader;
import java.util.Map;

/**
 * Helper class for parsing WFS XML requests (without involving the catalog)
 * Based on {@link WFSXmlUtils}
 */
public abstract class WfsXmlParserHelper {
    protected GeoServer gs;
    protected EntityResolverProvider entityResolverProvider;

    public WfsXmlParserHelper(GeoServer geoserver) {
        this.gs = geoserver;
        this.entityResolverProvider = new EntityResolverProvider(gs);
    }

    protected WFSInfo wfs() {
        return gs.getService(WFSInfo.class);
    }

    protected void initRequestParser(Parser parser, Map kvp) {
        WFSInfo wfs = wfs();
        Boolean strict = (Boolean) kvp.get("strict");
        if ( strict == null ) {
            strict = Boolean.FALSE;
        }

        //check for cite compliance, we always validate for cite
        if ( wfs.isCiteCompliant() ) {
            strict = Boolean.TRUE;
        }
        parser.setValidating(strict);
        //WFSURIHandler.addToParser(gs, parser);

        //Catalog catalog = gs.getCatalog();

        //"inject" namespace mappings
        //parser.getNamespaces().add(new CatalogNamespaceSupport(catalog));
    }

    public abstract Object parse(Object request, Reader reader, Map kvp) throws ServiceException;
}
