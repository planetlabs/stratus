/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.tomcat.util.codec.binary.Base64;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("StoreInfo")
public abstract class StoreInfoRedisImpl implements StoreInfo, Serializable {

    private static final long serialVersionUID = 9099211099443953016L;

    @Id
    private String id;

    @Indexed
    private String name;

    @Transient
    protected transient Catalog catalog;

    private String description;
    private String type;
    private boolean enabled;

    @Indexed
    private String workspaceId = WorkspaceInfoRedisImpl.NO_WORKSPACE_ID;

    @Transient
    private WorkspaceInfo workspace;

    private Map<String, String> serializedConnectionParameters;
    @Transient
    private transient Map<String, Serializable> connectionParameters = null;   
    
    private MetadataMap metadata = new MetadataMap();
    private Throwable error;

    public <T> T getAdapter(Class<T> adapterClass, Map<?, ?> hints) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkspaceInfo getWorkspace() {
        if (workspace == null && !workspaceId.equals(WorkspaceInfoRedisImpl.NO_WORKSPACE_ID)) {
            workspace = ResolvingProxy.create(workspaceId, WorkspaceInfo.class);
        }
        return workspace;
    }

    @Override
    public void setWorkspace(WorkspaceInfo workspace) {
        this.workspace = workspace;
        workspaceId = workspace == null? WorkspaceInfoRedisImpl.NO_WORKSPACE_ID : workspace.getId();
    }
    
	@Override
	public Map<String, Serializable> getConnectionParameters() {
		if (serializedConnectionParameters != null && connectionParameters == null) {
			connectionParameters = new HashMap<>();
			for(Map.Entry<String, String> parameter : serializedConnectionParameters.entrySet()) {
				connectionParameters.put(parameter.getKey(), (Serializable) SerializationUtils.deserialize(Base64.decodeBase64(parameter.getValue())));				
			}
		}
		return connectionParameters;
	}
	
	public void setConnectionParameters(Map<String, Serializable> connectionParameters) {
		this.connectionParameters = connectionParameters;
		if (connectionParameters != null) {
			serializedConnectionParameters = new HashMap<>();
			for(Map.Entry<String, Serializable> parameter : connectionParameters.entrySet()) {
				if (parameter.getValue() != null) {
					serializedConnectionParameters.put(parameter.getKey(), Base64.encodeBase64String(SerializationUtils.serialize(parameter.getValue())));
				}
			}
		} else {
			serializedConnectionParameters = null;
		}
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!( obj instanceof StoreInfo ) ) {
            return false;
        }

        final StoreInfo other = (StoreInfo) obj;
        if (serializedConnectionParameters == null) {
            if (other.getConnectionParameters() != null)
                return false;
        } else if (!getConnectionParameters().equals(other.getConnectionParameters()))
            return false;
        if (description == null) {
            if (other.getDescription() != null)
                return false;
        } else if (!description.equals(other.getDescription()))
            return false;
        if (enabled != other.isEnabled())
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
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((serializedConnectionParameters == null) ? 0 : serializedConnectionParameters.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((workspaceId == null) ? 0 : workspaceId.hashCode());
        return result;
    }

}