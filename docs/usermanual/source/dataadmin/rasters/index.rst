.. _dataadmin.rasters:

Storing raster data
===================

Stratus includes support for several raster formats to work with, and requires rasters to be stored on a :ref:`shared disk <sysadmin.filesystem>`, or an accessible cloud storage, so that each Stratus node can access the data. We recommend using `Amazon EFS <https://aws.amazon.com/efs/>`_ or `Amazon S3 <https://aws.amazon.com/s3/>`_ storage where supported.
