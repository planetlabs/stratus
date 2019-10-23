/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.util.Version;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("StyleInfo")
public class StyleInfoRedisImpl implements StyleInfo, Serializable {

	private static final long serialVersionUID = 7579381156988673293L;
	
	@Id private String id;
    @Indexed private String name;
    @Indexed private String workspaceId = WorkspaceInfoRedisImpl.NO_WORKSPACE_ID;
    @Transient private transient WorkspaceInfo workspace;
    private String legendId;
    private LegendInfo legend;
    private MetadataMap metadata;
    private String filename;
    private String format = SLDHandler.FORMAT;
    private String languageVersion = SLDHandler.VERSION_10.toString();
    
    @Transient protected transient Catalog catalog;
    
    public WorkspaceInfo getWorkspace() {
    	if (workspace == null && !workspaceId.equals(WorkspaceInfoRedisImpl.NO_WORKSPACE_ID)) {
    		workspace = ResolvingProxy.create(workspaceId, WorkspaceInfo.class);
    	}
        return workspace;
    }

    public void setWorkspace(WorkspaceInfo workspace) {
    	this.workspace = workspace;
        workspaceId = workspace == null? WorkspaceInfoRedisImpl.NO_WORKSPACE_ID : workspace.getId();
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    @Deprecated
    public Version getSLDVersion() {
        return getFormatVersion();
    }

    @Override
    @Deprecated
    public void setSLDVersion(Version v) {
        setFormatVersion(v);
    }

    @Override
    public Version getFormatVersion() {
        return new Version(languageVersion);
    }

    @Override
    public void setFormatVersion(Version version) {
        this.languageVersion = version.toString();
    }

    @Override
    public StyledLayerDescriptor getSLD() throws IOException {
        return this.catalog.getResourcePool().getSld(this);
    }

    @Override
    public Style getStyle() throws IOException {
        return catalog.getResourcePool().getStyle( this );
    }

    @Override
    public String prefixedName() {
        if(workspace != null) {
            return workspace.getName() + ":" + getName();
        } else {
            return getName();
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((workspace == null) ? 0 : workspace.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((languageVersion == null) ? 0 : languageVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof StyleInfo))
            return false;
        final StyleInfo other = (StyleInfo) obj;
        if (filename == null) {
            if (other.getFilename() != null)
                return false;
        } else if (!filename.equals(other.getFilename()))
            return false;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        if (WorkspaceInfoRedisImpl.NO_WORKSPACE_ID.equals(workspaceId)) {
            if (other.getWorkspace() != null)
                return false;
        } else if (other.getWorkspace() == null || !workspaceId.equals(other.getWorkspace().getId()))
            return false;
        if (format == null) {
            if (other.getFormat() != null)
                return false;
        }
        else {
            if (!format.equals(other.getFormat()))
                return false;
        }
        if (languageVersion == null) {
            if (other.getFormatVersion() != null)
                return false;
        } else if (!languageVersion.equals(other.getFormatVersion().toString()))
            return false;
        return true;
    }
}
