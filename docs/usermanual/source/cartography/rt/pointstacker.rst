.. _cartography.rt.pointstacker:

Point Stacker
=============

The Point Stacker rendering transformation is a **Vector-to-Vector** transformation which displays a dataset with features that occur close together aggregated into a single point. This produces a more readable map in situations when there are many points to display. As the stacking is performed dynamically, it can be used to visualize changing data, and does not incur a performance overhead even when applied to very large datasets.

The stacked view is created by configuring a layer with an style which invokes the PointStacker rendering transformation.

.. figure:: img/pointstacker-volcanoes.png

   Point Stacker rendering transformation

Usage
-----

As with all rendering transformations, the transformation is invoked by inserting a transform into a style. The style can then be applied to any layer which is backed by a suitable dataset.

The transformation function is called ``vec:PointStacker``. Note that this is the same as the WPS process, as these functions can be invoked as either a WPS process or a rendering transformation.

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
   * - ``cellSize``
     - No
     - Size of the cells in which to aggregate points (in pixels, default = 1)
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

The PointStacker rendering transformation is applied to a dataset containing **vector** features. The features may be of any type, though point geometries are typically expected. If non-point geometries are used, the centroids of the features will be used. The dataset is supplied in the ``data`` parameter.


Output
------

The output of the transformation is a **vector** layer containing point features. Each feature has the following attributes:

.. list-table::
   :header-rows: 1
   :class: non-responsive
   :widths: 20 20 60

   * - Name
     - Type
     - Description
   * - ``geom``
     - Point
     - Point geometry representing the group of features
   * - ``count``
     - Integer
     - Count of all input features represented by this point
   * - ``countUnique``
     - Integer
     - Count of all different input points represented by this point

The output can be styled using a point symbolizer.


Example
-------

This example shows point stacking applied to a dataset of world volcanoes, displayed with a base map layer of continental topography.

The source data used in this example is the ``world:volcanoes`` layer.

Below is an example showing how to perform this rendering transformation in SLD. You can adapt this example to your data with minimal effort by adjusting the parameters.

SLD
^^^

The PointStacker output can be produced by the following SLD:

.. literalinclude:: artifact/pointstacker_example.sld
   :language: xml
   :linenos:
   :emphasize-lines: 18,21,25,31,37,45,66,121

* **Lines 15-43** define the Point Stacker rendering transformation, providing values for the transformation parameters which are appropriate for the input dataset.
* **Line 18** specifies the input dataset parameter name.
* **Line 21** specifies a cell size of 30 to aggregate the points by.
* **Lines 24-42** define the output parameters **outputBBOX**, **outputWith** and **outputHeight**, which are obtained from internal environment variables set during rendering, as described above.

* **Lines 44-169** define styling rules which are applied to the transformation output to produce the rendered layer.
* **Lines 44-64** define a rule **rule1** for depicting a single (unstacked) point using a red triangle symbol.
* **Lines 65-119** define a rule **rule29** for depicting a stacked point which has a count in the range 2 to 9. The points are styled as dark red circles of size 14 pixels, with a label showing the count inside them.
* **Lines 120-169** define a rule **rule10** for depicting a stacked point which has a count of 10 or greater. The points are styled as dark red circles of size 22 pixels, with a label that includes the point count.

.. note:: :download:`Download the SLD for this example <artifact/pointstacker_example.sld>`
