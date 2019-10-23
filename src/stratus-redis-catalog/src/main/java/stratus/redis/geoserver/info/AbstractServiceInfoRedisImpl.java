/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.info;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.geoserver.catalog.KeywordInfo;
import org.geoserver.catalog.MetadataLinkInfo;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geoserver.config.GeoServer;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static stratus.redis.geoserver.RedisGeoServerFacade.NO_WORKSPACE;

/**
 * Base class for {@link ServiceInfo} implementations uses for serialization to Redis.
 */
@Data
public abstract class AbstractServiceInfoRedisImpl implements ServiceInfo {
    @Indexed
    private String id;

    @Indexed
    private String clazz;

    protected static Class<? extends ServiceInfo> interfaceClass = ServiceInfo.class;
    protected static Class<? extends ServiceInfo> implClass = ServiceInfoImpl.class;

    @Indexed
    private String name;

    @Indexed
    private String workspaceId = NO_WORKSPACE;

    @Transient
    private WorkspaceInfo workspace;

    @Transient
    private transient GeoServer geoServer;

    protected boolean enabled = true;

    protected String title;

    protected String maintainer;

    protected String abstrct;

    protected String accessConstraints;

    protected String fees;

    protected ArrayList versions = new ArrayList();

    protected ArrayList<KeywordInfo> keywords = new ArrayList();

    protected ArrayList exceptionFormats = new ArrayList();

    protected MetadataLinkInfo metadataLink;

    protected boolean citeCompliant;

    protected String onlineResource;

    protected String schemaBaseURL = "http://schemas.opengis.net";

    protected boolean verbose;

    protected String outputStrategy;

    protected Map<String, String> serializedMetadata = new HashMap<>();
    @Transient
    protected MetadataMap metadata;

    protected HashMap clientProperties = new HashMap();

    @Override
    public WorkspaceInfo getWorkspace() {
        if (workspace == null && !workspaceId.equals(NO_WORKSPACE)) {
            workspace = ResolvingProxy.create(workspaceId, WorkspaceInfo.class);
        }
        return workspace;
    }

    @Override
    public void setWorkspace(WorkspaceInfo workspace) {
        this.workspace = workspace;
        workspaceId = workspace == null ? NO_WORKSPACE : workspace.getId();
    }

    @Override
    public String getAbstract() {
        return abstrct;
    }

    @Override
    public void setAbstract(String abstrct) {
        this.abstrct = abstrct;
    }

    @Override
    public List<String> keywordValues() {
        List<String> values = new ArrayList<String>();
        if (keywords != null) {
            for (KeywordInfo kw : keywords) {
                values.add(kw.getValue());
            }
        }
        return values;
    }
    @Override
    public MetadataMap getMetadata() {
        if (metadata == null) {
            metadata = new MetadataMap();
            if (serializedMetadata != null) {

                for (Map.Entry<String, String> entry : serializedMetadata.entrySet()) {
                    metadata.put(entry.getKey(), (Serializable) SerializationUtils.deserialize(Base64.decodeBase64(entry.getValue())));
                }
            }
        }
        return metadata;
    }

    public void setMetadata(MetadataMap metadata) {
        this.metadata = metadata;
        if (metadata == null) {
            serializedMetadata = null;
            return;
        }

        serializedMetadata = new HashMap<>();
        for (Map.Entry<String, Serializable> entry : metadata.entrySet()) {
            if (entry.getValue() != null) {
                serializedMetadata.put(entry.getKey(), Base64.encodeBase64String(SerializationUtils.serialize(entry.getValue())));
            }
        }
    }
}
