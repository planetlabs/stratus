.. _glossary:

Glossary
========

.. glossary::
   :sorted:

   API
     Application Programming Interface. A set of routines, procedures, protocols, and tools for building software applications.

   Aurora
     Amazon Aurora is a relational database engine that combines the speed and reliability of high-end commercial databases with the simplicity and cost-effectiveness of open source databases. It delivers up to five times the throughput of standard MySQL and up to three times the throughput of standard PostgreSQL.

   AWS
     Amazon Web Services (AWS) is a comprehensive, evolving, cloud computing platform provided by Amazon.

   Azure
     Microsoft Azure is a cloud computing platform provided by Microsoft.

   Stratus
     Stratus.

   Chart (Helm)
     A :term:`Helm` package that contains information sufficient for installing a set of :term:`Kubernetes` resources into a Kubernetes cluster. Charts contain a Chart.yaml file as well as templates, default values (values.yaml), and dependencies.

   Cloud Storage
     Cloud Storage is a cloud computing model in which data is stored on remote servers accessed from the internet, or "cloud". It is maintained, operated and managed by a cloud storage service provider on a storage servers that are built on virtualization techniques. Examples of Cloud Storage include :term:`S3` and Amazon :term:`EFS` provided under :term:`AWS`.

   CloudWatch
     CloudWatch is a monitoring service for :term:`AWS` cloud resources and the applications you run on :term:`AWS`. You can use Amazon CloudWatch to collect and track metrics, collect and monitor log files, set alarms, and automatically react to changes in your :term:`AWS` resources.

   Container
     A container is a stand-alone, executable software bundle that includes everything needed to run it. A container executes on a virtualized operating system, but is typically much smaller and faster than a :term:`Virtual Machine`. The most commonly used containerization technology is :term:`Docker`.

   Coverage
     A type of spatial data that represents different values at different locations. Satellite imagery, aerial photography, and digital elevation models (DEMs) are examples of coverage data.

   CRS
     Coordinate reference system. The combination of a geographic and projected coordinate systems that define how two-dimensional maps relate to locations on the earth's surface. Geographic coordinate systems use a three-dimensional spherical surface to define those locations, referencing points on that surface through angles (latitude and longitude values) measured from the earth's center. Projected coordinate systems are based on flat two-dimensional surfaces, with locations referenced by x, y coordinates measured as constant lengths and distances across that surface. See also :term:`SRID` and :term:`SRS`.

   Data Store
     A GeoServer data store represents a connection to a source of raster or vector data, such as a file or group of files, a database table, and so on.

   Docker
     Docker is a platform for running containerized applications. It also serves as a repository for distributing containers. See: :term:`Container`.

   EC2
     Elastic Compute Cloud (:term:`AWS` EC2) is a web service that provides secure, resizable compute capacity in the cloud.

   ECS
     Amazon Elastic Container Service (:term:`AWS` ECS) is a highly scalable, high-performance container orchestration service that supports Docker containers and allows you to easily run and scale containerized applications on :term:`AWS`.

   EFS
     Amazon Elastic File System (Amazon EFS) provides simple, scalable, elastic file storage for use with AWS Cloud services and on-premises resources. Amazon EFS is built to elastically scale on demand without disrupting applications, growing and shrinking automatically as you add and remove files, so your applications have the storage they need, when they need it.

   Feature Type
     A GeoServer feature type (featureType) represents a data source table.

   GCP
     Google Cloud Platform (GCP) is a suite of cloud computing services provided by Google.

   GDAL
     `Geospatial Data Abstraction Library <http://gdal.org>`_, pronounced "GOO-duhl", an open source raster access library with support for a large number of formats, used widely in both open source and proprietary software.

   Geographic Markup Language
     `Geography Markup Language <http://www.opengeospatial.org/standards/gml>`_ (GML) is the :term:`Open Geospatial Consortium` standard XML format for representing spatial feature information.

   GeoJSON
     Javascript Object Notation. A text format that is very fast to parse in JavaScript virtual machines. In spatial, the extended specification for `GeoJSON <http://geojson.org>`_ is commonly used.

   GeoRSS
     RSS feed containing geographic information in :term:`GML <Geographic Markup Language>` (Geographic Markup Language) format.

   GIS
     `Geographic information system <http://en.wikipedia.org/wiki/Geographic_information_system>`_ or geographical information system captures, stores, analyzes, manages, and presents data that is linked to location.

   GML
     See :term:`Geographic Markup Language`.

   Helm
     Helm is the package manager for Kubernetes. As an operating system package manager makes it easy to install tools on an OS, Helm makes it easy to install applications and resources into Kubernetes clusters.

   ImageMosaic
     A GeoServer data store supporting the creation of a mosaic based on a number of georeferenced raster data sources. See also :term:`Data Store` and :term:`Mosaic`.

   JSON
     JavaScript Object Notation. A text format that is very fast to parse in JavaScript virtual machines. In spatial, the extended specification for `GeoJSON <http://geojson.org>`_ is commonly used.

   JSP
     JavaServer Pages. A scripting system for Java server applications that allows the interleaving of markup and Java procedural code.

   JSTL
     JavaServer Page Template Library. A tag library for :term:`JSP` that encapsulates many of the standard functions handled in JSP (database queries, iteration, conditionals) into a terse syntax.

   K8s
     See :term:`Kubernetes`

   KML
     Keyhole Markup Language. This is the spatial :term:`XML` format used by Google Earth. Google Earth was originally written by a company named "Keyhole", hence the (now obscure) reference in the name.

   Kubernetes
     Kubernetes is an open-source system for automating deployment, scaling, and management of containerized applications. It groups containers that make up an application into logical units for easy management and discovery.

   Layer
     A published GeoServer resource representing a raster or vector spatial data source.

   Mosaic
     A collection of georeferenced raster data sources merged together to create a seamless raster dataset. See also :term:`ImageMosaic`.

   Namespace
     A GeoServer namespace defines the XML namespace of a feature type. An XML namespace uniquely describes elements and attributes in an XML document. See also :term:`Feature Type`. Can also refer to a GeoServer :term:`Workspace`.

   OGC
     See :term:`Open Geospatial Consortium`.

   Open Geospatial Consortium
     The `Open Geospatial Consortium <http://www.opengeospatial.org/>`_ (OGC)  is a standards organization responsible for developing specifications for geospatial services.

   OSGeo
     The `Open Source Geospatial Foundation <http://osgeo.org>`_ (OSGeo) is a non-profit foundation dedicated to the promotion and support of open source geospatial software.

   Projection
     A method of representing the earth's three-dimensional surface on a two-dimensional plan. See also :term:`CRS`.

   Quay
     Quay.io is a service that specializes in hosting private :term:`Docker` repositories.

   Raster Layer
     A published GeoServer representation of raster format data stored as a cell-based representation of features on the earth surface. Each cell has a distinct value, and all cells with the same value represent a specific feature. Raster layers are analogous to coverages. See also :term:`Layer` and :term:`Coverage`.

   RDS
     Amazon Relational Database Service (Amazon RDS) is a web service that makes it easier to set up, operate, and scale a relational database in the cloud.

   Redis
     Redis is an open source, BSD licensed, advanced key-value store. It is often referred to as a data structure server since keys can contain strings, hashes, lists, sets and sorted sets. Redis stands for REmote DIctionary Server.

   REST
     REpresentational State Transfer. An open, resource-oriented model for implementing Web services.

   S3
     Simple Storage Service (:term:`AWS` s3) is storage for the Internet. It is designed to make web-scale computing easier for developers.

   Scalable Vector Graphics
     This is a family of specifications of an :term:`XML`-based file format for describing two-dimensional vector graphics, both static and dynamic (i.e. interactive or animated). See http://en.wikipedia.org/wiki/Scalable_Vector_Graphics.

   SFSQL
     The `Simple Features for SQL <http://www.opengeospatial.org/standards/sfs>`_ (SFSQL) specification from the :term:`Open Geospatial Consortium` defines the types and functions that make up a standard spatial database.

   SLD
     The `Styled Layer Descriptor <http://www.opengeospatial.org/standards/sld>`_ (SLD) specification from the :term:`Open Geospatial Consortium` defines an format for describing cartographic rendering of vector features.

   Spring
     Spring Framework is a Java platform that provides comprehensive infrastructure support for developing Java applications, including web applications.

   Spring Boot
     Spring boot is a utility for setting up applications quickly, offering an out of the box configuration in order to build :term:`Spring` powered applications.

   SQL
     Structured query language. This is the standard programming language for querying relational databases. See http://en.wikipedia.org/wiki/SQL.

   SQL/MM
     `SQL Multimedia <http://www.fer.unizg.hr/_download/repository/SQLMM_Spatial-_The_Standard_to_Manage_Spatial_Data_in_Relational_Database_Systems.pdf>`_; includes several sections on extended types, including a substantial section on spatial types.

   SRID
     Spatial reference ID. This a unique number assigned to a particular "coordinate reference system". The PostGIS table **spatial_ref_sys** contains a large collection of well-known SRID values and text representations of the coordinate reference systems.

   SRS
     Spatial reference system. See :term:`SRID` and :term:`CRS`.

   SVG
     See :term:`Scalable Vector Graphics`.

   Terraform
     Terraform is a tool for building, changing, and versioning infrastructure. Infrastructure is described by a set of configuration files, which are used by Terraform to create and run an execution plan to set up the environment.

   Vector Layer
     A published GeoServer representation of a collection of vector feature types (featureType) stored as mathematical paths—a point as a single x, y coordinate, lines as a series of x, y coordinates, and polygons as a series of x, y coordinates that start and end on the same location. See also :term:`Layer`.

   Virtual Machine
     A Virtual Machine (VM) is an emulation of a computer system. It runs on a host computer, and provides virtual hardware that an arbitrary operating system can run on. A VM can do (almost) everything a regular machine can do. Multiple VMs can run on a single host machine. See also: :term:`Container`.

   WCS
     See :term:`Web Coverage Service`.

   Web Coverage Service
     The `Web Coverage Service <http://www.opengeospatial.org/standards/wcs>`_ (WCS) specification from the :term:`Open Geospatial Consortium` defines an interface for reading and writing geospatial data as "coverages" across the web.

   Web Feature Service
     The `Web Feature Service <http://www.opengeospatial.org/standards/wfs>`_ (WFS) specification from the :term:`Open Geospatial Consortium` defines an interface for reading and writing geographic features across the web.

   Web Map Service
     The `Web Map Service <http://www.opengeospatial.org/standards/wms>`_ (WMS) specification from the :term:`Open Geospatial Consortium` defines an interface for requesting rendered map images across the web.

   Web Processing Service
     The `Web Processing Service <http://www.opengeospatial.org/standards/wps>`_ (WPS) specification from the :term:`Open Geospatial Consortium` provides rules for standardizing inputs and outputs (requests and responses) for geospatial processing services.

   Well-Known Binary
     This refers to the binary representation of geometries described in the Simple Features for SQL specification (:term:`SFSQL`).

   Well-Known Text
     This refers either to the text representation of geometries, with strings starting "POINT", "LINESTRING", "POLYGON", and so on, or the text representation of a :term:`CRS`, with strings starting "PROJCS", "GEOGCS", and so on. Well-known text representations are :term:`OGC <Open Geospatial Consortium>` standards, but do not have their own specification documents. The first descriptions of Well-Known Text (for geometries and for CRS) appeared in the :term:`SFSQL` 1.0 specification. Often abbreviated as WKT.

   WFS
     See :term:`Web Feature Service`.

   WKB
     See :term:`Well-Known Binary`.

   WKT
     See :term:`Well-Known Text`.

   WMS
     See :term:`Web Map Service`.

   Workspace
     An organizational structure in GeoServer for grouping related data stores. See also :term:`Data Store`.

   WPS
     See :term:`Web Processing Service`.

   XML
     eXtensible Markup Language. A document encoding markup language, designed for data transport and storage.
