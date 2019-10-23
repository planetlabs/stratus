Introduction
============

PostGIS was developed by Refractions Research Inc, as a spatial database
technology research project. Refractions is a GIS and database
consulting company in Victoria, British Columbia, Canada, specializing
in data integration and custom software development. We plan on
supporting and developing PostGIS to support a range of important GIS
functionality, including full OpenGIS support, advanced topological
constructs (coverages, surfaces, networks), desktop user interface tools
for viewing and editing GIS data, and web-based access tools.

PostGIS is an incubation project of the OSGeo Foundation. PostGIS is
being continually improved and funded by many FOSS4G Developers as well
as corporations all over the world that gain great benefit from its
functionality and versatility.


Project Steering Committee
----------------------------

The PostGIS Project Steering Committee (PSC) coordinates the general
direction, release cycles, documentation, and outreach efforts for the
PostGIS project. In addition the PSC provides general user support,
accepts and approves patches from the general PostGIS community and
votes on miscellaneous issues involving PostGIS such as developer commit
access, new PSC members or significant API changes.

Mark Cave-Ayland
    Coordinates bug fixing and maintenance effort, alignment of PostGIS
    with PostgreSQL releases, spatial index selectivity and binding,
    loader/dumper, and Shapefile GUI Loader, integration of new and new
    function enhancements.

Regina Obe
    Buildbot Maintenance, windows production and experimental builds,
    Documentation, general user support on PostGIS newsgroup, X3D
    support, Tiger Geocoder Support, management functions, and smoke
    testing new functionality or major code changes.

Bborie Park
    Raster development, integration with GDAL, raster loader, user
    support, general bug fixing, testing on various OS (Slackware, Mac,
    Windows, and more)

Paul Ramsey (Chair)
    Co-founder of PostGIS project. General bug fixing, geography
    support, geography and geometry index support (2D, 3D, nD index and
    anything spatial index), underlying geometry internal structures,
    PointCloud (in development), GEOS functionality integration and
    alignment with GEOS releases, loader/dumper, and Shapefile GUI
    loader.

Sandro Santilli
    Bug fixes and maintenance and integration of new GEOS functionality
    and alignment with GEOS releases, Topology support, and Raster
    framework and low level api functions.


Core Contributors Present
----------------------------

Jorge Arévalo
    Raster development, GDAL driver support, loader

Nicklas Avén
    Distance function enhancements (including 3D distance and
    relationship functions) and additions, Tiny WKB output format (TWKB)
    (in development) and general user support

Olivier Courtin
    Input output XML (KML,GML)/GeoJSON functions, 3D support and bug
    fixes.

Pierre Racine
    Raster overall architecture, prototyping, programming support

David Zwarg
    Raster development (mostly map algebra analytic functions)


Core Contributors Past
-------------------------

Chris Hodgson
    Prior PSC Member. General development, site and buildbot
    maintenance, OSGeo incubation management

Kevin Neufeld
    Prior PSC Member. Documentation and documentation support tools,
    buildbot maintenance, advanced user support on PostGIS newsgroup,
    and PostGIS maintenance function enhancements.

Dave Blasby
    The original developer/Co-founder of PostGIS. Dave wrote the server
    side objects, index bindings, and many of the server side analytical
    functions.

Mateusz Loskot
    Raster loader, low level raster api functions

Jeff Lounsbury
    Original development of the Shape file loader/dumper. Current
    PostGIS Project Owner representative.

Mark Leslie
    Ongoing maintenance and development of core functions. Enhanced
    curve support. Shapefile GUI loader.



Other Contributors
--------------------

Individual Contributors

    In alphabetical order: Alex Bodnaru, Alex Mayrhofer, Andrea Peri,
    Andreas Forø Tollefsen, Andreas Neumann, Anne Ghisla, Barbara
    Phillipot, Ben Jubb, Bernhard Reiter, Brian Hamlin, Bruce Rindahl,
    Bruno Wolff III, Bryce L. Nordgren, Carl Anderson, Charlie Savage,
    Dane Springmeyer, David Skea, David Techer, Eduin Carrillo, Even
    Rouault, Frank Warmerdam, George Silva, Gerald Fenoy, Gino Lucrezi,
    Guillaume Lelarge, IIDA Tetsushi, Ingvild Nystuen, Jason Smith, Jeff
    Adams, Jose Carlos Martinez Llari, Kashif Rasul, Klaus Foerster,
    Kris Jurka, Leo Hsu, Loic Dachary, Luca S. Percich, Maria Arias de
    Reyna, Mark Sondheim, Markus Schaber, Maxime Guillaud, Maxime van
    Noppen, Michael Fuhr, Nathan Wagner, Nathaniel Clay, Nikita Shulga,
    Norman Vine, Rafal Magda, Ralph Mason, Richard Greenwood, Silvio
    Grosso, Steffen Macke, Stephen Frost, Tom van Tilburg, Vincent
    Picavet

