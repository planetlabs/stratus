.. _cartography.mbstyle.tutorial:

MBStyle tutorial
================

This tutorial provides a set of steps that users can follow to style layers using the MBStyle styling language.

For this tutorial, you will need to download the following files from the `Natural Earth dataset <http://www.naturalearthdata.com/>`_:

.. include:: files/data.txt

You will need to convert these files to PostGIS tables using :ref:`shp2pgsql <dataadmin.pgGettingStarted.shp2pgsql>` command line utility for the .shp files and :ref:`raster2pgsql <dataadmin.pgGettingStarted.raster2pgsql>` for the raster files, and import them into GeoServer as new layers. This tutorial will assume that the layers were renamed to be ``countries``, ``roads``, ``places``, and ``DEM``, respectively.

The following pages will demonstrate how to style each of these layers to compose a map. The tutorial is divided into five steps. The first four steps will style each of the layers individually, and then the final step will be to compose the finished map.

.. toctree::
   :maxdepth: 1

   line
   polygon
   point
   raster
   map
