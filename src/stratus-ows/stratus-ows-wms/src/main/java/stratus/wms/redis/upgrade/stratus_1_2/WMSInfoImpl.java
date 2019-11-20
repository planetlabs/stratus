/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wms.redis.upgrade.stratus_1_2;

import org.geoserver.catalog.AuthorityURLInfo;
import org.geoserver.catalog.LayerIdentifierInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.ModificationProxy;
import org.geoserver.config.impl.ServiceInfoImpl;
import org.geoserver.wms.CacheConfiguration;
import org.geoserver.wms.WMSInfo;
import org.geoserver.wms.WatermarkInfo;
import org.geoserver.wms.WatermarkInfoImpl;
import stratus.redis.upgrade.Upgradable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/*
 * Copy of the GeoServer 2.13.0 WMSInfoImpl class, with serialVersionUID added.
 * Used for deserialization from Stratus 1.2.0 catalogs
 */
public class WMSInfoImpl extends ServiceInfoImpl implements WMSInfo, Upgradable<org.geoserver.wms.WMSInfoImpl> {

    private static final long serialVersionUID = 3889874487525565234L;
    List<String> srs = new ArrayList<String>();

    Boolean bboxForEachCRS;

    WatermarkInfo watermark = new WatermarkInfoImpl();

    WMSInterpolation interpolation = WMSInterpolation.Nearest;


    boolean getFeatureInfoMimeTypeCheckingEnabled;
    Set<String> getFeatureInfoMimeTypes = new HashSet<String>();

    boolean getMapMimeTypeCheckingEnabled;
    Set<String> getMapMimeTypes = new HashSet<String>();

    boolean dynamicStylingDisabled;

    // GetFeatureInfo result are reprojected by default
    private boolean featuresReprojectionDisabled = false;

    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "authorityURLs", and a not transient in the 2.2.x series.
     *
     * @since 2.1.3
     */
    protected List<AuthorityURLInfo> authorityURLs = new ArrayList<AuthorityURLInfo>(2);


    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "identifiers", and a not transient in the 2.2.x series.
     *
     * @since 2.1.3
     */
    protected List<LayerIdentifierInfo> identifiers = new ArrayList<LayerIdentifierInfo>(2);

    int maxBuffer;

    int maxRequestMemory;

    int maxRenderingTime;

    int maxRenderingErrors;

    private String capabilitiesErrorHandling;

    private String rootLayerTitle;

    private String rootLayerAbstract;

    public WMSInfoImpl() {
        authorityURLs = new ArrayList<AuthorityURLInfo>(2);
        identifiers = new ArrayList<LayerIdentifierInfo>(2);
    }

    public int getMaxRequestMemory() {
        return maxRequestMemory;
    }

    public void setMaxRequestMemory(int maxRequestMemory) {
        this.maxRequestMemory = maxRequestMemory;
    }

    public WatermarkInfo getWatermark() {
        return watermark;
    }

    public void setWatermark(WatermarkInfo watermark) {
        this.watermark = watermark;
    }

    public void setInterpolation(WMSInterpolation interpolation) {
        this.interpolation = interpolation;
    }

    public WMSInterpolation getInterpolation() {
        return interpolation;
    }

    public List<String> getSRS() {
        return srs;
    }

    public void setSRS(List<String> srs) {
        this.srs = srs;
    }

    public Boolean isBBOXForEachCRS() {
        if (bboxForEachCRS != null) {
            return bboxForEachCRS;
        }

        //check the metadata map if upgrading from 2.1.x
        Boolean bool = getMetadata().get("bboxForEachCRS", Boolean.class);
        return bool != null && bool;
    }

    public void setBBOXForEachCRS(Boolean bboxForEachCRS) {
        this.bboxForEachCRS = bboxForEachCRS;
    }

    public int getMaxBuffer() {
        return maxBuffer;
    }

    public void setMaxBuffer(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    public int getMaxRenderingTime() {
        return maxRenderingTime;
    }

    public void setMaxRenderingTime(int maxRenderingTime) {
        this.maxRenderingTime = maxRenderingTime;
    }

    public int getMaxRenderingErrors() {
        return maxRenderingErrors;
    }

    public void setMaxRenderingErrors(int maxRenderingErrors) {
        this.maxRenderingErrors = maxRenderingErrors;
    }

    @Override
    public List<AuthorityURLInfo> getAuthorityURLs() {
        return authorityURLs;
    }

    public void setAuthorityURLs(List<AuthorityURLInfo> urls) {
        this.authorityURLs = urls;
    }

    @Override
    public List<LayerIdentifierInfo> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<LayerIdentifierInfo> identifiers) {
        this.identifiers = identifiers;
    }

    public Set<String> getGetFeatureInfoMimeTypes() {
        return getFeatureInfoMimeTypes;
    }

    public void setGetFeatureInfoMimeTypes(Set<String> getFeatureInfoMimeTypes) {
        this.getFeatureInfoMimeTypes = getFeatureInfoMimeTypes;
    }

    public Set<String> getGetMapMimeTypes() {
        return getMapMimeTypes;
    }

    public void setGetMapMimeTypes(Set<String> getMapMimeTypes) {
        this.getMapMimeTypes = getMapMimeTypes;
    }

    public boolean isGetFeatureInfoMimeTypeCheckingEnabled() {
        return getFeatureInfoMimeTypeCheckingEnabled;
    }

    public void setGetFeatureInfoMimeTypeCheckingEnabled(boolean getFeatureInfoMimeTypeCheckingEnabled) {
        this.getFeatureInfoMimeTypeCheckingEnabled = getFeatureInfoMimeTypeCheckingEnabled;
    }