Corporate Sponsors

    These are corporate entities that have contributed developer time,
    hosting, or direct monetary funding to the PostGIS project

    In alphabetical order: Arrival 3D, Associazione Italiana per
    l'Informazione Geografica Libera (GFOSS.it), AusVet, Avencia,
    Azavea, Cadcorp, CampToCamp, City of Boston (DND), Clever Elephant
    Solutions, Cooperativa Alveo, Deimos Space, Faunalia, Geographic
    Data BC, Hunter Systems Group, Lidwala Consulting Engineers,
    LisaSoft, Logical Tracking & Tracing International AG, Michigan Tech
    Research Institute, Natural Resources Canada, Norwegian Forest and
    Landscape Institute, OpenGeo, OSGeo, Oslandia, Palantir
    Technologies, Paragon Corporation, R3 GIS, Refractions Research,
    Regione Toscana-SIGTA, Safe Software, Sirius Corporation plc, Stadt
    Uster, UC Davis Center for Vectorborne Diseases, University of
    Laval, U.S Department of State (HIU), Vizzuality, Zonar Systems

Crowd Funding Campaigns

    Crowd funding campaigns are campaigns we run to get badly wanted
    features funded that can service a large number of people. Each
    campaign is specifically focused on a particular feature or set of
    features. Each sponsor chips in a small fraction of the needed
    funding and with enough people/organizations contributing, we have
    the funds to pay for the work that will help many. If you have an
    idea for a feature you think many others would be willing to
    co-fund, please post to the `PostGIS
    newsgroup <http://postgis.net/mailman/listinfo/postgis-users>`__
    your thoughts and together we can make it happen.

    PostGIS 2.0.0 was the first release we tried this strategy. We used
    `PledgeBank <http://www.pledgebank.com>`__ and we got two successful
    campaigns out of it.

    `**postgistopology** <http://www.pledgebank.com/postgistopology>`__
    - 10 plus sponsors each contributed $250 USD to build toTopoGeometry
    function and beef up topology support in 2.0.0. It happened.

    `**postgis64windows** <http://www.pledgebank.com/postgis64windows>`__
    - 20 someodd sponsors each contributed $100 USD to pay for the work
    needed to work out PostGIS 64-bit issues on windows. It happened. We
    now have a 64-bit release for PostGIS 2.0.1 available on PostgreSQL
    stack builder.

Important Support Libraries

    The `GEOS <http://trac.osgeo.org/geos/>`__ geometry operations
    library, and the algorithmic work of Martin Davis in making it all
    work, ongoing maintenance and support of Mateusz Loskot, Sandro
    Santilli (strk), Paul Ramsey and others.

    The `GDAL <http://trac.osgeo.org/gdal/>`__ Geospatial Data
    Abstraction Library, by Frank Warmerdam and others is used to power
    much of the raster functionality introduced in PostGIS 2.0.0. In
    kind, improvements needed in GDAL to support PostGIS are contributed
    back to the GDAL project.

    The `Proj4 <http://trac.osgeo.org/proj/>`__ cartographic projection
    library, and the work of Gerald Evenden and Frank Warmerdam in
    creating and maintaining it.

    Last but not least, the `PostgreSQL
    DBMS <http://www.postgresql.org>`__, The giant that PostGIS stands
    on. Much of the speed and flexibility of PostGIS would not be
    possible without the extensibility, great query planner, GIST index,
    and plethora of SQL features provided by PostgreSQL.

More Information
------------------

-  The latest software, documentation and news items are available at
   the PostGIS web site, http://postgis.net.

-  More information about the GEOS geometry operations library is
   available at\ http://trac.osgeo.org/geos/.

-  More information about the Proj4 reprojection library is available at
   http://trac.osgeo.org/proj/.

-  More information about the PostgreSQL database server is available at
   the PostgreSQL main site http://www.postgresql.org.

-  More information about GiST indexing is available at the PostgreSQL
   GiST development site, http://www.sai.msu.su/~megera/postgres/gist/.

-  More information about MapServer internet map server is available at
   `http://mapserver.org <http://mapserver.org/>`__.

-  The "`Simple Features for Specification for
   SQL <http://www.opengeospatial.org/standards/sfs>`__\ " is available
   at the OpenGIS Consortium web site: http://www.opengeospatial.org/.


