.. _dataadmin.pgBasics.joins:


Spatial joins
=============

Spatial joins combine information from different tables by using :ref:`Spatial Relationships <dataadmin.pgBasics.spatialrelationships>` as the join key. A great deal of GIS analysis can be accomplished using spatial joins.

For example, given a subway station location, neighborhood data, and borough names, it is possible to locate a neighborhood that contains a particular subway station with the following SQL code:

.. code-block:: sql

  SELECT 
    subways.name AS subway_name, 
    neighborhoods.name AS neighborhood_name, 
  FROM nyc_neighborhoods AS neighborhoods
  JOIN nyc_subway_stations AS subways
  ON ST_Contains(neighborhoods.the_geom, subways.the_geom)
  WHERE subways.name = 'Broad St';

:: 

   subway_name | neighborhood_name  
  -------------+--------------------
   Broad St    | Financial District 

Any function that provides a true/false relationship between two tables can form the basis of a spatial join, but the most commonly used are :command:`ST_Intersects`, :command:`ST_Contains`, and :command:`ST_DWithin`.

Join and summarize
------------------

The combination of a ``JOIN`` with a ``GROUP BY`` operation supports the type of analysis that is usually undertaken with a GIS system. For example, to answer the question "What is the population and demographic profile of the neighborhoods of Manhattan?", requires analyzing census population information, with the boundaries of neighborhoods. The results should be further limited to report on just one borough of Manhattan. 

.. code-block:: sql

  SELECT 
    neighborhoods.name AS neighborhood_name, 
    Sum(census.popn_total) AS population,
    Round(100.0 * Sum(census.popn_white) / Sum(census.popn_total),1) AS white_pct,
    Round(100.0 * Sum(census.popn_black) / Sum(census.popn_total),1) AS black_pct
  FROM nyc_neighborhoods AS neighborhoods
  JOIN nyc_census_blocks AS census
  ON ST_Intersects(neighborhoods.the_geom, census.the_geom)
  WHERE neighborhoods.boroname = 'Manhattan'
  GROUP BY neighborhoods.name
  ORDER BY white_pct DESC;

::

   neighborhood_name  | population | white_pct | black_pct 
 ---------------------+------------+-----------+-----------
  Carnegie Hill       |      19909 |      91.6 |       1.5
  North Sutton Area   |      21413 |      90.3 |       1.2
  West Village        |      27141 |      88.1 |       2.7
  Upper East Side     |     201301 |      87.8 |       2.5
  Greenwich Village   |      57047 |      84.1 |       3.3
  Soho                |      15371 |      84.1 |       3.3
  Murray Hill         |      27669 |      79.2 |       2.3
  Gramercy            |      97264 |      77.8 |       5.6
  Central Park        |      49284 |      77.8 |      10.4
  Tribeca             |      13601 |      77.2 |       5.5
  Midtown             |      70412 |      75.9 |       5.1
  Chelsea             |      51773 |      74.7 |       7.4
  Battery Park        |       9928 |      74.1 |       4.9
  Upper West Side     |     212499 |      73.3 |      10.4
  Financial District  |      17279 |      71.3 |       5.3
  Clinton             |      26347 |      64.6 |      10.3
  East Village        |      77448 |      61.4 |       9.7
  Garment District    |       6900 |      51.1 |       8.6
  Morningside Heights |      41499 |      50.2 |      24.8
  Little Italy        |      14178 |      39.4 |       1.2
  Yorkville           |      57800 |      31.2 |      33.3
  Inwood              |      50922 |      29.3 |      14.9
  Lower East Side     |     104690 |      28.3 |       9.0
  Washington Heights  |     187198 |      26.9 |      16.3
  East Harlem         |      62279 |      20.2 |      46.2
  Hamilton Heights    |      71133 |      14.6 |      41.1
  Chinatown           |      18195 |      10.3 |       4.2
  Harlem              |     125501 |       5.7 |      80.5


In this example:

#. The ``JOIN`` clause creates a virtual table that includes columns from both the neighborhoods and census tables. 
#. The ``WHERE`` clause filters the virtual table to just rows in Manhattan. 
#. The remaining rows are grouped by the neighborhood name and processed by the aggregation function, :command:`SUM`, to summarize the population values.

.. note:: 

   The ``JOIN`` clause combines two ``FROM`` items. By default, this uses an ``INNER JOIN``, however there are four other types of joins. For further information, please refer to the `join_type <http://www.postgresql.org/docs/9.2/interactive/sql-select.html>`_ definition in the PostgreSQL documentation.

A distance test can also be used as a join key, to answer a summarized "all items within a given radius" query. For example, to calculate the population within a 500 meter radius of the "Broad St" subway station:

.. code-block:: sql

  SELECT 
    Sum(census.popn_total) AS population
  FROM nyc_census_blocks census
  JOIN nyc_subway_stations subway
  ON ST_DWithin(census.the_geom, subway.the_geom, 500)
  WHERE subway.name = 'Broad St';

You can alter the search radius or the subway name to return different population profiles for different stations.