    public boolean isGetMapMimeTypeCheckingEnabled() {
        return getMapMimeTypeCheckingEnabled;
    }

    public void setGetMapMimeTypeCheckingEnabled(boolean getMapMimeTypeCheckingEnabled) {
        this.getMapMimeTypeCheckingEnabled = getMapMimeTypeCheckingEnabled;
    }

    public String getRootLayerTitle() {
        return rootLayerTitle;
    }

    public void setRootLayerTitle(String rootLayerTitle) {
        this.rootLayerTitle = rootLayerTitle;
    }

    public String getRootLayerAbstract() {
        return rootLayerAbstract;
    }

    public void setRootLayerAbstract(String rootLayerAbstract) {
        this.rootLayerAbstract = rootLayerAbstract;
    }

    /**
     * Sets the status of dynamic styling (SLD and SLD_BODY params) allowance
     *
     * @param dynamicStylingDisabled
     */
    @Override
    public void setDynamicStylingDisabled(Boolean dynamicStylingDisabled) {
        this.dynamicStylingDisabled= dynamicStylingDisabled;
    }

    /**
     * @return the status of dynamic styling (SLD and SLD_BODY params) allowance
     */
    @Override
    public Boolean isDynamicStylingDisabled() {
        return dynamicStylingDisabled;
    }


    @Override
    public boolean isFeaturesReprojectionDisabled() {
        return featuresReprojectionDisabled;
    }

    @Override
    public void setFeaturesReprojectionDisabled(boolean featuresReprojectionDisabled) {
        this.featuresReprojectionDisabled = featuresReprojectionDisabled;
    }

    //stub methods to satisfy API
    @Override
    public CacheConfiguration getCacheConfiguration() {
        return new CacheConfiguration();
    }

    @Override
    public void setCacheConfiguration(CacheConfiguration cacheCfg) {

    }

    @Override
    public org.geoserver.wms.WMSInfoImpl upgrade() {
        org.geoserver.wms.WMSInfoImpl upgraded = new org.geoserver.wms.WMSInfoImpl();

        //ServiceInfoImpl
        upgraded.setId(this.id);
        upgraded.setWorkspace(this.workspace);
        //Handle modificationProxy
        if (this.workspace != null && Proxy.isProxyClass(this.workspace.getClass())) {
            InvocationHandler modificationProxy = Proxy.getInvocationHandler(this.workspace);
            Object workspace = null;
            if (modificationProxy instanceof ModificationProxy) {
                workspace = ((ModificationProxy)modificationProxy).getProxyObject();
            } else if (modificationProxy instanceof stratus.redis.upgrade.stratus_1_2.ModificationProxy) {
                workspace = ((stratus.redis.upgrade.stratus_1_2.ModificationProxy) modificationProxy).getProxyObject();
            }
            if (workspace != null && workspace instanceof WorkspaceInfo) {
                upgraded.setWorkspace(ModificationProxy.create((WorkspaceInfo) workspace, WorkspaceInfo.class));
            }
        }
        upgraded.setEnabled(this.enabled);
        upgraded.setName(this.name);
        upgraded.setTitle(this.title);
        upgraded.setMaintainer(this.maintainer);
        upgraded.setAbstract(this.abstrct);
        upgraded.setAccessConstraints(this.accessConstraints);
        upgraded.setFees(this.fees);
        upgraded.setVersions(this.versions);
        upgraded.setKeywords(this.keywords);
        upgraded.setExceptionFormats(this.exceptionFormats);
        upgraded.setMetadataLink(this.metadataLink);
        upgraded.setCiteCompliant(this.citeCompliant);
        upgraded.setOnlineResource(this.onlineResource);
        upgraded.setSchemaBaseURL(this.schemaBaseURL);
        upgraded.setVerbose(this.verbose);
        upgraded.setOutputStrategy(this.outputStrategy);
        upgraded.setMetadata(this.metadata);
        upgraded.setClientProperties(this.clientProperties);

        //WMSInfoImpl
        upgraded.setMaxRequestMemory(this.maxRequestMemory);
        upgraded.setWatermark(this.watermark);
        upgraded.setInterpolation(this.interpolation);
        upgraded.setSRS(this.getSRS());
        upgraded.setBBOXForEachCRS(this.bboxForEachCRS);
        upgraded.setMaxBuffer(this.maxBuffer);
        upgraded.setMaxRenderingTime(this.maxRenderingTime);
        upgraded.setMaxRenderingErrors(this.maxRenderingErrors);
        upgraded.setAuthorityURLs(this.authorityURLs);
        upgraded.setIdentifiers(this.identifiers);
        upgraded.setGetFeatureInfoMimeTypes(this.getFeatureInfoMimeTypes);
        upgraded.setGetMapMimeTypes(this.getMapMimeTypes);
        upgraded.setGetFeatureInfoMimeTypeCheckingEnabled(this.getFeatureInfoMimeTypeCheckingEnabled);
        upgraded.setGetMapMimeTypeCheckingEnabled(this.getMapMimeTypeCheckingEnabled);
        upgraded.setRootLayerTitle(this.rootLayerTitle);
        upgraded.setRootLayerAbstract(this.rootLayerAbstract);
        upgraded.setDynamicStylingDisabled(this.dynamicStylingDisabled);
        upgraded.setFeaturesReprojectionDisabled(this.featuresReprojectionDisabled);

        return upgraded;
    }
}