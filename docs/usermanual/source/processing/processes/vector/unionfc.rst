.. _processing.processes.vector.unionfc:

UnionFeatureCollection
======================

Description
-----------

The ``vec:UnionFeatureCollection`` process is similar to a merge operation. It takes as input two feature collections and outputs a single feature collection, containing all features and attributes. This is useful for combining similar sets of features, such as layers that cover different geographic areas, but otherwise contain the same type of information.


.. figure:: img/unionfc.png

   *vec:UnionFeatureCollection*

Inputs and outputs
------------------

``vec:UnionFeatureCollection`` accepts :ref:`processing.processes.formats.fcin` and returns :ref:`processing.processes.formats.fcout`.

Inputs
~~~~~~

.. tabularcolumns:: |p{3cm}|p{4cm}|p{4cm}|p{4cm}|
.. list-table::
   :header-rows: 1

   * - Name
     - Description
     - Type
     - Usage
   * - ``first feature collection``
     - First feature collection
     - :ref:`SimpleFeatureCollection <processing.processes.formats.fcin>`
     - Required
   * - ``second feature collection``
     - Second feature collection
     - :ref:`SimpleFeatureCollection <processing.processes.formats.fcin>`
     - Required

Outputs
~~~~~~~

.. list-table::
   :header-rows: 1

   * - Name
     - Description
     - Type
   * - ``result``
     - Output feature collection
     - :ref:`SimpleFeatureCollection <processing.processes.formats.fcout>`


Usage notes
-----------

* Although this process only accepts two inputs, it is possible to chain this process together with itself to combine more than two feature collections.
* Both input feature collections must have the same default geometry.
* The :term:`CRS` of each input feature collection must be the same, as mismatched coordinate systems may produce unexpected output.
* The :term:`CRS` of the first input feature collection is used for the output feature collection.
* Identical features in both input collections will both be preserved as individual features, and will not be combined.
* The attributes list in the output will be a union of the input attributes. If one of the input features doesn't have a particular attribute present in the other input features, the attribute value will be left blank.
* If two fields with the same name exist in both input feature collections, only one attribute with that name will be added to the output feature collection, and values taken from both input feature collections.* If attributes with the same name have different types in each input feature collection, an attribute of type ``String`` will be added to the output feature collection, and the string representation of values from the input feature collections will be used.

.. todo:: Example needed


Related processes
-----------------

* The :ref:`vec:IntersectionFeatureCollection <processing.processes.vector.intersectionfc>` process performs an intersection operation on two feature collections instead of a merge operation.

