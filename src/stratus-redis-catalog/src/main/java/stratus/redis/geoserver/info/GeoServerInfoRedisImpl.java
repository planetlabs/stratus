/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.info;

import lombok.NoArgsConstructor;
import org.geoserver.catalog.MetadataMap;
import org.geoserver.config.*;
import org.geoserver.config.impl.CoverageAccessInfoImpl;
import org.geoserver.config.impl.JAIInfoImpl;
import org.geoserver.config.impl.SettingsInfoImpl;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author joshfix
 * Created on 6/5/18
 */
@NoArgsConstructor
@RedisHash("GeoServerInfo")
public class GeoServerInfoRedisImpl implements GeoServerInfo, Serializable {

    private static final long serialVersionUID = 2200774379516625113L;
    protected String id;
    protected long updateSequence;
    protected int featureTypeCacheSize;
    protected SettingsInfo settings = new SettingsInfoImpl();
    protected String jaiBytesBase64;
    protected CoverageAccessInfo coverageAccess = new CoverageAccessInfoImpl();
    protected MetadataMap metadata = new MetadataMap();
    protected Map<Object, Object> clientProperties = new HashMap<>();
    protected String adminUsername;
    protected String adminPassword;
    protected Boolean useHeadersProxyURL = false;
    protected Boolean globalServices = true;
    protected Integer xmlPostRequestLogBufferSize = 1024;
    protected Boolean xmlExternalEntitiesEnabled = Boolean.FALSE;
    protected String lockProviderName;
    protected WebUIMode webUIMode = WebUIMode.DEFAULT;

    @Transient private JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
    private ResourceErrorHandling resourceErrorHandling;

    @Transient protected transient GeoServer geoServer;
    @Transient protected transient JAIInfo jai = new JAIInfoImpl();

    //deprecated members, kept around to maintain xstream persistence backward compatability
    @Deprecated protected ContactInfo contact;
    @Deprecated protected String charset;
    @Deprecated protected String title;
    @Deprecated protected Integer numDecimals;
    @Deprecated protected String onlineResource;
    @Deprecated protected String schemaBaseUrl;
    @Deprecated protected String proxyBaseUrl;
    @Deprecated protected Boolean verbose;
    @Deprecated protected Boolean verboseExceptions;

