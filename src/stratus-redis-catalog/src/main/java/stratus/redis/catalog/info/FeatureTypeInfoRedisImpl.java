/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtils;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.AttributeTypeInfoImpl;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.data.FeatureSource;
import org.geotools.data.util.MeasureConverterFactory;
import org.geotools.measure.Measure;
import org.geotools.util.factory.Hints;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.util.ProgressListener;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("FeatureTypeInfo")
public class FeatureTypeInfoRedisImpl extends ResourceInfoRedisImpl implements FeatureTypeInfo {

	private static final long serialVersionUID = 8871944587482214973L;
	
    @Transient protected transient Catalog catalog;
    private String cqlFilter;
    private int maxFeatures;
    private int numDecimals;
    private Boolean padWithZeros;
    private Boolean forcedDecimal;
    private List<AttributeTypeInfoRedisImpl> attributes = new ArrayList<>();    
    private List<String> responseSRS = new ArrayList<>();
    private boolean overridingServiceSRS;
    private boolean skipNumberMatched = false;
    private boolean circularArcPresent;
    private String linearizationTolerance;
    @Transient private transient DataStoreInfo store;

    @Override 
    public List<AttributeTypeInfo> getAttributes() {
    	List<AttributeTypeInfo> convertedAttributes = new ArrayList<>();
    	
    	for (AttributeTypeInfo origin : attributes) {
    		AttributeTypeInfoImpl dest = new AttributeTypeInfoImpl();
    		try {
				BeanUtils.copyProperties(dest, origin);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
    		convertedAttributes.add(dest);
    	}
    	
    	return convertedAttributes;
    }
    
    public void setAttributes(List<AttributeTypeInfo> attributes) {
    	this.attributes = new ArrayList<>();
    	
    	for (AttributeTypeInfo origin : attributes) {
    		AttributeTypeInfoRedisImpl dest = new AttributeTypeInfoRedisImpl();
    		try {
				BeanUtils.copyProperties(dest, origin);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new IllegalStateException(e);
			}
    		this.attributes.add(dest);
    	}
    }

    @Override
    public DataStoreInfo getStore() {
    	if (store == null && storeId != null) {
    		store = ResolvingProxy.create(storeId, DataStoreInfo.class);
    	}
        return store;
    }

    @Override
    public void setStore(StoreInfo store) {
    	if (store instanceof DataStoreInfo) {
    		storeId = store.getId();
    		this.store = (DataStoreInfo) store;
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @Override
    public Filter filter() {
    	throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public boolean getSkipNumberMatched() {
        return this.skipNumberMatched;
    }

    @Override
    public List<AttributeTypeInfo> attributes() throws IOException {
    	throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public FeatureType getFeatureType() throws IOException {
    	throw new UnsupportedOperationException(); //no need to implement
    }

    @Override
    public FeatureSource<? extends FeatureType, ? extends Feature> getFeatureSource(ProgressListener pl, Hints hints) throws IOException {
        return catalog.getResourcePool().getFeatureSource( this, hints );
    }
    
    @Override
    public Measure getLinearizationTolerance() {
        try {
            return MeasureConverterFactory.CONVERTER.convert(linearizationTolerance, Measure.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not convert '"+linearizationTolerance+"' to measure",e);
        }
    }
    
    @Override
    public void setLinearizationTolerance(Measure tolerance) {
        try {
            linearizationTolerance = MeasureConverterFactory.CONVERTER.convert(tolerance, String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert '"+tolerance+"' to string",e);
        }
    }

    @Override
    public boolean getPadWithZeros() {
        return padWithZeros == null ? false : padWithZeros;
    }

    @Override
    public boolean getForcedDecimal() {
        return forcedDecimal == null ? false : forcedDecimal;
    }

    @Override
    public void setPadWithZeros(boolean padWithZeros) {
        this.padWithZeros = padWithZeros;
    }

    @Override
    public void setForcedDecimal(boolean forcedDecimal) {
        this.forcedDecimal = forcedDecimal;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((cqlFilter == null) ? 0 : cqlFilter.hashCode());
        result = prime * result + maxFeatures;
        result = prime * result + numDecimals;
        result = prime * result + (overridingServiceSRS ? 1231 : 1237);
        result = prime * result + ((responseSRS == null) ? 0 : responseSRS.hashCode());
        result = prime * result + (skipNumberMatched ? 2845 : 3984);
        return result;
    }

    /*
     * Mind, this method cannot be auto-generated, it has to compare against the interface,
     * not the implementation
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof FeatureTypeInfo)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        final FeatureTypeInfo other = (FeatureTypeInfo) obj;
        if (attributes == null) {
            if (other.getAttributes() != null)
                return false;
        } else if (!attributes.equals(other.getAttributes()))
            return false;
        if (responseSRS == null) {
            if (other.getResponseSRS() != null)
                return false;
        } else if (!responseSRS.equals(other.getResponseSRS()))
            return false;
        if (circularArcPresent != other.isCircularArcPresent())
            return false;
        if (linearizationTolerance == null) {
            if (other.getLinearizationTolerance() != null)
                return false;
        } else if (!linearizationTolerance.equals(other.getLinearizationTolerance()))
            return false;
        if (maxFeatures != other.getMaxFeatures())
            return false;
        if (numDecimals != other.getNumDecimals())
            return false;
        if (overridingServiceSRS != other.isOverridingServiceSRS())
            return false;
        if (skipNumberMatched != other.getSkipNumberMatched())
            return false;
        if (cqlFilter == null) {
            if (other.getCqlFilter() != null)
                return false;
        } else if (!cqlFilter.equals(other.getCqlFilter()))
            return false;

        return true;
    }
    
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }
}
