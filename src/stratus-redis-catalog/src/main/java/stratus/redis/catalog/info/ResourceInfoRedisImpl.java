/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.feature.NameImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
public abstract class ResourceInfoRedisImpl implements ResourceInfo, Serializable {

    private static final long serialVersionUID = -375881080019295043L;

    /**
     * HACK - special class to resolve the fact they didn't add a plain constructor to Keyword
     * (is necessary for redis serialisation compatibility)
     *
     * @author Niels Charlier
     */
    protected static class KeywordRedis extends Keyword {
        private static final long serialVersionUID = -4760261416974292807L;

        public KeywordRedis() {
            super(""); //it doesn't want null, hence the lack of default constructor
            //we'll just pass an empty string...
        }

        public KeywordRedis(KeywordInfo keyword) {
            super(keyword.getValue());
            setLanguage(keyword.getLanguage());
            setVocabulary(keyword.getVocabulary());
        }
    }

    @Id
    protected String id;
    @Indexed
    protected String name;
    protected String nativeName;
    @Indexed
    protected String namespaceId;
    @Transient
    protected NamespaceInfo namespace;
    @Transient
    protected transient Catalog catalog;

    protected Map<String, String> serializedMetadata = new HashMap<>();
    @Transient
    protected MetadataMap metadata;

    protected List<String> alias = new ArrayList<>();
    protected String title;
    protected String description;
    protected String _abstract;
    protected List<KeywordInfo> keywords = new ArrayList<>();
    protected List<MetadataLinkInfo> metadataLinks = new ArrayList<>();
    protected List<DataLinkInfo> dataLinks = new ArrayList<>();
    protected String SRS;
    protected ProjectionPolicy projectionPolicy;
    protected boolean enabled;
    protected boolean advertised;
    @Indexed
    protected String storeId;
    private StoreInfo store;

    protected String nativeCRS_wkt;
    protected double[] nativeBoundingBox_coords;
    protected Integer nativeBoundingBox_epsg;
    protected String nativeBoundingBox_wkt;
    protected double[] latLonBoundingBox_coords;
    protected Integer latLonBoundingBox_epsg;
    protected String latLonBoundingBox_wkt;
    @Transient
    protected CoordinateReferenceSystem nativeCRS;
    @Transient
    protected ReferencedEnvelope nativeBoundingBox;
    @Transient
    protected ReferencedEnvelope latLonBoundingBox;

    protected boolean serviceConfiguration = false;

    protected List<String> disabledServices = new ArrayList<>();

    protected ResourceInfoRedisImpl() {
    }

    protected ResourceInfoRedisImpl(Catalog catalog) {
        this.catalog = catalog;
    }

    protected ResourceInfoRedisImpl(Catalog catalog, String id) {
        this(catalog);
        setId(id);
    }

    @Override
    public NamespaceInfo getNamespace() {
        if (namespace == null && namespaceId != null) {
            namespace = ResolvingProxy.create(namespaceId, NamespaceInfo.class);
        }
        return namespace;
    }

    @Override
    public void setNamespace(NamespaceInfo namespace) {
        namespaceId = namespace == null ? null : namespace.getId();
        this.namespace = namespace;
    }

    public void setKeywords(List<KeywordInfo> keywords) {
        this.keywords = new ArrayList<>();
        if (keywords != null) {
            keywords.stream().forEach((kw) -> this.keywords.add(new KeywordRedis(kw)));
        }
    }

    @Override
    public List<KeywordInfo> getKeywords() {
        List<KeywordInfo> convertedKeywords = new ArrayList<>();
        if (keywords != null) {
            keywords.stream().forEach((kw) -> convertedKeywords.add(new Keyword((KeywordRedis) kw)));
        }
        return convertedKeywords;
    }

    @Override
    public CoordinateReferenceSystem getNativeCRS() {
        if (nativeCRS == null && nativeCRS_wkt != null) {
            try {
                nativeCRS = CRS.parseWKT(nativeCRS_wkt);
            } catch (FactoryException e) {
                throw new IllegalStateException(e); //should never happen
            }
        }
        return nativeCRS;
    }

