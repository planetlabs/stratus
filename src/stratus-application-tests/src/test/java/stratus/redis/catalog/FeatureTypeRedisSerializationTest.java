/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.FeatureTypeInfo;
import org.geotools.measure.Measure;
import org.junit.Ignore;
import org.junit.Test;
import si.uom.NonSI;
import si.uom.SI;
import tec.uom.se.AbstractUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test that a FeatureTypeInfo serializes to and deserializes from Redis correctly.
 * @author Kevin Smith
 *
 */
public class FeatureTypeRedisSerializationTest extends AbstractRedisCatalogTest {

    @Test
    public void testNullLinearizationTolerance() {
        FeatureTypeInfo newFt = catalog.getFactory().createFeatureType();
        newFt.setEnabled(true);
        newFt.setName( "ftName2" );
        newFt.setAbstract( "ftAbstract" );
        newFt.setDescription( "ftDescription" );
        newFt.setStore( ds );
        newFt.setNamespace( ns );
        newFt.setLinearizationTolerance(null);
        
        this.addDataStore();
        this.addNamespace();
        this.catalog.add(newFt);
        assertThat(catalog.getFeatureType(newFt.getId()), hasProperty("linearizationTolerance", nullValue()));
    }
    
    @Test
    public void testDistanceLinearizationTolerance() {
        FeatureTypeInfo newFt = catalog.getFactory().createFeatureType();
        newFt.setEnabled(true);
        newFt.setName( "ftName2" );
        newFt.setAbstract( "ftAbstract" );
        newFt.setDescription( "ftDescription" );
        newFt.setStore( ds );
        newFt.setNamespace( ns );

        newFt.setLinearizationTolerance(new Measure(42, SI.METRE));
        
        this.addDataStore();
        this.addNamespace();
        this.catalog.add(newFt);
        assertThat(catalog.getFeatureType(newFt.getId()), hasProperty("linearizationTolerance", equalTo(new Measure(42, SI.METRE))));
    }
    
    @Test
    public void testAngleLinearizationTolerance() {
        FeatureTypeInfo newFt = catalog.getFactory().createFeatureType();
        newFt.setEnabled(true);
        newFt.setName( "ftName2" );
        newFt.setAbstract( "ftAbstract" );
        newFt.setDescription( "ftDescription" );
        newFt.setStore( ds );
        newFt.setNamespace( ns );
        newFt.setLinearizationTolerance(new Measure(42, NonSI.MINUTE_ANGLE));
        
        this.addDataStore();
        this.addNamespace();
        this.catalog.add(newFt);
        assertThat(catalog.getFeatureType(newFt.getId()), hasProperty("linearizationTolerance", equalTo(new Measure(42, NonSI.MINUTE_ANGLE))));
    }
    
    @Test
    @Ignore // org.geotools.util.MeasureConverterFactory.CONVERTER does not handle round trip for Unit.ONE
    public void testDimensionlessLinearizationTolerance() {
        FeatureTypeInfo newFt = catalog.getFactory().createFeatureType();
        newFt.setEnabled(true);
        newFt.setName( "ftName2" );
        newFt.setAbstract( "ftAbstract" );
        newFt.setDescription( "ftDescription" );
        newFt.setStore( ds );
        newFt.setNamespace( ns );
        newFt.setLinearizationTolerance(new Measure(42, AbstractUnit.ONE));
        
        this.addDataStore();
        this.addNamespace();
        this.catalog.add(newFt);
        assertThat(catalog.getFeatureType(newFt.getId()), hasProperty("linearizationTolerance", equalTo(new Measure(42, AbstractUnit.ONE))));
    }
    
    @Test
    public void testDimensionlessNullUnitLinearizationTolerance() {
        FeatureTypeInfo newFt = catalog.getFactory().createFeatureType();
        newFt.setEnabled(true);
        newFt.setName( "ftName2" );
        newFt.setAbstract( "ftAbstract" );
        newFt.setDescription( "ftDescription" );
        newFt.setStore( ds );
        newFt.setNamespace( ns );
        newFt.setLinearizationTolerance(new Measure(42, null));
        
        this.addDataStore();
        this.addNamespace();
        this.catalog.add(newFt);
        assertThat(catalog.getFeatureType(newFt.getId()), hasProperty("linearizationTolerance", equalTo(new Measure(42, null))));
    }
}
