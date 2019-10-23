.. _sysadmin.caching.seeding.gui:

Seeding a tile cache through the web interface
==============================================

This section explains how to start a seed task in the built-in tile caching system. The considerations for how best to determine the parameters for such a job are contained in the :ref:`sysadmin.caching.seeding.considerations` section. It will assume that a gridset has already been configured for the layer.

To seed a tile cache for a given layer:

#. Log in to the GeoServer web admin interface with an user with administrative credentials.

#. Once logged in, click the :guilabel:`Tile Layers` link under the :guilabel:`Tile Caching` section.

   .. figure:: img/gui_menu_tilelayers.png

      *Tile Layers link in the Tile Cache menu*

#. Find the entry for the layer you would like to seed, and click :guilabel:`Seed/Truncate`.

   .. figure:: img/gui_menu_layerlist.png

      *usa:states in the list of tiled layers*

#. This will bring up the embedded GeoWebCache interface, which is responsible for managing the tile cache.

   .. figure:: img/gui_gwcseedpage.png

      *Embedded GeoWebCache menu used for seeding*

#. Fill out the form titled :guilabel:`Create a new task`.

   .. tabularcolumns:: |p{5cm}|p{10cm}|
   .. list-table::
      :header-rows: 1

      * - Option
        - Description
      * - :guilabel:`Number of tasks to use`
        - Number of concurrent threads to use in the seeding process. Value to use is system-dependent, though to minimize the chance of a task getting blocked, it is a good idea to use a value of at least 2.
      * - :guilabel:`Type of operation`
        - Determines the operation. Select :guilabel:`Seed` in most cases.
      * - :guilabel:`Grid Set`
        - Desired grid set to use when generating tiles.
      * - :guilabel:`Format`
        - Image format for tiles. Must be a MIME type such as ``image/png``.
      * - :guilabel:`Zoom start`
        - Lowest zoom level to generate tiles. Often but not always 0 (the zoom level that contains the fewest amount of tiles).
      * - :guilabel:`Zoom end`
        - Highest zoom level to generate tiles. See :ref:`sysadmin.caching.seeding.considerations` for advice on determining which zoom levels to seed.
      * - :guilabel:`Bounding box`
        - Use this extent to seed tiles from only a subsection of the entire grid set extent. See :ref:`sysadmin.caching.seeding.considerations` for advice on when to seed a portion of the extent.

   .. figure:: img/gui_gwcseedform.png

      *Seeding form*

#. When the form is filled out, click :guilabel:`Submit`. The seed task will start. The page will show the task's status, including estimated time remaining. Click the :guilabel:`Refresh list` button to update the view.

   .. figure:: img/gui_status.png

      *Status of seed tasks*

#. The status of this layer's seed tasks are available at ``http://<GEOSERVER_URL>/gwc/rest/seed/namespace_layer``. In the URL, the colon in the fully qualified layer name is replaced by an underscore (so ``usa:states`` would become ``usa_states``).

   .. note::

      It is also possible to view all currently running seed tasks from this page (or any layer's seed page) by selecting List :guilabel:`all Layers tasks` at the very top of the page. The view will automatically refresh to include seed tasks from other layers.

      .. figure:: img/gui_listalllayers.png

         *Select this to view seed tasks for all layers*

#. On this status page, it is also possible to kill (cancel) seed tasks. To kill a seed task, find the seed task to kill and click the :guilabel:`Kill Task` button.

  .. figure:: img/gui_killtask.png

     *Click to kill task*

