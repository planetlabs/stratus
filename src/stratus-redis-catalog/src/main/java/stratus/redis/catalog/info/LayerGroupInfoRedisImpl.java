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
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshfix on 9/23/16.
 */
@Getter
@Setter
@ToString
@RedisHash("LayerGroupInfo")
public class LayerGroupInfoRedisImpl implements LayerGroupInfo, Serializable {

	private static final long serialVersionUID = -63628964358392607L;
	
	@Id private String id;
    @Indexed private String name;
    
    private Mode mode = Mode.SINGLE;
    private Boolean queryDisabled;
    private List<MetadataLinkInfo> metadataLinks = new ArrayList<>();
    private MetadataMap metadata = new MetadataMap();
    private AttributionInfo attribution;    
    private String title;  
    private String abstractTxt; 
    private List<AuthorityURLInfo> authorityURLs = new ArrayList<>(2);
    private List<LayerIdentifierInfo> identifiers = new ArrayList<>(2);
    
    protected double[] bounds_coords;
    protected Integer bounds_epsg;
    protected String bounds_wkt;
    @Transient protected ReferencedEnvelope bounds; 
    
    @Indexed private String workspaceId;    
    @Transient private transient WorkspaceInfo workspace;
    
    private String rootLayerId;
    @Transient private transient LayerInfo rootLayer;
    
    private String rootLayerStyleId;    
    @Transient private transient StyleInfo rootLayerStyle;
    
    private List<String> layerIds = new ArrayList<>();
    private List<String> layerGroupIds = new ArrayList<>();    
    @Transient private List<PublishedInfo> layers;
    
    private List<String> styleIds = new ArrayList<>();
    @Transient private transient List<StyleInfo> styles;
    
    @Transient transient Catalog catalog;

    private List<KeywordInfo> keywords;

    public static final String TITLE_KEY = "title";
    public static final String NULL_VALUE = "null";

    public void setKeywords(List<KeywordInfo> keywords) {
        this.keywords = new ArrayList<>();
        if (keywords != null) {
            keywords.stream().forEach((kw) -> this.keywords.add(new ResourceInfoRedisImpl.KeywordRedis(kw)));
        }
    }

