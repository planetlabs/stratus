/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ows;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.ows.DispatcherCallback;
import org.geoserver.ows.HttpErrorCodeException;
import org.geoserver.ows.Request;
import org.geoserver.ows.Response;
import org.geoserver.platform.Operation;
import org.geoserver.platform.Service;
import org.geoserver.platform.ServiceException;
import org.geotools.feature.NameImpl;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Examines an OWS request for virtual services, and verifies that they represent something that exists in the catalog
 * If this is not the case, throws a {@link HttpErrorCodeException} so that the OWS request returns an HTTP 404
 *
 * If {@link OWSCachingCallback} is enabled, this will run after it, so that any cache preloading is done before
 * verifying the virtual service, so that we don't make any unnecessary requests to redis
 */
@Slf4j
public class OWSVirtualServiceCallback implements DispatcherCallback {

    @Autowired
    Catalog catalog;

    public OWSVirtualServiceCallback() { }

    public OWSVirtualServiceCallback(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public Request init(Request request) {
        /* Check for virtual service */
        // /[ws]/ OR /[ws]/[layer] OR /[lg] (global lg only)
        String wsName = null;
        String layerName = null;
        if (request.getContext() != null) {
            wsName = request.getContext();

            //Handle GWC case - "gwc/service" appended to request context
            int gwc = wsName.indexOf("gwc/service");
            if (gwc >= 0) {
                wsName = wsName.substring(0, gwc);
                if (wsName.endsWith("/")) {
                    wsName = wsName.substring(0, wsName.lastIndexOf('/'));
                }
            }
            int slash = wsName.indexOf('/');
            if (slash > -1) {
                layerName = wsName.substring(slash + 1);
                wsName = wsName.substring(0, slash);
            }
            if (wsName.length() == 0) {
                wsName = null;
            }
        }
        //Verify the virtual service exists
        if (wsName != null) {
            WorkspaceInfo ws = catalog.getWorkspaceByName(wsName);
            NamespaceInfo ns = catalog.getNamespaceByPrefix(wsName);
            if (ws != null && ns != null) {
                if (layerName != null) {
                    if (catalog.getLayerByName(new NameImpl(ns.getURI(), layerName)) == null) {
                        if (log.isTraceEnabled()) {
                            log.trace("Could not find layer " + layerName + ", trying a layer group lookup");
                        }
                        if (catalog.getLayerGroupByName(ws, layerName) == null) {
                            log.trace("Could not a layer group named " + wsName + ":" + layerName);
                            throw new HttpErrorCodeException(404);
                        }
                    }
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Could not find workspace " + wsName + ", trying a layer group lookup");
                }
                if (catalog.getLayerGroupByName((WorkspaceInfo) null, wsName) == null) {
                    log.trace("Could not a layer group named " + wsName);
                    throw new HttpErrorCodeException(404);
                }
            }
        }
        return request;
    }

    @Override
    public Service serviceDispatched(Request request, Service service) throws ServiceException {
        return service;
    }

    @Override
    public Operation operationDispatched(Request request, Operation operation) {
        return operation;
    }

    @Override
    public Object operationExecuted(Request request, Operation operation, Object result) {
        return result;
    }

    @Override
    public Response responseDispatched(Request request, Operation operation, Object result, Response response) {
        return response;
    }

    @Override
    public void finished(Request request) {

    }
}
