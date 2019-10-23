/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.config.GeoServer;
import org.geoserver.config.impl.DefaultGeoServerFacade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of {@link GeoServerCache}
 */
public class DefaultGeoServerCache extends DefaultGeoServerFacade implements GeoServerCache {

    private boolean allServices = false;
    private Set<String> servicesByWorkspace = new HashSet<>();

    private HashMap<String, Set<List<Object>>> cachedMethodCalls = new HashMap<>();

    public DefaultGeoServerCache(GeoServer geoServer) {
        super(geoServer);
    }

    @Override
    public boolean isCached(String method, List<Object> arguments) {
        Set<List<Object>> calls = cachedMethodCalls.get(method);
        return calls != null && calls.contains(arguments);
    }

    @Override
    public void setCached(String method, List<Object> arguments, boolean cached) {
        Set<List<Object>> calls = cachedMethodCalls.get(method);

        if (cached) {
            if (calls == null) {
                calls = new HashSet<>();
                cachedMethodCalls.put(method, calls);
            }
            calls.add(arguments);
        } else {
            if (calls != null) {
                calls.remove(arguments);
            }
        }
    }

    @Override
    public boolean isServicesCached() {
        return allServices;
    }

    @Override
    public boolean isServicesCached(WorkspaceInfo workspace) {
        return servicesByWorkspace.contains(workspace.getId());
    }

    @Override
    public void setServicesCached(boolean cached) {
       allServices = cached;
    }

    @Override
    public void setServicesCached(WorkspaceInfo workspace, boolean cached) {
        if (cached) {
            servicesByWorkspace.add(workspace.getId());
        } else {
            servicesByWorkspace.remove(workspace.getId());
        }
    }
}