    public GeoServerInfoRedisImpl(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SettingsInfo getSettings() {
        return settings;
    }

    @Override
    public void setSettings(SettingsInfo settings) {
        this.settings = settings;
    }

    @Override
    public Boolean isUseHeadersProxyURL() {
        return useHeadersProxyURL;
    }

    @Override
    public void setUseHeadersProxyURL(Boolean useHeadersProxyURL) {
        this.useHeadersProxyURL = useHeadersProxyURL;
    }

    @Override
    public void setContact(ContactInfo contactInfo) {
        getSettings().setContact(contactInfo);
    }

    @Override
    public ContactInfo getContact() {
        return getSettings().getContact();
    }

    @Override
    public JAIInfo getJAI() {
        if (jai == null) {
            byte[] jaiBytes = Base64.getDecoder().decode(jaiBytesBase64);
            jai = (JAIInfo) serializer.deserialize(jaiBytes);
        }
        return jai;
    }

    public void setJAI(JAIInfo jai) {
        this.jai = jai;
        byte[] jaiBytes = serializer.serialize(jai);
        // base 64 encode, otherwise spring data creates a hash field for every entry in the byte array
        jaiBytesBase64 = Base64.getEncoder().encodeToString(jaiBytes);
    }

    public CoverageAccessInfo getCoverageAccess() {
        return coverageAccess;
    }

    public void setCoverageAccess(CoverageAccessInfo coverageAccess) {
        this.coverageAccess = coverageAccess;
    }

    public void setTitle(String title) {
        getSettings().setTitle(title);
    }

    public String getTitle() {
        return getSettings().getTitle();
    }

    public String getCharset() {
        return getSettings().getCharset();
    }

    public void setCharset(String charset) {
        getSettings().setCharset(charset);
    }

    public int getNumDecimals() {
        return getSettings().getNumDecimals();
    }

    public void setNumDecimals(int numDecimals) {
        getSettings().setNumDecimals(numDecimals);
    }

    public String getOnlineResource() {
        return getSettings().getOnlineResource();
    }

    public void setOnlineResource(String onlineResource) {
        getSettings().setOnlineResource(onlineResource);
    }

    public String getProxyBaseUrl() {
        return getSettings().getProxyBaseUrl();
    }

    public void setProxyBaseUrl(String proxyBaseUrl) {
        getSettings().setProxyBaseUrl(proxyBaseUrl);
    }

    public String getSchemaBaseUrl() {
        return getSettings().getSchemaBaseUrl();
    }

    public void setSchemaBaseUrl(String schemaBaseUrl) {
        getSettings().setSchemaBaseUrl(schemaBaseUrl);
    }

    public boolean isVerbose() {
        return getSettings().isVerbose();
    }

    public void setVerbose(boolean verbose) {
        getSettings().setVerbose(verbose);
    }

    public boolean isVerboseExceptions() {
        return getSettings().isVerboseExceptions();
    }

    @Override
    public void setVerboseExceptions(boolean verboseExceptions) {
        getSettings().setVerboseExceptions(verboseExceptions);
    }

    @Override
    public long getUpdateSequence() {
        return updateSequence;
    }

    @Override
    public void setUpdateSequence( long updateSequence ) {
        this.updateSequence = updateSequence;
    }

    @Override
    public String getAdminPassword() {
        return adminPassword;
    }

    @Override
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public String getAdminUsername() {
        return adminUsername;
    }

    @Override
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    @Override
    public int getFeatureTypeCacheSize() {
        return featureTypeCacheSize;
    }

    @Override
    public void setFeatureTypeCacheSize(int featureTypeCacheSize) {
        this.featureTypeCacheSize = featureTypeCacheSize;
    }

    // Also use get because the java Introspector does not respect "is" prefix for getters of Boolean object types
    // (as opposed to primitives)
    public Boolean getGlobalServices() {
        return globalServices;
    }

    @Override
    public Boolean isGlobalServices() {
        return globalServices;
    }

    @Override
    public void setGlobalServices(Boolean forceVirtualServices) {
        this.globalServices = forceVirtualServices;
    }

    @Override
    public void setXmlPostRequestLogBufferSize(Integer bufferSize) {
        this.xmlPostRequestLogBufferSize = bufferSize;

    }

    @Override
    public Integer getXmlPostRequestLogBufferSize() {
        return this.xmlPostRequestLogBufferSize;
    }

    /**
     * If true it enables evaluation of XML entities contained in XML files received in a service (WMS, WFS, ...) request.
     * Default is FALSE.
     * Enabling this feature is a security risk.
     */
    @Override
    public void setXmlExternalEntitiesEnabled(Boolean xmlExternalEntitiesEnabled) {
        this.xmlExternalEntitiesEnabled = xmlExternalEntitiesEnabled;
    }

    /**
     * If true it enables evaluation of XML entities contained in XML files received in a service (WMS, WFS, ...) request.
     * Default is FALSE.
     * Enabling this feature is a security risk.
     */
    @Override
    public Boolean isXmlExternalEntitiesEnabled() {
        return this.xmlExternalEntitiesEnabled;
    }

    @Override
    public MetadataMap getMetadata() {
        return metadata;
    }

    @Override
    public Map<Object, Object> getClientProperties() {
        return clientProperties;
    }

    @Override
    public String getLockProviderName() {
        return this.lockProviderName;
    }

    @Override
    public void setLockProviderName(String lockProviderName) {
        this.lockProviderName = lockProviderName;
    }

    @Override
    public void dispose() {
        if (coverageAccess != null){
            coverageAccess.dispose();
        }
    }

    @Override
    public void setResourceErrorHandling(ResourceErrorHandling mode) {
        this.resourceErrorHandling = mode;
    }

    @Override
    public ResourceErrorHandling getResourceErrorHandling() {
        if(this.resourceErrorHandling == null) {
            return ResourceErrorHandling.SKIP_MISCONFIGURED_LAYERS;
        }

        return resourceErrorHandling;
    }

    @Override
    public WebUIMode getWebUIMode() {
        return webUIMode;
    }

    @Override
    public void setWebUIMode(WebUIMode webUIMode) {
        this.webUIMode = webUIMode;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGeoServer(GeoServer geoServer) {
        this.geoServer = geoServer;
    }

    public void setClientProperties(Map<Object, Object> properties) {
        this.clientProperties = properties;
    }

    public void setMetadata(MetadataMap metadata) {
        this.metadata = metadata;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((settings == null) ? 0 : settings.hashCode());
        result = prime * result
                + ((adminPassword == null) ? 0 : adminPassword.hashCode());
        result = prime * result
                + ((adminUsername == null) ? 0 : adminUsername.hashCode());
        result = prime
                * result
                + ((clientProperties == null) ? 0 : clientProperties.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((metadata == null) ? 0 : metadata.hashCode());
        result = prime * result + new Long(updateSequence).hashCode();
        result = prime * result + (globalServices ? 1231 : 1237);
        result = prime * result + xmlPostRequestLogBufferSize;
        result = prime * result +
                ((resourceErrorHandling == null) ? 0 : resourceErrorHandling.hashCode());
        result = prime * result + ((lockProviderName == null) ? 0 : lockProviderName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!( obj instanceof GeoServerInfo ) ) {
            return false;
        }
        final GeoServerInfo other = (GeoServerInfo) obj;
        if (adminPassword == null) {
            if (other.getAdminPassword() != null)
                return false;
        } else if (!adminPassword.equals(other.getAdminPassword()))
            return false;
        if (adminUsername == null) {
            if (other.getAdminUsername() != null)
                return false;
        } else if (!adminUsername.equals(other.getAdminUsername()))
            return false;
        if (settings == null) {
            if (other.getSettings() != null)
                return false;
        } else if (!settings.equals(other.getSettings()))
            return false;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (updateSequence != other.getUpdateSequence())
            return false;

        if (!Objects.equals(globalServices, other.isGlobalServices()))
            return false;
        if (xmlPostRequestLogBufferSize == null) {
            if (other.getXmlPostRequestLogBufferSize() != null) {
                return false;
            }
        }
        else if (!xmlPostRequestLogBufferSize.equals(other.getXmlPostRequestLogBufferSize())) {
            return false;
        }

        if (getResourceErrorHandling() == null) {
            if (other.getResourceErrorHandling() != null) return false;
        } else {
            if (!getResourceErrorHandling().equals(other.getResourceErrorHandling())) return false;
        }

        if (lockProviderName == null) {
            if (other.getLockProviderName() != null) return false;
        } else {
            if (!lockProviderName.equals(other.getLockProviderName())) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append('[').append(getTitle()).append(']')
                .toString();
    }

    /*
     * XStream specific method, needed to initialize members that are added over time and to cleanly
     * handle deprecated members.
     */
    public Object readResolve() {
        if (this.globalServices == null) {
            this.globalServices = true;
        }
        if (this.xmlPostRequestLogBufferSize == null) {
            this.xmlPostRequestLogBufferSize = 1024;
        }
        if (this.settings == null) {
            this.settings = new SettingsInfoImpl();
        }

        //handle deprecated members, forward values onto the setter methods
        if (contact != null) {
            setContact(contact);
            contact = null;
        }
        if (charset != null) {
            setCharset(charset);
            charset = null;
        }
        if (title != null) {
            setTitle(title);
            title = null;
        }
        if (numDecimals != null) {
            setNumDecimals(numDecimals);
            numDecimals = null;
        }
        if (onlineResource != null) {
            setOnlineResource(onlineResource);
            onlineResource = null;
        }
        if (schemaBaseUrl != null) {
            setSchemaBaseUrl(schemaBaseUrl);
            schemaBaseUrl = null;
        }
        if (proxyBaseUrl != null) {
            setProxyBaseUrl(proxyBaseUrl);
            proxyBaseUrl = null;
        }
        if (verbose != null) {
            setVerbose(verbose);
            verbose = null;
        }
        if (verboseExceptions != null) {
            setVerboseExceptions(verboseExceptions);
            verboseExceptions = null;
        }

        return this;
    }

}