    @Override
    public void setNativeCRS(CoordinateReferenceSystem nativeCRS) {
        this.nativeCRS = nativeCRS;
        this.nativeCRS_wkt = nativeCRS == null ? null : nativeCRS.toWKT();
    }

    protected static ReferencedEnvelope createReferencedEnvelope(double[] coords, Integer epsg, String wkt) {
        if (coords.length != 4) {
            return null;
        }
        CoordinateReferenceSystem crs = null;
        if (epsg != null) {
            try {
                crs = CRS.decode("EPSG:" + epsg);
            } catch (FactoryException e) {
                throw new IllegalStateException(e);
            }
        }
        if (crs == null && wkt != null) {
            try {
                crs = CRS.parseWKT(wkt);
            } catch (FactoryException e) {
                throw new IllegalStateException(e);
            }
        }
        return new ReferencedEnvelope(coords[0], coords[1], coords[2], coords[3], crs);
    }

    protected static double[] getCoords(ReferencedEnvelope referencedEnvelope) {
        if (referencedEnvelope == null) {
            return null;
        }
        return new double[]{
                referencedEnvelope.getMinX(),
                referencedEnvelope.getMaxX(),
                referencedEnvelope.getMinY(),
                referencedEnvelope.getMaxY()
        };
    }

    protected static Integer getEPSG(ReferencedEnvelope referencedEnvelope) {
        if (referencedEnvelope != null && referencedEnvelope.getCoordinateReferenceSystem() != null) {
            try {
                return CRS.lookupEpsgCode(referencedEnvelope.getCoordinateReferenceSystem(), false);
            } catch (FactoryException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    protected static String getWKT(ReferencedEnvelope referencedEnvelope) {
        if (referencedEnvelope != null && referencedEnvelope.getCoordinateReferenceSystem() != null) {
            return referencedEnvelope.getCoordinateReferenceSystem().toWKT();
        }
        return null;
    }

    @Override
    public ReferencedEnvelope getNativeBoundingBox() {
        if (nativeBoundingBox == null && nativeBoundingBox_coords != null) {
            nativeBoundingBox = createReferencedEnvelope(nativeBoundingBox_coords, nativeBoundingBox_epsg, nativeBoundingBox_wkt);
        }
        return nativeBoundingBox;
    }

    @Override
    public void setNativeBoundingBox(ReferencedEnvelope nativeBoundingBox) {
        this.nativeBoundingBox = nativeBoundingBox;
        nativeBoundingBox_coords = getCoords(nativeBoundingBox);
        nativeBoundingBox_epsg = getEPSG(nativeBoundingBox);
        nativeBoundingBox_wkt = getWKT(nativeBoundingBox);
    }

    @Override
    public ReferencedEnvelope getLatLonBoundingBox() {
        if (latLonBoundingBox == null && latLonBoundingBox_coords != null) {
            latLonBoundingBox = createReferencedEnvelope(latLonBoundingBox_coords, latLonBoundingBox_epsg, latLonBoundingBox_wkt);
        }
        return latLonBoundingBox;
    }

    @Override
    public void setLatLonBoundingBox(ReferencedEnvelope latLonBoundingBox) {
        this.latLonBoundingBox = latLonBoundingBox;
        latLonBoundingBox_coords = getCoords(latLonBoundingBox);
        latLonBoundingBox_epsg = getEPSG(latLonBoundingBox);
        latLonBoundingBox_wkt = getWKT(latLonBoundingBox);
    }

    @Override
    public Name getQualifiedName() {
        return new NameImpl(getNamespace().getURI(), getName());
    }

    @Override
    public Name getQualifiedNativeName() {
        return new NameImpl(getNamespace().getURI(), getNativeName());
    }

    @Override
    @Deprecated
    public String getPrefixedName() {
        return this.prefixedName();
    }

    @Override
    public String prefixedName() {
        return getNamespace().getPrefix() + ":" + getName();
    }

    @Override
    public String getAbstract() {
        return this._abstract;
    }

    @Override
    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    @Override
    public List<String> keywordValues() {
        throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public ReferencedEnvelope boundingBox() throws Exception {
        throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public CoordinateReferenceSystem getCRS() {
        throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean enabled() {
        //return enabled;
        StoreInfo store = getStore();
        boolean storeEnabled = store != null && store.isEnabled();
        return storeEnabled && this.isEnabled();
    }

    @Override
    public <T> T getAdapter(Class<T> type, Map<?, ?> map) {
        return null;
    }

    @Override
    public MetadataMap getMetadata() {
        if (serializedMetadata != null && metadata == null) {
            metadata = new MetadataMap();
            for (Map.Entry<String, String> entry : serializedMetadata.entrySet()) {
                metadata.put(entry.getKey(), (Serializable) SerializationUtils.deserialize(Base64.decodeBase64(entry.getValue())));
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

    @Override
    public boolean isServiceConfiguration() {
        return serviceConfiguration;
    }

    @Override
    public void setServiceConfiguration(boolean serviceConfiguration) {
        this.serviceConfiguration = serviceConfiguration;
    }

    @Override
    public List<String> getDisabledServices() {
        return disabledServices;
    }

    @Override
    public void setDisabledServices(List<String> disabledServices) {
        this.disabledServices = disabledServices;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_abstract == null) ? 0 : _abstract.hashCode());
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime
                * result
                + ((latLonBoundingBox == null) ? 0 : latLonBoundingBox
                .hashCode());
        result = prime * result
                + ((metadataLinks == null) ? 0 : metadataLinks.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime
                * result
                + ((nativeBoundingBox == null) ? 0 : nativeBoundingBox
                .hashCode());
        result = prime * result
                + ((nativeCRS == null) ? 0 : nativeCRS.hashCode());
        result = prime * result
                + ((nativeName == null) ? 0 : nativeName.hashCode());
        result = prime
                * result
                + ((projectionPolicy == null) ? 0 : projectionPolicy.hashCode());
        result = prime * result + ((SRS == null) ? 0 : SRS.hashCode());
        result = prime * result + ((storeId == null) ? 0 : storeId.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ResourceInfo))
            return false;

        final ResourceInfo other = (ResourceInfo) obj;
        if (_abstract == null) {
            if (other.getAbstract() != null)
                return false;
        } else if (!_abstract.equals(other.getAbstract()))
            return false;
        if (alias == null) {
            if (other.getAlias() != null)
                return false;
        } else if (!alias.equals(other.getAlias()))
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
        if (keywords == null) {
            if (other.getKeywords() != null)
                return false;
        } else if (!keywords.equals(other.getKeywords()))
            return false;
        if (latLonBoundingBox == null) {
            if (other.getLatLonBoundingBox() != null)
                return false;
        } else if (!latLonBoundingBox.equals(other.getLatLonBoundingBox()))
            return false;
        if (metadataLinks == null) {
            if (other.getMetadataLinks() != null)
                return false;
        } else if (!metadataLinks.equals(other.getMetadataLinks()))
            return false;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        if (namespace == null) {
            if (other.getNamespace() != null)
                return false;
        } else if (!namespace.equals(other.getNamespace()))
            return false;
        if (nativeBoundingBox == null) {
            if (other.getNativeBoundingBox() != null)
                return false;
        } else if (!nativeBoundingBox.equals(other.getNativeBoundingBox()))
            return false;
        if (nativeCRS == null) {
            if (other.getNativeCRS() != null)
                return false;
        } else if (!CRS.equalsIgnoreMetadata(nativeCRS, other.getNativeCRS()))
            return false;
        if (nativeName == null) {
            if (other.getNativeName() != null)
                return false;
        } else if (!nativeName.equals(other.getNativeName()))
            return false;
        if (projectionPolicy == null) {
            if (other.getProjectionPolicy() != null)
                return false;
        } else if (!projectionPolicy.equals(other.getProjectionPolicy()))
            return false;
        if (SRS == null) {
            if (other.getSRS() != null)
                return false;
        } else if (!SRS.equals(other.getSRS()))
            return false;
        if (storeId == null) {
            if (other.getStore() != null)
                return false;
        } else if (other.getStore() == null || !storeId.equals(other.getStore().getId()))
            return false;
        if (title == null) {
            if (other.getTitle() != null)
                return false;
        } else if (!title.equals(other.getTitle()))
            return false;
        return true;
    }
}
