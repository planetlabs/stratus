.. _cartography.rt.heatmap:

Heatmap
=======

The Heatmap rendering transformation is a **Vector-to-Raster** transformation which displays a dataset as a heatmap surface (also known as a "density surface"). The heatmap surface is generated dynamically, so it can be used to visualize dynamic data. It can be applied to very large datasets with good performance.

The heatmap view is created by configuring a layer with a style which invokes the Heatmap rendering transformation.

This tutorial will show how to create a dynamic heatmap using rendering transformations.

.. figure:: img/heatmap_urban_us_east.png

   Heatmap rendering transformation

Usage
-----

As with all rendering transformations, the transformation is invoked by inserting a transform into a style. The style can then be applied to any layer which is backed by a suitable dataset. The dataset may have a weight attribute, whose name is supplied to the process via the ``weightAttr`` process parameter.

The transformation function is called ``vec:Heatmap``. Note that this is the same as the WPS process, as these functions can be invoked as either a WPS process or a rendering transformation.

The transformation parameters are as follows. The order of parameters is not significant.

.. list-table::
   :header-rows: 1
   :class: non-responsive
   :widths: 20 10 70

   * - Name
     - Required?
     - Description
   * - ``data``
     - Yes
     - Input FeatureCollection containing the features to transform
   * - ``radiusPixels``
     - Yes
     - Radius of the density kernel (in pixels)
   * - ``weightAttr``
     - No
     - Name of the weight attribute. (Default = 1)
   * - ``pixelsPerCell``
     - No
     - Resolution of the computed grid (Default = 1). Larger values improve performance, but may degrade appearance if too large.
   * - ``outputBBOX``
     - Yes
     - Georeferenced bounding box of the output.
   * - ``outputWidth``
     - Yes
     - Output image width.
   * - ``outputHeight``
     - Yes
     - Output image height.

.. include:: include/envvars.txt

Input
-----

The Heatmap rendering transformation is applied to an input dataset containing **vector** features. The features may be of any type, though point geometries are typically expected. If non-point geometries are used, the centroids of the features will be used. The dataset is supplied in the ``data`` parameter.

In addition, features can optionally be weighted by supplying an attribute name in the ``weightAttr`` parameter. The value of the attribute is used to weight the influence of each point feature.

Output
------

The output of the transformation is a single-band **raster**. Each pixel has a floating-point value in the range [0..1] measuring the density of the pixel relative to the rest of the surface. The generated raster can be styled using a standard raster symbolizer.

In order for the style to be correctly validated, the input geometry element must be declared in the raster symbolizer:

For SLD:

.. code-block:: xml

   <Geometry>
     <ogc:PropertyName>...</ogc:PropertyName>
   </Geometry>

Examples
--------

The source data used in this example is derived from public domain data obtained from the `Natural Earth <http://www.naturalearthdata.com/>`_ website.

Below is an example showing how to perform this rendering transformation in SLD. You can adapt this example to your data with minimal effort by adjusting the parameters.

SLD
^^^

The heatmap surface can be produced by the following SLD:

.. literalinclude:: artifact/heatmap_example.sld
   :language: xml
   :linenos:
   :emphasize-lines: 17,21,27,32,57,58,59-65,60-61

In the SLD **lines 14-53** define the Heatmap rendering transformation, giving values for the transformation parameters which are appropriate for the input dataset.

* **Line 17** specifies the input dataset parameter name.
* **Line 21** specifies the dataset attribute which provides a weighting for the input points.
* **Line 27** specifies a kernel density radius of 100 pixels.
* **Line 32** defines the resolution of computation to be 10 pixels per cell, which provides efficient rendering time while still providing output of reasonable visual quality.
* **Lines 34-52** define the output parameters, which are obtained from internal environment variables set during rendering, as described above.
* **Lines 55-66** define the symbolizer used to style the raster computed by the transformation.
* **Line 57** defines the geometry property of the input dataset, which is required for SLD validation purposes.
* **Line 58** specifies an overall opacity of 0.6 for the rendered layer.
* **Lines 59-65** define a color map with which to symbolize the output raster. The color map uses a **type** of ``ramp``, which produces a smooth transition between colors. **Lines 60-61** specify that raster values of between 0 and 0.02 should be displayed with a fully transparent color of white, which makes areas where there no influence from data points invisible.

.. note:: :download:`Download the SLD for this example <artifact/heatmap_example.sld>`
