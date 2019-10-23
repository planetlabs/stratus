.. _sysadmin.caching.seeding.considerations:

Seeding considerations
======================

.. todo:: Some of these sections could use images.

Determining what to seed involves a number of considerations. Leaving out the discussion of storage medium and limiting the discussion to just the existence or non-existence of tiles, the following considerations should be taken into account:

* Whether or not to seed
* Image format
* Coordinate reference system
* Tile dimensions
* Metatiling
* To what level to seed the full extent
* Areas of interest
* To what zoom level to seed each area of interest

Each one of these considerations are dependent on context and a system's specific needs.

Determining whether or not to seed
----------------------------------

The primary benefit to seeding is that an application will provide faster performance, as the time it takes to transfer an already-generated image is much less than the time to generate the image from the WMS server. However, if one's data is rapidly changing, then the time spent generating tiles will be wasted as the tiles will become invalid and need to be replaced often.

In all but the most dynamic data situations, seeding at least some of the cache will be beneficial.

Determining image format
------------------------

There are a number of image formats that can be used for a tile cache:

* JPEG
* PNG (24-bit PNG)
* PNG8 (8-bit PNG)
* GIF

Each of these have their own benefits and drawbacks. The content of your data will determine the best tile cache to use. For example, an imagery layer may provide a much smaller average tile size by using JPEG, while a layer with very few colors might be more efficiently served with PNG8.

The application(s) that will be requesting these tiles may have a specific format in mind, which could influence the image format.

While in almost all cases, a single image format is sufficient, it is possible to allow for more than one image format per layer. This will require managing more than one tile cache per layer, doubling the management tasks as well as the disk space. Unless there is a significant benefit to providing multiple image formats, it is not recommended to do so.

Determining coordinate reference system
---------------------------------------

It is possible to set up multiple coordinate reference systems for a given layer. A separate tile cache is needed for each of these.

The applications that will be requesting these tiles will usually have a specific CRS that is used. Selecting more than one grid set (which includes CRS) will require entirely separate tile caches and extra administrative work, so unless there is a specific reason to do so, it is recommended to work with a single grid set.

Determining tile dimensions
---------------------------

The dimensions of each tile is also a consideration, as it affects the block size on disk as well as network throughput. In general, the default size of 256x256 pixels should be sufficient.

Determining metatiling
----------------------

Metatiling is a method of generating tiles where a larger tile is generated in one pass and then sliced up, as opposed to many passes each with a single individual tile.

Metatiles mean each request will be bigger, requiring more resources, but there will be fewer requests needed. The biggest performance difference would be when a whole metatile needs to be generated when just a single tile is missing from the cache. This would be much slower than if meta-titling is not used. On the other hand, other tiles in the same metatile will often be  needed around the same time and so will benefit from the newly cached metatile.

Metatiling is especially beneficial when labels are used. The algorithm for placing labels without overlapping works only within a tile, so labels can't span tile boundaries. With metatiles, only the metatile boundaries impact label generation, which means there are far fewer places where labels would be affected.

Quantitatively, the number of tile boundaries that are also metatile boundaries is ``1/f`` (where ``f`` is the metatiling factor). The size of the metatile in tiles is given by ``f^2``. As ``f`` increases (larger metatiles), labeling improves but the amount by which it improves gets smaller, while the rate at which the size of the metatiles increase gets bigger. For example, with a 3x3 metatile, there would be one-third (1/3) the tile boundaries, while requiring a metatile nine times the size of a single tile. For a 4x4 grid, there would be one-quarter (1/4) the tile boundaries, but requiring a metatile *sixteen* times the size of a single tile. The marginal benefit decreases rapidly while the marginal cost increases rapidly. A metatile of 3x3 is usually a sufficient balance here.

Other considerations:

* The size of the window the tiles will be appearing in is a good heuristic for the upper limit on metatile size.
* If the layer is computationally intensive then smaller metatiles, or no metatiles at all would probably allow for better performance.

Determining to what level to seed the full extent
-------------------------------------------------

