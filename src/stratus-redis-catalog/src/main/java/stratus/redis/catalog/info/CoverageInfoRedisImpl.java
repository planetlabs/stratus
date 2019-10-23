/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.CoverageDimensionImpl;
import org.geoserver.catalog.impl.ResolvingProxy;
import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.geotools.util.factory.Hints;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.ProgressListener;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by joshfix on 9/20/16.
 */
@Getter
@Setter
@ToString
@RedisHash("CoverageInfo")
public class CoverageInfoRedisImpl extends ResourceInfoRedisImpl implements CoverageInfo {
    private static final long serialVersionUID = 659498790758954330L;
    
    private String nativeFormat;
    private Map<String, Object> gridProps;
    @Transient private transient GridGeometry grid;    
    private List<String> supportedFormats = new ArrayList<>();
    private List<String> interpolationMethods = new ArrayList<>();
    private String defaultInterpolationMethod;
    private List<CoverageDimensionInfo> dimensions = new ArrayList<>();
    private List<String> requestSRS = new ArrayList<>();
    private List<String> responseSRS = new ArrayList<>();
    private Map<String, Serializable> parameters = new HashMap<>();
    private String nativeCoverageName;
    @Transient private transient CoverageStoreInfo store;
    
    @Override 
    public GridGeometry getGrid() {
        if (grid == null && gridProps != null
                && gridProps.containsKey("low_x") && gridProps.containsKey("low_y")
                && gridProps.containsKey("high_x") && gridProps.containsKey("high_y")
                && gridProps.containsKey("sx") && gridProps.containsKey("sy")
                && gridProps.containsKey("shx") && gridProps.containsKey("shy")
                && gridProps.containsKey("tx") && gridProps.containsKey("ty")
                && gridProps.containsKey("epsg")) {

            int lowx = (int) gridProps.get("low_x");
            int lowy = (int) gridProps.get("low_y");
            int highx = (int) gridProps.get("high_x");
            int highy = (int) gridProps.get("high_y");
            GeneralGridEnvelope gridRange = new GeneralGridEnvelope(new int[] {lowx, lowy}, new int[] {highx, highy});
            
            double sx = (double) gridProps.get("sx");
            double sy = (double) gridProps.get("sy");
            double shx = (double) gridProps.get("shx");
            double shy = (double) gridProps.get("shy");
            double tx = (double) gridProps.get("tx");
            double ty = (double) gridProps.get("ty");         
            AffineTransform2D gridToCRS = new AffineTransform2D(sx, shx, shy, sy, tx, ty);
            
            int epsg = (int) gridProps.get("epsg");
            CoordinateReferenceSystem crs = null;
            try {
                crs = CRS.decode("EPSG:" + epsg);
            } catch (FactoryException e) {
                throw new IllegalStateException(e);
            }
            
            grid = new GridGeometry2D(gridRange, gridToCRS, crs);
        }
        return grid;
    }
    
    @Override
    public void setGrid(GridGeometry grid) {
    	this.grid = grid;
    	
    	if (grid != null) {
	    	gridProps = new HashMap<>();
	    	gridProps.put("low_x", grid.getGridRange().getLow().getCoordinateValues()[0]);
	    	gridProps.put("low_y", grid.getGridRange().getLow().getCoordinateValues()[1]);
	    	gridProps.put("high_x", grid.getGridRange().getHigh().getCoordinateValues()[0]);
	    	gridProps.put("high_y", grid.getGridRange().getHigh().getCoordinateValues()[1]);
	
	    	MathTransform tx = grid.getGridToCRS();
	    	if (tx instanceof AffineTransform) {
	    		AffineTransform atx = (AffineTransform) tx;    		
		    	gridProps.put("sx", atx.getScaleX());
		    	gridProps.put("sy", atx.getScaleY());
		    	gridProps.put("shx", atx.getShearX());
		    	gridProps.put("shy", atx.getShearY());
		    	gridProps.put("tx", atx.getTranslateX());
		    	gridProps.put("ty", atx.getTranslateY());
	    	} else {		
		    	gridProps.put("sx", 1.0);
		    	gridProps.put("sy", 1.0);
		    	gridProps.put("shx", 0.0);
		    	gridProps.put("shy", 0.0);
		    	gridProps.put("tx", 0.0);
		    	gridProps.put("ty", 0.0);
	    	}
	    	
	    	try {
				gridProps.put("epsg", CRS.lookupEpsgCode(((GridGeometry2D )grid).getCoordinateReferenceSystem(), false));
		    } catch (FactoryException e) {
				throw new IllegalStateException(e);
			}
    	} else {
    		gridProps = null;
    	}
    }   
    
    @Override
    public List<CoverageDimensionInfo> getDimensions() {
    	List<CoverageDimensionInfo> convertedDimensions = new ArrayList<>();
        if (dimensions != null) {
        	dimensions.stream().forEach((di) -> {
        		CoverageDimensionInfo convertedDi = new CoverageDimensionImpl();
        		BeanUtils.copyProperties(di, convertedDi);
            	convertedDimensions.add(convertedDi);
            });
        }
        return convertedDimensions;
	}

