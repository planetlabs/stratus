/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ndvi;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.parameter.GeneralParameterValue;

import java.awt.geom.Point2D;
import java.io.File;

public class NDVIProcessTest {

    @Test
    public void testExecute() throws Exception {
        File testFile = new File("src/test/java/rgbnir_cropped.tiff");
        GeoTiffReader geoTiffReader = new GeoTiffReader(testFile);
        GridCoverage2D coverage = geoTiffReader.read(new GeneralParameterValue[0]);
        NDVIProcess ndviProcess = new NDVIProcess();
        GridCoverage2D execute = ndviProcess.execute(coverage, 0, 3);
        float[] value = execute.evaluate(new Point2D.Double(375239.23, 5476200.95), new float[1]);
        Assert.assertEquals(value[0], 0.07018983f, 0.000000001);
    }

}