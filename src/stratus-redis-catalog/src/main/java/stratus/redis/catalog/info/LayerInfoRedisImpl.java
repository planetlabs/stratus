/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by joshfix on 9/22/16.
 */
@Data
@Slf4j
@RedisHash("LayerInfo")
public class LayerInfoRedisImpl implements LayerInfo, Serializable {

	private static final long serialVersionUID = 6136423748206359744L;

	static final String KEY_ADVERTISED = "advertised";
    
    //for backwards compatibility
    @Transient protected transient String name; 
    @Transient protected transient boolean enabled;
    @Transient protected transient boolean advertised;

    @Id
    protected String id;

    private String abstractTxt;

    protected String path;

    protected PublishedType type;
    
    protected String defaultStyleId;

    @Transient
    protected StyleInfo defaultStyle;
    
    @Indexed
    protected Set<String> styleIds = new HashSet<>();

    @Transient
    protected Set<StyleInfo> styles;
    
    @Indexed
    protected String resourceId;

    @Transient
    protected ResourceInfo resource;

    protected LegendInfo legend;

    protected Boolean queryable;

    protected MetadataMap metadata = new MetadataMap();

    protected AttributionInfo attribution;

    protected WMSInterpolation defaultWMSInterpolationMethod;

    @Override
    public StyleInfo getDefaultStyle() {
        if (defaultStyle == null && defaultStyleId != null) {
        	defaultStyle = ResolvingProxy.create(defaultStyleId, StyleInfo.class);
        }
        return defaultStyle;
    }

    @Override
    public ResourceInfo getResource() {
    	if (resource == null && resourceId != null) {
    		resource = ResolvingProxy.create(resourceId, ResourceInfo.class);
        }
        return resource;
    }
    
    @Override
    public Set<StyleInfo> getStyles() {
    	if (styles == null) {
    		styles = new HashSet<>();
    		for (String styleId : styleIds) {
    			styles.add(ResolvingProxy.create(styleId, StyleInfo.class));
    		}
        }
        return styles;
    }

    @Override
    public void setDefaultStyle(StyleInfo style) {
        this.defaultStyle = style;
        defaultStyleId = style == null ? null : style.getId();
    }

    @Override
    public void setResource(ResourceInfo resource) {
        this.resource = resource;
        resourceId = resource == null ? null : resource.getId();
    }
    
    public void setStyles(Set<StyleInfo> styles) {
        this.styles = styles;
        styleIds = new HashSet<>();
		for (StyleInfo style : styles) {
			if (style != null) {
				styleIds.add(style.getId());
			}
		}
    }

    @Override
    public void setName(String name) {
        this.name = name;
        if (resource == null) {
            log.warn("Attempting to set the name \"" + name + "\" on a layer's resource, but the resource is null.");
        } else {
            resource.setName(name);
        }
    }

    @Override
    public String getName() {
        if (resource == null) {
            return name;
        }
        return resource.getName();
    }

    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "authorityURLs", and a not transient in the 2.2.x series.
     *
     * @since 2.1.3
     */
    protected List<AuthorityURLInfo> authorityURLs = new ArrayList<>(1);


    /**
     * This property is transient in 2.1.x series and stored under the metadata map with key
     * "identifiers", and a not transient in the 2.2.x series.
     *
     * @since 2.1.3
     */
    protected List<LayerIdentifierInfo> identifiers = new ArrayList<>(1);

    protected Boolean opaque;

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    @Override
    public boolean isQueryable() {
        return this.queryable == null || this.queryable;
    }

    @Override
    public void setOpaque(boolean opaque) {
        this.opaque = opaque;
    }

    @Override
    public boolean isOpaque() {
        return this.opaque != null && this.opaque;
    }

    @Override
    @Deprecated
    public String getPrefixedName() {
        return prefixedName();
    }

    @Override
    public String prefixedName() {
        return this.getResource().getStore().getWorkspace().getName() + ":" + getName();
    }

    @Override
    public String getTitle() {
        return resource.getTitle();
    }

    @Override
    public void setTitle(String title) {
        this.resource.setTitle(title);
    }

    @Override
    public String getAbstract() {
        return abstractTxt;
    }

    @Override
    public void setAbstract(String abstractTxt) {
        this.abstractTxt = abstractTxt;
    }

	@Override
	public boolean enabled() {
		return enabled;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((defaultStyle == null) ? 0 : defaultStyle.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((legend == null) ? 0 : legend.hashCode());
        // TODO: add back when resource publish split is in place
        // result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result
                + ((resource == null) ? 0 : resource.hashCode());
        result = prime * result + ((styles == null) ? 0 : styles.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((attribution == null) ? 0 : attribution.hashCode());
        result = prime * result + ((authorityURLs == null) ? 0 : authorityURLs.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof LayerInfo))
            return false;
        final LayerInfo other = (LayerInfo) obj;
        if (defaultStyle == null) {
            if (other.getDefaultStyle() != null)
                return false;
        } else if (!defaultStyle.equals(other.getDefaultStyle()))
            return false;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (legend == null) {
            if (other.getLegend() != null)
                return false;
        } else if (!legend.equals(other.getLegend()))
            return false;
        // TODO: add back when resource/publish split is in place
//        if (name == null) {
//            if (other.getName() != null)
//                return false;
//        } else if (!name.equals(other.getName()))
//            return false;
        if (path == null) {
            if (other.getPath() != null)
                return false;
        } else if (!path.equals(other.getPath()))
            return false;
        if (resource == null) {
            if (other.getResource() != null)
                return false;
        } else if (!resource.equals(other.getResource()))
            return false;
        if (styles == null) {
            if (other.getStyles() != null)
                return false;
        } else if (!styles.equals(other.getStyles()))
            return false;
        if (type == null) {
            if (other.getType() != null)
                return false;
        } else if (!type.equals(other.getType()))
            return false;
        if (attribution == null) {
            if (other.getAttribution() != null)
                return false;
        } else if (!attribution.equals(other.getAttribution()))
            return false;
        if(authorityURLs == null){
            if (other.getAuthorityURLs() != null)
                return false;
        } else if (!authorityURLs.equals(other.getAuthorityURLs()))
            return false;

        if(identifiers == null){
            if (other.getIdentifiers() != null)
                return false;
        } else if (!identifiers.equals(other.getIdentifiers()))
            return false;

        return true;
    }

}