    @Override
    public List<KeywordInfo> getKeywords() {
        List<KeywordInfo> convertedKeywords = new ArrayList<>();
        if (keywords != null) {
            keywords.stream().forEach((kw) -> convertedKeywords.add(new Keyword((ResourceInfoRedisImpl.KeywordRedis) kw)));
        }
        return convertedKeywords;
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
    public StyleInfo getRootLayerStyle() {
        if (rootLayerStyle == null && rootLayerStyleId != null) {
        	rootLayerStyle = ResolvingProxy.create(rootLayerStyleId, StyleInfo.class);
        }
        return rootLayerStyle;
    }

    @Override
    public void setRootLayerStyle(StyleInfo style) {
        this.rootLayerStyle = style;
        rootLayerStyleId = style == null ? null : style.getId();
    }

    @Override
    public List<PublishedInfo> getLayers() {
        if (layers == null && layerIds != null) {
            layers = new ArrayList<>();
            for (String layerId : layerIds) {
                if (NULL_VALUE.equals(layerId)) {
                    layers.add(null);
                } else if (layerId.startsWith("LayerGroup")) {
                    layers.add(ResolvingProxy.create(layerId, LayerGroupInfo.class));
                } else {
                    layers.add(ResolvingProxy.create(layerId, LayerInfo.class));
                }
            }
        }
        return layers;
    }

    public void setLayers(List<PublishedInfo> layers) {
        this.layers = layers;
        layerIds = new ArrayList<>();
        layerGroupIds = new ArrayList<>();
        for (PublishedInfo layer : layers) {
            if (layer != null) {
                layerIds.add(layer.getId());
            } else {
                layerIds.add(NULL_VALUE);
            }
        }
    }
    
    @Override
    public List<StyleInfo> getStyles() {
    	if (styles == null && styleIds != null) {
    		styles = new ArrayList<>();
    		for (String styleId : styleIds) {
                if (NULL_VALUE.equals(styleId)) {
                    styles.add(null);
                } else {
                    styles.add(ResolvingProxy.create(styleId, StyleInfo.class));
                }
    		}
        }
        return styles;
    }
    
    public void setStyles(List<StyleInfo> styles) {
        this.styles = styles;
        styleIds = new ArrayList<>();
		for (StyleInfo style : styles) {
			if (style != null) {
				styleIds.add(style.getId());
			} else {
                styleIds.add(NULL_VALUE);
            }
		}
    }
    
    @Override
    public LayerInfo getRootLayer() {
    	if (rootLayer == null && rootLayerId != null) {
    		rootLayer = ResolvingProxy.create(rootLayerId, LayerInfo.class);
    	}
        return rootLayer;
    }

    @Override
    public void setRootLayer(LayerInfo rootLayer) {
    	this.rootLayer = rootLayer;
    	rootLayerId = rootLayer == null? null : rootLayer.getId();
    }
    
    @Override
    public ReferencedEnvelope getBounds() {
		if (bounds == null && bounds_coords != null) {
			bounds = ResourceInfoRedisImpl.createReferencedEnvelope(bounds_coords, bounds_epsg, bounds_wkt);
    	}
		return bounds;
	}

    @Override
	public void setBounds(ReferencedEnvelope bounds) {
		this.bounds = bounds;
		bounds_coords = ResourceInfoRedisImpl.getCoords(bounds);
		bounds_epsg = ResourceInfoRedisImpl.getEPSG(bounds);
		bounds_wkt = ResourceInfoRedisImpl.getWKT(bounds);
	}

    @Override
    public String getTitle() {
        if(title == null && metadata != null) {
            title = metadata.get(TITLE_KEY, String.class);
        }
        return title;
    }

    @Override
    public String prefixedName() {
        return workspace != null ? workspace.getName() + ":" + name : name;
    }

    @Override
    @Deprecated
    public String getPrefixedName() {
        return prefixedName();
    }

    @Override
    public List<LayerInfo> layers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StyleInfo> styles() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public PublishedType getType() {
        return PublishedType.GROUP;
    }
    
    @Override
    public String getAbstract() {
        if(abstractTxt == null && metadata != null) {
            abstractTxt = metadata.get(TITLE_KEY, String.class);
        }
        return abstractTxt;
    }
    
    @Override
    public void setAbstract(String abstractTxt) {
        this.abstractTxt = abstractTxt;
    }
    
    @Override
    public boolean isQueryDisabled() {
        return queryDisabled != null ? queryDisabled : false;
    }
    
    @Override
    public void setQueryDisabled(boolean queryDisabled) {
        this.queryDisabled = queryDisabled ? Boolean.TRUE : null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bounds == null) ? 0 : bounds.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((layers == null) ? 0 : layers.hashCode());
        result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((abstractTxt == null) ? 0 : abstractTxt.hashCode());
        result = prime * result + ((workspace == null) ? 0 : workspace.hashCode());
        result = prime * result + ((styles == null) ? 0 : styles.hashCode());
        result = prime * result + ((rootLayer == null) ? 0 : rootLayer.hashCode());
        result = prime * result + ((rootLayerStyle == null) ? 0 : rootLayerStyle.hashCode());        
        result = prime * result + ((authorityURLs == null) ? 0 : authorityURLs.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
        result = prime * result + ((attribution == null) ? 0 : attribution.hashCode());
        result = prime * result + ((metadataLinks == null) ? 0 : metadataLinks.hashCode());
        result = prime * result + ((queryDisabled == null) ? 0 : queryDisabled.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!( obj instanceof LayerGroupInfo) ) 
            return false;
        LayerGroupInfo other = (LayerGroupInfo) obj;
        if (bounds == null) {
            if (other.getBounds() != null)
                return false;
        } else if (!bounds.equals(other.getBounds()))
            return false;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!id.equals(other.getId()))
            return false;
        if (layers == null) {
            if (other.getLayers() != null)
                return false;
        } else if (!layers.equals(other.getLayers()))
            return false;
        if (metadata == null) {
            if (other.getMetadata() != null)
                return false;
        } else if (!metadata.equals(other.getMetadata()))
            return false;
        if (name == null) {
            if (other.getName() != null)
                return false;
        } else if (!name.equals(other.getName()))
            return false;
        if (mode == null) {
            if (other.getMode() != null)
                return false;
        } else if (!mode.equals(other.getMode()))
            return false;        
        if (title == null) {
            if (other.getTitle() != null) {
                return false;
            }
        } else if (!title.equals(other.getTitle())) 
            return false;
        if (abstractTxt == null) {
            if (other.getAbstract() != null) {
                return false;
            }
        } else if (!abstractTxt.equals(other.getAbstract())) 
            return false;        
        if (workspace == null) {
            if (other.getWorkspace() != null)
                return false;
        } else if (!workspace.equals(other.getWorkspace()))
            return false;
        if (styles == null) {
            if (other.getStyles() != null)
                return false;
        } else if (!styles.equals(other.getStyles()))
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

        if(rootLayer == null){
            if (other.getRootLayer() != null)
                return false;
        } else if (!rootLayer.equals(other.getRootLayer()))
            return false;
        
        if(rootLayerStyle == null){
            if (other.getRootLayerStyle() != null)
                return false;
        } else if (!rootLayerStyle.equals(other.getRootLayerStyle()))
            return false;
        
        if(attribution == null){
            if (other.getAttribution() != null)
                return false;
        } else if (!attribution.equals(other.getAttribution()))
            return false;
        
        if(metadataLinks == null){
            if (other.getMetadataLinks() != null)
                return false;
        } else if (!metadataLinks.equals(other.getMetadataLinks()))
            return false;
        
        if (queryDisabled == null) {
            if (other.isQueryDisabled())
                return false;
        } else if (!queryDisabled.equals(other.isQueryDisabled()))
            return false;
        
        return true;
    }
}