Each zoom level typically adds four times as many tiles (and four times the processing time to generate them) as the zoom level before it. This can add up to a tremendous amount of tiles, and thus prohibitive disk space and seed times. Therefore, it is impractical to fully seed a layer to its maximum zoom level unless it has a very small extent or a very shallow range of zoom.

A good scenario is therefore to seed the entire extent a certain (relatively low) zoom level, and then zoom certain areas to a much higher zoom level.

As this consideration refers to the max extent of a layer, disk space and processing time are the primary concerns here. While it is not possible to accurately gauge how much disk space a full extent seeding process will require, estimates can be made now that the image format and grid sets are known.

For example, given a layer in the EPSG:4326 coordinate system, utilizing the full extent, using PNG8 as the image format, and assuming an average image size on disk of 16 kB, one can estimate the disk space needed:

.. list-table::
   :header-rows: 1

   * - Highest zoom level (starting at 0)
     - Total number of tiles
     - Total disk space needed
   * - 2
     - 42
     - 672 kB
   * - 4
     - 682
     - 10.7 MB
   * - 8
     - ~175,000
     - 2.7 GB
   * - 12
     - ~44.7 million
     - 683 GB

Processing time is much more difficult to estimate, as it is dependent on specific network and system configurations.

Determining areas of interest
-----------------------------

In a given layer, there are usually areas of varying interest. Users will likely be zooming in farther in some areas more than others. In a world map, for example, cities will have users zooming in more often than the middle of the ocean. (By contrast, in a city map or other limited-extent layer, the entire extent is usually of equal interest.)

So the next step is to determine the "areas of interest" and their extents. Consider the following example or a world map where the users are likely to be looking at New York State:

.. todo:: Would be great to have an image of this.

.. list-table::
   :header-rows: 1

   * - Area
     - Relative level of interest
     - Extent
   * - World
     - Low
     - (-180, -90), (180, 90)
   * - United States
     - Medium
     - (-126, 25), (-66, 50)
   * - New York State
     - High
     - (-79.9, 40.3), (-73.2, 45.2)

The purpose here is to provide a trade-off between extent and detail.

Determining to what zoom level to seed each area of interest
------------------------------------------------------------

Now that the extent of each area of interest has been determined, the highest zoom level to seed to for each area should also be determined. Areas of moderate interest (the United States, in the above example) should be seeded to more levels than the full extent. Areas of high interest (New York State, in the above example) should be seeded to even higher zoom level.

Knowing the extent of each area can help determine to what zoom level each should be seeded.

In the example above, the United States extent should be seeded to a level higher than the world, and that the New York State extent should be seeded to a higher zoom level than the United States. As always, the actual zoom levels chosen are based on the specific considerations of the data and the disk space and processing time required.

Note that to avoid duplication in seeding jobs (especially if replacing existing/outdated tiles, also known as "reseeding"), the various jobs should operate on mutually exclusive zoom levels as shown below.

.. figure:: img/extent.png

   *Diagram showing extents and various zoom levels cached*

Preparing the seed tasks
------------------------

The above detailed analysis should be all the information needed to generate the seed jobs. There should be a seed job for the full extent of the layer and a seed job for every area of interest.

Using the above example of New York State, there would be three separate seed tasks:

* World (max extent, low interest)
* United States (area of moderate interest)
* New York State (area of high interest)

While each task would have the same image format, CRS, and tile dimensions, they would each differ in extent and zoom level.

Determining tile expiration and reseeding
-----------------------------------------

Not every data source is static, so when the underlying data is updated, tiles will need to be deleted and recreated. This process is known as "reseeding".

Based on how often your data changes, you may either wish to reseed regularly to keep the tiles fresh, or let tiles expire after a certain amount of time. Reseeding requires more processing time but ensures greater accuracy. Letting tiles expire requires less work but means that a user may occasionally request a tile or set of tiles that will need to generated in the moment.

Seeding in the cluster
----------------------

As long as the blob store for the layer being seeded is properly shared between the nodes, seeded tiles will be available to all nodes.  However a seeding job will run on just one node of the cluster.  The load of rendering those tiles will be borne by that one node and if the node fails, the job will not be completed.
