.. _cartography.rt.barnes:


Barnes Surface
==============

The Barnes Surface rendering transformation is a **Vector-to-Raster** transformation which computes a interpolated surface across a set of irregular observation points. It is commonly used as an interpolation technique for weather maps and other meteorological datasets. The surface is generated dynamically from the dataset, so it can be used to visualize changing data. The surface view is created by configuring a layer with a style which invokes the Barnes Surface rendering transformation.

.. figure:: img/barnes_surface.png

   Barnes Surface rendering transformation used to render a maximum temperature surface

Technical description
---------------------

The Barnes Surface algorithm operates on a regular grid of cells covering a specified extent in the input data space. It computes an initial pass to produce an interpolated value for each grid cell. The value of a cell is determined by its proximity to the input observation points, using a summation of exponential (Gaussian) decay functions for each observation point. Refinement passes may be used to improve the estimate by reducing the error between the computed surface and the observations.

The rendering transformation uses the Barnes Surface algorithm to compute a surface over a set of irregular data points,
providing a raster surface as output.

The input is a dataset of **points**, with an attribute providing an **observed value** for each point. The radius of influence of each observation point is controlled by the **length scale**. A number of **refinement passes** can be performed to improve the surface estimate, with the degree of refinement controlled by the **convergence factor**.

Usage
-----

As with all rendering transformations, the transformation is invoked by inserting a transform into a style. The style can then be applied to any layer which is backed by a suitable dataset.

The transformation function is called ``vec:BarnesSurface``. Note that this is the same as the WPS process, as these functions can be invoked as either a WPS process or a rendering transformation.

The transformation parameters are as follows. The order of parameters is not significant.

.. list-table::
   :header-rows: 1
   :class: non-responsive
   :widths: 20 10 70

   * - Name
     - Required?
     - Description
   * - ``valueAttr``
     - Yes
     - Name of the value attribute
   * - ``dataLimit``
     - No
     - Limits the number of input points which are processed
   * - ``scale``
     - Yes
     - Length scale for the interpolation. In units of the input data CRS.
   * - ``convergence``
     - No
     - Convergence factor for refinement. Values can be between 0 and 1. Values below 0.4 are safest. (Default = 0.3)
   * - ``passes``
     - No
     - Number of passes to compute. Values can be 1 or greater. (Default = 2)
   * - ``minObservations``
     - No
     - Minimum number of observations required to support a grid cell. (Default = 2)
   * - ``maxObservationDistance``
     - No
     - Maximum distance (in units of the input data CRS) to an observation for it to support a grid cell. A value of 0 means all observations are used (Default = 0).
   * - ``noDataValue``
     - No
     - The NO_DATA value to use for unsupported grid cells in the output.
   * - ``pixelsPerCell``
     - No
     - Resolution of the computed grid. Larger values improve performance, but may degrade appearance if too large. (Default = 1)
   * - ``queryBuffer``
     - No
     - Distance to expand the query envelope by. Larger values provide a more stable surface. In units of the input data CRS. fault = 0)
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

The Barnes Surface rendering transformation is applied to a **vector** input dataset with point geometries. The dataset is supplied in the ``data`` parameter, while the observation value for features is supplied in the attribute named in the ``valueAttr`` parameter.

To prevent extrapolation into areas unsupported by observations, the influence of observation points can be limited using the ``minObservations`` and ``maxObservationDistance`` parameters. This also increases performance by reducing the observations evaluated for each grid cell. Uncomputed grid cells are given the value ``noDataValue``.

To ensure the computed surface is stable under panning and zooming the extent for the input data can be expanded by a user-specified distance (``queryBuffer``). This ensures enough data points are included to avoid edge effects on the computed surface. The expansion distance depends on the length scale, convergence factor, and data spacing in a complex way, so must be manually determined. (A good heuristic is to set the distance at least as large as the length scale.)

