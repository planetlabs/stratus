.. _sysadmin.caching.seeding:

Seeding a tile cache
====================

A tile can be generated in two ways:

* By the system administrator, usually in an automatic batch operation
* By the user, who generates individual tiles by requesting ones not already cached

In both cases, the method of generation remains the same on the server side: a request is sent to the source WMS, which is returned and stored in the tile cache.

The first case, generating tiles by the system administrator in a batch operation, is known as "seeding". This section will discuss considerations involved when seeding a cache in advance, as well as how to execute those requests.

.. toctree::
   :maxdepth: 2

   considerations
   gui

.. todo::

   rest
