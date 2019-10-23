/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.catalog.workspace;

import lombok.Data;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.WorkspaceInfo;

/**
 * @author joshfix
 * Created on 6/15/18
 */
@Data
public class SearchResult {

    private String name;
    private String workspace;
    private String clazz;
    private String bbox;
    private String title;

    public SearchResult(LayerInfo ri){
        this.name = ri.getName();
        this.clazz = ri.toString();
        this.workspace = ri.getResource().getStore().getWorkspace().getName();
        this.bbox = ri.getResource().getLatLonBoundingBox().toString();
        this.title = ri.getTitle();
    }

    public SearchResult(WorkspaceInfo wi){
        this.name = wi.getName();
        this.clazz = wi.toString();
        this.workspace = wi.getName();
        this.bbox = null;
        this.title = null;
    }

    public SearchResult(LayerGroupInfo ri){
        this.name = ri.getName();
        this.clazz = ri.toString();
        this.workspace = ri.getWorkspace().getName();
        this.bbox = ri.getBounds().toString();
        this.title = ri.getTitle();
    }
}
