/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.redis.data;

import com.google.common.collect.ImmutableSet;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geoserver.gwc.layer.GeoServerTileLayerInfoImpl;
import org.geoserver.gwc.layer.StyleParameterFilter;
import org.geotools.util.logging.Logging;
import org.geowebcache.config.XMLGridSubset;
import org.geowebcache.filter.parameters.ParameterFilter;
import org.geowebcache.filter.parameters.RegexParameterFilter;
import org.geowebcache.filter.parameters.StringParameterFilter;
import org.geowebcache.filter.request.RequestFilter;
import org.geowebcache.layer.ExpirationRule;
import org.geowebcache.layer.meta.LayerMetaInformation;
import org.geowebcache.layer.updatesource.UpdateSourceDefinition;
import org.geowebcache.mime.FormatModifier;
import org.geowebcache.util.GWCVars;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;

/**
 * A redis implementation of {@link org.geoserver.gwc.layer.GeoServerTileLayerInfoImpl} that adds @Transient
 * annotations to ensure spring data honors transient fields.
 *
 * Should only be used when (de)serializing to/from redis
 *
 * @author joshfix
 * Created on 5/18/18
 */
@RedisHash("GeoServerTileLayer")
public class GeoServerTileLayerRedisInfoImpl implements GeoServerTileLayerInfo, Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 8277055420849712230L;

    private static final Logger LOGGER = Logging.getLogger(GeoServerTileLayerInfoImpl.class);

    private String id;

    // // AbstractTileLayer mirror properties ////

    private boolean enabled;

    private Boolean inMemoryCached;

    @Id
    private String name;

    private String blobStoreId;

    // the object that gets serialized
    @XStreamAlias("parameterFilters")
    @AccessType(AccessType.Type.PROPERTY)
    private Set<ParameterFilter> parameterFilters = new HashSet();

    private Set<String> mimeFormats;

    private int[] metaWidthHeight;

    /**
     * @see GWCVars#CACHE_DISABLE_CACHE
     * @see GWCVars#CACHE_NEVER_EXPIRE
     * @see GWCVars#CACHE_USE_WMS_BACKEND_VALUE
     * @see GWCVars#CACHE_VALUE_UNSET
     */
    private int expireCache;

    private List<ExpirationRule> expireCacheList;

    private int expireClients;

    @SuppressWarnings("unused")
    private List<FormatModifier> formatModifiers;

    private Set<XMLGridSubset> gridSubsets;

    @SuppressWarnings("unused")
    @Transient
    transient private LayerMetaInformation metaInformation;

    @SuppressWarnings("unused")
    @Transient
    transient private List<? extends UpdateSourceDefinition> updateSources;

    @SuppressWarnings("unused")
    @Transient
    transient private List<? extends RequestFilter> requestFilters;

    @SuppressWarnings("unused")
    @Transient
    transient private boolean useETags;

    @SuppressWarnings("unused")
    @Transient
    transient private List<ExpirationRule> expireClientsList;

    @SuppressWarnings("unused")
    @Transient
    transient private Integer backendTimeout;

    @SuppressWarnings("unused")
    @Transient
    transient private Boolean cacheBypassAllowed;

    @SuppressWarnings("unused")
    @Transient
    transient private Boolean queryable;

    // in-memory storage
    @Transient
    transient private Map<String, ParameterFilter> parameterFiltersMap;

    // //// GeoServerTileLayer specific properties //////
    private int gutter;

    // For backward compatibility with 2.2 and 2.3
    // FIXME  need to hide this when serializing back out
    private Boolean autoCacheStyles;

    public GeoServerTileLayerRedisInfoImpl() {
        readResolve();
    }

    /**
     * XStream initialization of unset fields
     *
     * @return {@code this}
     */
    private final Object readResolve() {
        if (null == metaWidthHeight) {
            metaWidthHeight = new int[2];
        }
        gridSubsets = nonNull(gridSubsets);
        mimeFormats = nonNull(mimeFormats);

        // Convert the deserialized set into a map.
        parameterFilters = nonNull(parameterFilters);
        setParameterFilters(parameterFilters);

        // Apply the old autoCacheStyles flag if it was specified.
        if(autoCacheStyles!=null){
            if(autoCacheStyles) {
                if(!isAutoCacheStyles()){
                    addParameterFilter(new StyleParameterFilter());
                }
            } else {
                if(isAutoCacheStyles()){
                    this.removeParameterFilter("STYLES");
                }
            }
            autoCacheStyles = null;
        }
        return this;
    }

    private final Object writeReplace() {
        parameterFilters = getParameterFilters();
        return this;
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public GeoServerTileLayerRedisInfoImpl clone() {
        GeoServerTileLayerRedisInfoImpl clone;
        try {
            clone = (GeoServerTileLayerRedisInfoImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw propagate(e);
        }
        clone.metaWidthHeight = metaWidthHeight.clone();
        clone.gridSubsets = nonNull((Set<XMLGridSubset>)null);
        for (XMLGridSubset gs : gridSubsets) {
            clone.gridSubsets.add(gs.clone());
        }
        clone.mimeFormats = nonNull((Set<String>)null);
        clone.mimeFormats.addAll(mimeFormats);
        clone.parameterFiltersMap = nonNull((Map<String, ParameterFilter>)null);
        for (ParameterFilter pf : parameterFiltersMap.values()) {
            clone.addParameterFilter(pf.clone());
        }
        return clone;
    }

    private <T> Set<T> nonNull(Set<T> set) {
        return set == null ? new HashSet<T>() : set;
    }
    private <K,T> Map<K, T> nonNull(Map<K,T> set) {
        return set == null ? new HashMap<K,T>() : set;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String getBlobStoreId() {
        return blobStoreId;
    }

    @Override
    public void setBlobStoreId(@Nullable String blobStoreId) {
        this.blobStoreId = blobStoreId;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#getMetaTilingX()
     */
    @Override
    public int getMetaTilingX() {
        return metaWidthHeight[0];
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#getMetaTilingY()
     */
    @Override
    public int getMetaTilingY() {
        return metaWidthHeight[1];
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#setMetaTilingY(int)
     */
    @Override
    public void setMetaTilingY(int metaTilingY) {
        checkArgument(metaTilingY > 0);
        metaWidthHeight[1] = metaTilingY;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#setMetaTilingX(int)
     */
    @Override
    public void setMetaTilingX(int metaTilingX) {
        checkArgument(metaTilingX > 0);
        metaWidthHeight[0] = metaTilingX;
    }

    @Override
    public int getExpireCache() {
        return this.expireCache;
    }

    @Override
    public void setExpireCache(int expireCache) {
        this.expireCache = expireCache;
    }

    @Override
    public List<ExpirationRule> getExpireCacheList() {
        return expireCacheList;
    }

    @Override
    public void setExpireCacheList(List<ExpirationRule> expireCacheList) {
        this.expireCacheList = expireCacheList;
    }

    @Override
    public int getExpireClients() {
        return expireClients;
    }

    @Override
    public void setExpireClients(int seconds) {
        this.expireClients = seconds;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#cachedStyles()
     */
    @Override
    public ImmutableSet<String> cachedStyles() {
        ParameterFilter styleQualifier = getParameterFilter("STYLES");
        try {
            if (styleQualifier != null) {
                List<String> styles = styleQualifier.getLegalValues();
                if(styles!=null) {
                    return ImmutableSet.copyOf(styles);
                }
            }
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.WARNING, "StyleParameterFilter was not initialized properly", ex);
        }
        return ImmutableSet.of();
    }

    @Override
    public Set<String> getMimeFormats() {
        return mimeFormats;
    }

    public void setMimeFormats(Set<String> mimeFormats) {
        this.mimeFormats = mimeFormats;
    }

    @Override
    public Set<XMLGridSubset> getGridSubsets() {
        return gridSubsets;
    }

    @Override
    public void setGridSubsets(Set<XMLGridSubset> gridSubsets) {
        this.gridSubsets = gridSubsets;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setGutter(int gutter) {
        this.gutter = gutter;
    }

    @Override
    public int getGutter() {
        return gutter;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#isAutoCacheStyles()
     */
    @Override
    public boolean isAutoCacheStyles() {
        ParameterFilter filter = getParameterFilter("STYLES");
        return filter!=null && filter instanceof StyleParameterFilter &&
                ((StyleParameterFilter)filter).getStyles()==null;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#setAutoCacheStyles(boolean)
     * @deprecated
     */
    @Override
    public void setAutoCacheStyles(boolean autoCacheStyles) {
        if(autoCacheStyles){
            // Add a default StyleParameterFilter.
            ParameterFilter newFilter = new StyleParameterFilter();
            addParameterFilter(newFilter);
        } else {
            ParameterFilter filter = getParameterFilter("STYLES");
            if(filter!=null && filter instanceof StyleParameterFilter){
                parameterFilters.remove(filter);
            }
        }
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#setParameterFilters(Set)
     */
    @Override
    public void setParameterFilters(Set<ParameterFilter> parameterFilters) {
        this.parameterFiltersMap = new HashMap<>();
        this.parameterFilters = new HashSet<>();
        for (ParameterFilter pf: parameterFilters){
            addParameterFilter(pf);
        }
    }


    @Override
    public boolean addParameterFilter(ParameterFilter parameterFilter) {
        //convert regular parameter filters to redis ones, as applicable
        if (parameterFilter instanceof RegexParameterFilter) {
            parameterFilters.add(regexParameterFilterToRedis(parameterFilter));
        } else if (parameterFilter instanceof StringParameterFilter) {
            parameterFilters.add(stringParameterFilterToRedis(parameterFilter));
        } else{
            parameterFilters.add(parameterFilter);
        }
        return parameterFiltersMap.put(parameterFilter.getKey().toUpperCase(), parameterFilter) != null;
    }

    @Override
    public boolean removeParameterFilter(String key) {
        return parameterFiltersMap.remove(key.toUpperCase()) !=null;
    }

    @Override
    public ParameterFilter getParameterFilter(String key) {
        return parameterFiltersMap.get(key.toUpperCase());
    }

    private static RegexParameterFilterRedis regexParameterFilterToRedis(RegexParameterFilter parameterFilter) {
        RegexParameterFilterRedis redisFilter = new RegexParameterFilterRedis();
        BeanUtils.copyProperties(parameterFilter, redisFilter);
        return redisFilter;
    }

    private static RegexParameterFilterRedis regexParameterFilterToRedis(ParameterFilter parameterFilter) {
        if (parameterFilter instanceof RegexParameterFilter) {
            return regexParameterFilterToRedis((RegexParameterFilter) parameterFilter);
        }
        return null;
    }

    private static StringParameterFilterRedis stringParameterFilterToRedis(StringParameterFilter parameterFilter) {
        StringParameterFilterRedis redisFilter = new StringParameterFilterRedis();
        BeanUtils.copyProperties(parameterFilter, redisFilter);
        return redisFilter;
    }

    private static StringParameterFilterRedis stringParameterFilterToRedis(ParameterFilter parameterFilter) {
        if (parameterFilter instanceof StringParameterFilter) {
            return stringParameterFilterToRedis((StringParameterFilter) parameterFilter);
        }
        return null;
    }

    /**
     * @see org.geoserver.gwc.layer.GeoServerTileLayerInfo#getParameterFilters()
     */
    @Override
    public Set<ParameterFilter> getParameterFilters() {
        return parameterFilters;
    }

    /**
     * Returns a copy of this objects data as an instance of {@link GeoServerTileLayerInfoImpl}.
     * Converts any custom implementations of parameter filters as part of this process.
     *
     * @return {@link GeoServerTileLayerInfoImpl} for use with GeoServer
     */
    public GeoServerTileLayerInfoImpl geoServerTileLayerInfo() {
        GeoServerTileLayerInfoImpl tileLayerInfo = new GeoServerTileLayerInfoImpl();
        BeanUtils.copyProperties(this, tileLayerInfo);
        tileLayerInfo.getMimeFormats().clear();
        tileLayerInfo.getMimeFormats().addAll(this.getMimeFormats());
        //convert parameter filters
        Set<ParameterFilter> parameterFilters = new HashSet<>();
        for (ParameterFilter parameterFilter : getParameterFilters()) {
            // we need to save our custom RegexParameterFilterRedis classes so that we don't persist the Pattern field
            if (parameterFilter instanceof RegexParameterFilterRedis) {
                RegexParameterFilter regexParameterFilter = new RegexParameterFilter();
                BeanUtils.copyProperties(parameterFilter, regexParameterFilter);
                parameterFilters.add(regexParameterFilter);
            } else if (parameterFilter instanceof StringParameterFilterRedis) {
                StringParameterFilter stringParameterFilter = new StringParameterFilter();
                BeanUtils.copyProperties(parameterFilter, stringParameterFilter);
                parameterFilters.add(stringParameterFilter);
            } else{
                parameterFilters.add(parameterFilter);
            }
        }
        tileLayerInfo.setParameterFilters(parameterFilters);
        return tileLayerInfo;
    }

    @Override
    public boolean isInMemoryCached() {
        return inMemoryCached != null ? inMemoryCached : true;
    }

    @Override
    public void setInMemoryCached(boolean inMemoryCached) {
        this.inMemoryCached = inMemoryCached;
    }

}