	public void setDimensions(List<CoverageDimensionInfo> dimensions) {
		List<CoverageDimensionInfo> convertedDimensions = new ArrayList<>();
        if (dimensions != null) {
        	dimensions.stream().forEach((di) -> {
        		CoverageDimensionInfo convertedDi = new CoverageDimensionInfoRedisImpl();
        		BeanUtils.copyProperties(di, convertedDi);
            	convertedDimensions.add(convertedDi);
            });
        }
        this.dimensions = convertedDimensions;
	}

	public void setParameters(Map<String, Serializable> parameters) {
    	this.parameters = parameters;    	
    	this.parameters.values().removeIf(Objects::isNull);
    }

    @Override
    public CoverageStoreInfo getStore() {
    	if (store == null && storeId != null) {
    		store = ResolvingProxy.create(storeId, CoverageStoreInfo.class);
    	}
        return store;
    }

    @Override
    public void setStore(StoreInfo store) {
    	if (store instanceof CoverageStoreInfo) {
    		storeId = store.getId();
    		this.store = (CoverageStoreInfo) store;
    	} else {
    		throw new IllegalArgumentException();
    	}
    }

    @Override
    public GridCoverage getGridCoverage(ProgressListener pl, Hints hints) throws IOException {
        // manage projection policy
        if (this.getProjectionPolicy() == ProjectionPolicy.FORCE_DECLARED){
            final Hints crsHints= new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.getCRS());
            if(hints!=null)
                hints.putAll(crsHints);
            else
                hints=crsHints;
        }        
        return catalog.getResourcePool().getGridCoverage(this, null, hints); 
    }

    @Override
    public GridCoverage getGridCoverage(ProgressListener pl, ReferencedEnvelope envelope, Hints hints) throws IOException {
        // manage projection policy
        if (this.getProjectionPolicy() == ProjectionPolicy.FORCE_DECLARED){
            final Hints crsHints= new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.getCRS());
            if(hints!=null)
                hints.putAll(crsHints);
            else
                hints=crsHints;
        }           
        return catalog.getResourcePool().getGridCoverage(this, envelope, hints);
    }

    @Override
    public GridCoverageReader getGridCoverageReader(ProgressListener pl, Hints hints) throws IOException {
        // manage projection policy
        if (this.getProjectionPolicy() == ProjectionPolicy.FORCE_DECLARED){
            final Hints crsHints= new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.getCRS());
            if(hints!=null)
                hints.putAll(crsHints);
            else
                hints=crsHints;
        }
        return catalog.getResourcePool().getGridCoverageReader(this, nativeCoverageName, hints);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((defaultInterpolationMethod == null) ? 0
                        : defaultInterpolationMethod.hashCode());
        result = prime * result
                + ((dimensions == null) ? 0 : dimensions.hashCode());
        result = prime * result + ((grid == null) ? 0 : grid.hashCode());
        result = prime
                * result
                + ((interpolationMethods == null) ? 0 : interpolationMethods
                        .hashCode());
        result = prime * result
                + ((nativeFormat == null) ? 0 : nativeFormat.hashCode());
        result = prime * result
                + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result
                + ((requestSRS == null) ? 0 : requestSRS.hashCode());
        result = prime * result
                + ((responseSRS == null) ? 0 : responseSRS.hashCode());
        result = prime
                * result
                + ((supportedFormats == null) ? 0 : supportedFormats.hashCode());
        result = prime
                * result
                + ((nativeCoverageName == null) ? 0 : nativeCoverageName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !( obj instanceof CoverageInfo ) ) {
            return false;
        }
        if ( !super.equals( obj ) ) {
            return false;
        }
        
        final CoverageInfo other = (CoverageInfo) obj;
        if (defaultInterpolationMethod == null) {
            if (other.getDefaultInterpolationMethod() != null)
                return false;
        } else if (!defaultInterpolationMethod
                .equals(other.getDefaultInterpolationMethod()))
            return false;
        if (dimensions == null) {
            if (other.getDimensions() != null)
                return false;
        } else if (!dimensions.equals(other.getDimensions()))
            return false;
        if (grid == null) {
            if (other.getGrid() != null)
                return false;
        } else if (!grid.equals(other.getGrid()))
            return false;
        if (interpolationMethods == null) {
            if (other.getInterpolationMethods() != null)
                return false;
        } else if (!interpolationMethods.equals(other.getInterpolationMethods()))
            return false;
        if (nativeFormat == null) {
            if (other.getNativeFormat() != null)
                return false;
        } else if (!nativeFormat.equals(other.getNativeFormat()))
            return false;
        if (parameters == null) {
            if (other.getParameters() != null)
                return false;
        } else if (!parameters.equals(other.getParameters()))
            return false;
        if (requestSRS == null) {
            if (other.getRequestSRS() != null)
                return false;
        } else if (!requestSRS.equals(other.getRequestSRS()))
            return false;
        if (responseSRS == null) {
            if (other.getResponseSRS() != null)
                return false;
        } else if (!responseSRS.equals(other.getResponseSRS()))
            return false;
        if (supportedFormats == null) {
            if (other.getSupportedFormats() != null)
                return false;
        } else if (!supportedFormats.equals(other.getSupportedFormats()))
            return false;
        if (nativeCoverageName == null) {
            if (other.getNativeCoverageName() != null)
                return false;
        } else if (!nativeCoverageName.equals(other.getNativeCoverageName()))
            return false;
        return true;
    }

    @Override
    public void accept(CatalogVisitor visitor) {
        visitor.visit(this);
    }
}
