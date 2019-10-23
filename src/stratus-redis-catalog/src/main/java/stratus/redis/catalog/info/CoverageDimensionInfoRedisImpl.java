/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Data;
import org.geoserver.catalog.CoverageDimensionInfo;
import org.geotools.util.NumberRange;
import org.opengis.coverage.SampleDimensionType;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoverageDimensionInfoRedisImpl implements CoverageDimensionInfo {
	
	private static final long serialVersionUID = -1463962678510313395L;

	String id;

    String name;

    String description;

    //must use doubles redis serialisation compatibility (range has not default constructor)
    double rangeMin, rangeMax;

    List<Double> nullValues = new ArrayList<>();
    
    String unit;
    
    //must use string for redis serialisation compatibility (DimensionType has not default constructor)
    String dimensionType;

	@SuppressWarnings("rawtypes")
	@Override
	public NumberRange getRange() {
		return new NumberRange<>(Double.class, rangeMin, true, rangeMax, true);
	}

	@Override
	public void setRange(@SuppressWarnings("rawtypes") NumberRange range) {
		this.rangeMin = range == null ? Double.NEGATIVE_INFINITY : range.getMinimum();
		this.rangeMax = range == null ? Double.POSITIVE_INFINITY : range.getMaximum();
	}

	@Override
	public SampleDimensionType getDimensionType() {
		return dimensionType == null ? null : SampleDimensionType.valueOf(dimensionType);
	}

	@Override
	public void setDimensionType(SampleDimensionType dimensionType) {
		this.dimensionType = dimensionType == null ? null : dimensionType.name();
	}

	

}
