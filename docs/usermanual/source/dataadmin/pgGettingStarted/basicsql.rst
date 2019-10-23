.. _dataadmin.pgGettingStarted.basicsql:


Basic SQL
=========

``SQL``, or "Structured Query Language", is a programming language specifically developed for querying and updating data in a relational database.

The four main SQL instructions, or *key words*, are: 

* ``SELECT``—Returns rows in response to a query
* ``INSERT``—Adds new rows to a table
* ``UPDATE``—Alters existing rows in a table
* ``DELETE``—Removes rows from a table
 

SELECT queries
--------------

The basic syntax for a select query is:

.. code-block:: sql

  SELECT some_columns FROM some_data_source WHERE some_condition;
  
.. note:: For a synopsis of all ``SELECT`` parameters, see the PostgresSQL `documentation  <http://www.postgresql.org/docs/9.1/interactive/sql-select.html>`_.
   
The parameter ``some_columns`` represents either column names or functions of column values. The ``some_data_source`` is either a single table, or a composite table created by joining two tables on a key or condition. The ``some_condition`` parameter is a filter restricting the number of rows to be returned.

For example, to query a table containing information about Brooklyn in New York City and ask "What are the names of all the neighborhoods in Brooklyn?", the following SQL command would be required:

.. code-block:: sql

  SELECT name 
    FROM nyc_neighborhoods 
    WHERE boroname = 'Brooklyn';

The results may be further refined by applying a function, or one-word command, to the query. For example, to identify *How many letters are in the names of all the neighborhoods in Brooklyn?* would require adding the PostgreSQL string length function, :command:`char_length(string)`. 

.. code-block:: sql

  SELECT char_length(name) 
    FROM nyc_neighborhoods 
    WHERE boroname = 'Brooklyn';

In many cases, the individual rows are of less interest than a statistic that applies to all of them. In this case, knowing the lengths of the neighborhood names might be less useful than knowing the average length of the names. Functions that operate on multiple rows and return a single result are known as *aggregate* functions.  

PostgreSQL has a number of built-in aggregate functions, including the general purpose ``avg()`` for calculating average values and ``stddev()`` for calculating standard deviations. To answer *What is the average number of letters and standard deviation of number of letters in the names of all the neighborhoods in Brooklyn?* would require modifying the query to report the average and standard deviation values as follows:
  
.. code-block:: sql

   SELECT avg(char_length(name)), stddev(char_length(name)) 
     FROM nyc_neighborhoods 
     WHERE boroname = 'Brooklyn';
  
This will return the following::

           avg         |       stddev       
  ---------------------+--------------------
   11.7391304347826087 | 3.9105613559407395

In this example, the aggregate functions have been applied to every row in the result set. It is also possible to summarize within smaller subsets of the result set by adding a ``GROUP BY`` clause. Aggregate functions often require a ``GROUP BY`` statement to group the result set by one or more columns. To identify *What is the average number of letters in the names of all the neighborhoods in New York City, reported by borough?*, would require the following code:

.. code-block:: sql

   SELECT boroname, avg(char_length(name)), stddev(char_length(name)) 
     FROM nyc_neighborhoods 
     GROUP BY boroname;
 
By including the ``boroname`` column in the output result, it is possible to determine which statistic applies to which borough. In an aggregate query, only output columns that are either (a) members of the grouping clause or (b) aggregate functions may be used.
  
::

     boroname    |         avg         |       stddev       
  ---------------+---------------------+--------------------
   Brooklyn      | 11.7391304347826087 | 3.9105613559407395
   Manhattan     | 11.8214285714285714 | 4.3123729948325257
   The Bronx     | 12.0416666666666667 | 3.6651017740975152
   Queens        | 11.6666666666666667 | 5.0057438272815975
   Staten Island | 12.2916666666666667 | 5.2043390480959474
  

For more information about SQL statements and functions, please refer to the `SQL Syntax <http://www.postgresql.org/docs/9.1/static/sql-syntax.html>`_ section of the PostgreSQL Documentation.