To prevent excessive CPU consumption the number of data points processed can be limited using the ``dataLimit`` parameter. If the limit is exceeded an output is still produced using the maximum number of points.

To improve performance, the surface grid can be computed at lower resolution than the output raster, using the ``pixelsPerCell`` parameter. The computed grid is upsampled to the output raster size using *Bilinear Interpolation with Edge Smoothing* to maintain quality. There is minimal impact on appearance for small cell sizes (10 pixels or less).

The surface is computed in the CRS (coordinate reference system) of the output. If the output CRS is different to the input CRS the data points are transformed into the output CRS. Likewise, the distance-based parameters ``scale`` and ``maxObservationDistance`` are converted into the units of the output CRS.

Output
------

The output of the transformation is a single-band **raster**. Each pixel has a floating-point value in the range [0..1] measuring the density of the pixel relative to the rest of the surface. The raster can be styled using a raster symbolizer.

.. note::

   In order for the style to be correctly validated in SLD, the input geometry element must be declared in the raster symbolizer:

   .. code-block:: xml

      <Geometry>
        <ogc:PropertyName>...</ogc:PropertyName>
      </Geometry>

Examples
--------

This example shows a temperature surface interpolated across a set of data points with a attribute giving the maximum daily temperature on a given day. It shows the generated Barnes Surface, the original input data points (drawn by another style), as well as a base map layer.

The source data used in this example is the ``world:globaldata_temp`` layer.

Below is an example showing how to perform this rendering transformation in SLD. You can adapt the example to your data with minimal effort by adjusting the parameters.

SLD
^^^

The surface layer can be produced by the following SLD

.. literalinclude:: artifact/barnes_example.sld
   :language: xml
   :linenos:
   :emphasize-lines: 15,17,20-21,24-25,28-29,32-33,36-37,40-41,44-45,48-49,52-55,58-61,64-67,74-77

In the SLD, **Lines 14-70** define the Barnes surface rendering transformation, giving values for the transformation parameters which are appropriate for the input dataset.

* **Line 15** specifes the name of the rendering transformation (``vec:BarnesSurface``).
* **Line 17** specifies the input dataset parameter name.
* **Lines 20-21** specifies the name of the observation value attribute ``valueattr`` to be ``value``.
* **Lines 24-25** sets a length ``scale`` of 15 degrees.
* **Lines 28-29** sets the ``convergence`` factor to be 0.2.
* **Lines 32-33** requests that 3 ``passes`` be performed (one for the initial estimate, and two refinement passes).
* **Lines 36-37** specifies that the minimum number of observations (``minObservations``) required to support an estimated cell is 1 (which means every observation point will be represented in the output).
* **Lines 40-41** specifies the maximum distance from a computed grid cell to an observation point (``maxObservationDistance``) is 10 degrees.
* **Lines 44-45** defines the resolution of computation to be 10 pixels per cell (``pixelsPerCell``), which provides efficient rendering time while still providing output of reasonable visual quality.
* **Lines 48-49** specifies the query buffer (``queryBuffer``) to be 40 degrees, which is chosen to be at least double the length scale for stability.
* **Lines 51-68** define the output parameters (``outputBBOX``, ``outputWidth``, and ``outputHeight``), which are obtained from internal environment variables set during rendering, as described above.
* **Lines 72-96** define the symbolizer used to style the raster computed by the transformation (``RasterSymbolizer``).
* **Line 74** defines the geometry property of the input dataset, which is required for SLD validation purposes.
* **Line 75** specifies an overall opacity of 0.8 for the rendered layer.
* **Lines 76-95** define a color map with which to symbolize the output raster. In this case the color map uses a **type** of ``ramp``, which produces a smooth transition between colors. The type could also be ``intervals``, which produces a contour effect with discrete transition between colors (see image above).
* **Line 77** specifies that the NO_DATA value of -990 should be displayed with a fully transparent color of white (masking uncomputed pixels).

.. note:: :download:`Download the SLD for this example <artifact/barnes_example.sld>`
