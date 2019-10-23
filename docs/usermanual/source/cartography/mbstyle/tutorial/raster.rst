.. _cartography.mbstyle.tutorial.raster:

Styling a raster layer
======================

Raster layers are composed of a grid of values in one or more bands. A grayscale image will have a single band, with each grid element containing the intensity at that pixel. An RGB image will have 3 bands, corresponding to red, green, and blue values. A raster can have any number of bands.

This raster layer has a single band. With the default `raster layer <https://www.mapbox.com/mapbox-gl-js/style-spec/#layers-raster>`_, it is drawn as a grayscale image.

Creating a new style
--------------------

#. Navigate to the GeoServer Styles list. Click the ``Add a new style`` option.

   Name this new style ``mbraster`` and set the format to ``MBStyle``.

   Under the ``Generate a default style`` option, select ``Raster`` and click the ``Generate`` link to create a default raster style.

.. TODO: If generate works, add instructions for generating a new MBStyle, else provide one we can paste.

   Click the ``Apply`` button, then navigate to the ``Layer Preview`` tab and select the ``dem`` layer to preview the style.

   .. figure:: ../../ysld/tutorial/img/raster_default.png

      Default line style

   .. note:: Your default color may vary.

#. The style will look something like this:

   .. code-block:: json

      {
        "version": 8,
        "layers": [
            {
                "type": "raster",
                "paint": {
                    "raster-opacity": 1
                }
            }
        ]
      }


Name and id
-----------

The style can be given a ``name`` parameter, and layers within the style can be given an ``id`` parameter. ``name`` is the name of the style, and may be displayed. ``id`` is a machine reference to the layer, and should be unique. Also add a ``source-layer`` parameter, which provides a reference to the layer this style should be applied to.

.. note:: When viewing the style in the Layer Preview tab, ensure the ``Preview as style group`` option is checked, to ensure that ``source-layer`` is used to determine the layer(s) to render the style on.

#. Modify the name and id elements in the default style:

   .. code-block:: json
      :emphasize-lines: 3, 6-7

      {
        "version": 8,
        "name": "dem",
        "layers": [
            {
                "id": "dem",
                "source-layer": "DEM",
                "type": "raster",
                "paint": {
                    "raster-opacity": 1
                }
            }
        ]
      }

Adding contrast
---------------

#. If we want the land to show up better in the display, we can use the ``raster-contrast`` and ``raster-brightness-max`` attributes:

   .. code-block:: json
      :emphasize-lines: 11-12

      {
          "version": 8,
          "name": "dem",
          "layers": [
              {
                  "id": "dem",
                  "source-layer": "DEM",
                  "type": "raster",
                  "paint": {
                      "raster-opacity": 1,
                      "raster-contrast": 0.5,
                      "raster-brighness-max": 0.25
                  }
              }
          ]
      }

   This increases the contrast by 50%, and darkens the resulting image by a factor of ``4``.

   .. figure:: img/raster_dem_contrast.png

      Added contrast

Creating a color map
--------------------

Other styling languages, such as YSLD and SLD, have the concept of a color-map, where different regions of a single band are mapped to different colors. MBStyle does not support this functionality.

.. note:: :download:`Download the final raster style <files/mbtut_raster.json>`

We have now styled all of our layers. Continue on to :ref:`cartography.mbstyle.tutorial.map` for the final step in the process.
