/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.ndvi;

import it.geosolutions.jaiext.JAIExt;
import it.geosolutions.jaiext.algebra.AlgebraDescriptor;
import it.geosolutions.jaiext.range.Range;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.image.ImageWorker;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.raster.RasterProcess;

import javax.media.jai.JAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

/**
 * Computes the NDVI
 *
 * <p>
 *
 * <p>(NIR - Red) / (NIR + Red)
 */
@DescribeProcess(title = "NDVI", description = "NDVI process on the given coverage")
public class NDVIProcess implements RasterProcess {

  @DescribeResult(name = "result", description = "A NDVI calculation on the input coverage")
  public GridCoverage2D execute(
      @DescribeParameter(name = "coverage", description = "Input GridCoverage", min = 1)
          GridCoverage2D coverage,
      @DescribeParameter(
            name = "redBand",
            description = "Red band to index, defaults to 0",
            min = 0,
            defaultValue = "0"
          )
          Integer redIndex,
      @DescribeParameter(
            name = "nirBand",
            description = "Nir band index, defaults to 3",
            min = 0,
            defaultValue = "3"
          )
          Integer nirIndex)
      throws ProcessException, IOException {

    int redBand = redIndex == null ? 0 : redIndex;
    int nirBand = nirIndex == null ? 3 : nirIndex;

    ImageWorker red =
        new ImageWorker(coverage.getRenderedImage())
            .retainBands(new int[] {redBand})
            .format(DataBuffer.TYPE_FLOAT);
    ImageWorker nir =
        new ImageWorker(coverage.getRenderedImage())
            .retainBands(new int[] {nirBand})
            .format(DataBuffer.TYPE_FLOAT);

    ImageWorker nirMinusRed =
        new ImageWorker(nir.getRenderedOperation()).subtract(red.getRenderedOperation());
    ImageWorker nirPlusRed =
        new ImageWorker(nir.getRenderedOperation()).addImage(red.getRenderedOperation());
    RenderedOp ndvi = divide(nirMinusRed, nirPlusRed);

    GridCoverageFactory coverageFactory = new GridCoverageFactory();
    GridCoverage2D ndviCoverage =
        coverageFactory.create("NDVIProcess", ndvi, new ReferencedEnvelope(coverage.getEnvelope()));

    return ndviCoverage;
  }

  private RenderedOp divide(ImageWorker nirMinusRed, ImageWorker nirPlusRed) {
    ParameterBlock pb = new ParameterBlock();
    pb.setSource(nirMinusRed.getRenderedOperation(), 0);
    pb.setSource(nirPlusRed.getRenderedOperation(), 1);
    RenderedOp image;
    if (JAIExt.isJAIExtOperation("algebric")) {
      prepareAlgebricOperation(
          AlgebraDescriptor.Operator.DIVIDE,
          pb,
          nirMinusRed.getROI(),
          nirMinusRed.getNoData(),
          true);
      image = JAI.create("algebric", pb, nirMinusRed.getRenderingHints());
    } else {
      image = JAI.create("Divide", pb, nirMinusRed.getRenderingHints());
    }
    return image;
  }

  private void prepareAlgebricOperation(
      AlgebraDescriptor.Operator op,
      ParameterBlock pb,
      ROI roi,
      Range nodata,
      boolean setDestNoData) {
    pb.set(op, 0);
    pb.set(roi, 1);
    pb.set(nodata, 2);
  }
}
