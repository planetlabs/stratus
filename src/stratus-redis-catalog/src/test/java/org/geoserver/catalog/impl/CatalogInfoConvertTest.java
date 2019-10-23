/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.catalog.impl;

import stratus.redis.catalog.impl.CatalogInfoConvert;
import stratus.redis.catalog.info.FeatureTypeInfoRedisImpl;
import stratus.redis.catalog.info.LayerGroupInfoRedisImpl;
import stratus.redis.catalog.info.ResourceInfoRedisImpl;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.MetadataMap;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.RegexpValidator;
import org.geotools.jdbc.VirtualTable;
import org.geotools.jdbc.VirtualTableParameter;
import org.geotools.jdbc.VirtualTableParameter.Validator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.locationtech.jts.geom.MultiPoint;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for conversion between the geoserver and redis implementations of various
 * {@link org.geoserver.catalog.CatalogInfo} interfaces.
 *
 * Created by tbarsballe on 2016-11-15.
 */
public class CatalogInfoConvertTest {

    /**
     * Regression test to verify that null styles are preserved when converting from {@link LayerGroupInfoImpl} to
     * {@link LayerGroupInfoRedisImpl} and back.
     */
    @Test
    public void testConvertLayerGroup() {
        LayerGroupInfo traditional = new LayerGroupInfoImpl();
        traditional.setName("testLg");
        traditional.getStyles().add(null);
        LayerGroupInfo toRedis = CatalogInfoConvert.toRedis(traditional);
        LayerGroupInfo toTraditional = CatalogInfoConvert.toTraditional(toRedis);

        assertEquals(1, toTraditional.getStyles().size());
    }

    /**
     * Regression test to verify that referenced envelopes using DefaultGeographicCRS.WGS84 are preserved when converting {@link FeatureTypeInfoImpl} to
     * {@link FeatureTypeInfoRedisImpl} and back.
     */
    @Test
    public void testConvertFeatureType() throws NoSuchFieldException, IllegalAccessException {
        FeatureTypeInfo traditional = new FeatureTypeInfoImpl();
        traditional.setName("testFt");
        traditional.setStore(new DataStoreInfoImpl());
        traditional.setLatLonBoundingBox(new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84));

        FeatureTypeInfo toRedis = CatalogInfoConvert.toRedis(traditional);

        //Reflectively null Transient latLonBoundingBox field to simulate catalog load
        Field bboxField = ResourceInfoRedisImpl.class.getDeclaredField("latLonBoundingBox");
        bboxField.setAccessible(true);
        bboxField.set(toRedis, null);
        FeatureTypeInfo toTraditional = CatalogInfoConvert.toTraditional(toRedis);

        assertNotNull(toTraditional.getLatLonBoundingBox().getCoordinateReferenceSystem());
    }

    @Test
    public void testConvertFeatureTypeWithVirtualTable() throws NoSuchFieldException, IllegalAccessException {
        FeatureTypeInfo traditional = new FeatureTypeInfoImpl();
        traditional.setName("testFtVt");
        traditional.setStore(new DataStoreInfoImpl());
        traditional.setLatLonBoundingBox(new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84));

        VirtualTable vt = new VirtualTable("view", "select id, geom from places where name = '%name%'");
        vt.addGeometryMetadatata("geom", MultiPoint.class, 26986);
        vt.setPrimaryKeyColumns(Collections.singletonList("id"));
        Validator paramValidator = new RegexpValidator(".*");
        vt.addParameter(new VirtualTableParameter("name", "Boston", paramValidator));
        MetadataMap metadata = traditional.getMetadata();
        metadata.put(FeatureTypeInfo.JDBC_VIRTUAL_TABLE, vt);

        FeatureTypeInfo toRedis = CatalogInfoConvert.toRedis(traditional);
        
        assertEquals(traditional.getMetadata(), toRedis.getMetadata());
    }
}
