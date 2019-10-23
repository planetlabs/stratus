Reporting Problems
==================

Reporting Software Bugs
=======================

Reporting bugs effectively is a fundamental way to help PostGIS
development. The most effective bug report is that enabling PostGIS
developers to reproduce it, so it would ideally contain a script
triggering it and every information regarding the environment in which
it was detected. Good enough info can be extracted running ``SELECT
postgis_full_version()`` [for postgis] and ``SELECT version()`` [for postgresql].

If you aren't using the latest release, it's worth taking a look at its
`release changelog <http://svn.osgeo.org/postgis/trunk/NEWS>`__ first,
to find out if your bug has already been fixed.

Using the `PostGIS bug tracker <http://trac.osgeo.org/postgis/>`__ will
ensure your reports are not discarded, and will keep you informed on its
handling process. Before reporting a new bug please query the database
to see if it is a known one, and if it is please add any new information
you have about it.

You might want to read Simon Tatham's paper about `How to Report Bugs
Effectively <http://www.chiark.greenend.org.uk/~sgtatham/bugs.html>`__
before filing a new report.

Reporting Documentation Issues
==============================

The documentation should accurately reflect the features and behavior of
the software. If it doesn't, it could be because of a software bug or
because the documentation is in error or deficient.

Documentation issues can also be reported to the `PostGIS bug
tracker <http://trac.osgeo.org/postgis>`__.

If your revision is trivial, just describe it in a new bug tracker
issue, being specific about its location in the documentation.

If your changes are more extensive, a Subversion patch is definitely
preferred. This is a four step process on Unix (assuming you already
have `Subversion <http://subversion.apache.org/>`__ installed):

1. Check out a copy of PostGIS' Subversion trunk. On Unix, type:

   ``svn checkout
           http://svn.osgeo.org/postgis/trunk/``

   This will be stored in the directory ./trunk

2. Make your changes to the documentation with your favorite text
   editor. On Unix, type (for example):

   ``vim trunk/doc/postgis.xml``

   Note that the documentation is written in DocBook XML rather than
   HTML, so if you are not familiar with it please follow the example of
   the rest of the documentation.

3. Make a patch file containing the differences from the master copy of
   the documentation. On Unix, type:

   ``svn diff trunk/doc/postgis.xml >
           doc.patch``

4. Attach the patch to a new issue in bug tracker.


