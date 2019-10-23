Long Transactions Support
=========================

This module and associated pl/pgsql functions have been implemented to
provide long locking support required by `Web Feature
Service <http://www.opengeospatial.org/standards/wfs>`__ specification.

    **Note**

    Users must use `serializable transaction
    level <http://www.postgresql.org/docs/current/static/transaction-iso.html>`__
    otherwise locking mechanism would break.

AddAuth
Add an authorization token to be used in current transaction.
boolean
AddAuth
text
auth\_token
Description
-----------

Add an authorization token to be used in current transaction.

Creates/adds to a temp table called temp\_lock\_have\_table the current
transaction identifier and authorization token key.

Availability: 1.1.3

Examples
--------

::

            SELECT LockRow('towns', '353', 'priscilla');
            BEGIN TRANSACTION;
                SELECT AddAuth('joey');
                UPDATE towns SET the_geom = ST_Translate(the_geom,2,2) WHERE gid = 353;
            COMMIT;


            ---Error--
            ERROR:  UPDATE where "gid" = '353' requires authorization 'priscilla'
            

See Also
--------

?

CheckAuth
Creates trigger on a table to prevent/allow updates and deletes of rows
based on authorization token.
integer
CheckAuth
text
a\_schema\_name
text
a\_table\_name
text
a\_key\_column\_name
integer
CheckAuth
text
a\_table\_name
text
a\_key\_column\_name
Description
-----------

Creates trigger on a table to prevent/allow updates and deletes of rows
based on authorization token. Identify rows using <rowid\_col> column.

If a\_schema\_name is not passed in, then searches for table in current
schema.

    **Note**

    If an authorization trigger already exists on this table function
    errors.

    If Transaction support is not enabled, function throws an exception.

Availability: 1.1.3

Examples
--------

::

                SELECT CheckAuth('public', 'towns', 'gid');
                result
                ------
                0
                

See Also
--------

?

DisableLongTransactions
Disable long transaction support. This function removes the long
transaction support metadata tables, and drops all triggers attached to
lock-checked tables.
text
DisableLongTransactions
Description
-----------

Disable long transaction support. This function removes the long
transaction support metadata tables, and drops all triggers attached to
lock-checked tables.

Drops meta table called ``authorization_table`` and a view called
``authorized_tables`` and all triggers called ``checkauthtrigger``

Availability: 1.1.3

Examples
--------

::

    SELECT DisableLongTransactions();
    --result--
    Long transactions support disabled
              

See Also
--------

?

EnableLongTransactions
Enable long transaction support. This function creates the required
metadata tables, needs to be called once before using the other
functions in this section. Calling it twice is harmless.
text
EnableLongTransactions
Description
-----------

Enable long transaction support. This function creates the required
metadata tables, needs to be called once before using the other
functions in this section. Calling it twice is harmless.

Creates a meta table called ``authorization_table`` and a view called
``authorized_tables``

Availability: 1.1.3

Examples
--------

::

    SELECT EnableLongTransactions();
    --result--
    Long transactions support enabled
              

See Also
--------

?

LockRow
Set lock/authorization for specific row in table
integer
LockRow
text
a\_schema\_name
text
a\_table\_name
text
a\_row\_key
text
an\_auth\_token
timestamp
expire\_dt
integer
LockRow
text
a\_table\_name
text
a\_row\_key
text
an\_auth\_token
timestamp
expire\_dt
integer
LockRow
text
a\_table\_name
text
a\_row\_key
text
an\_auth\_token
Description
-----------

Set lock/authorization for specific row in table <authid> is a text
value, <expires> is a timestamp defaulting to now()+1hour. Returns 1 if
lock has been assigned, 0 otherwise (already locked by other auth)

Availability: 1.1.3

Examples
--------

::

    SELECT LockRow('public', 'towns', '2', 'joey');
    LockRow
    -------
    1

    --Joey has already locked the record and Priscilla is out of luck
    SELECT LockRow('public', 'towns', '2', 'priscilla');
    LockRow
    -------
    0

            

See Also
--------

?

UnlockRows
Remove all locks held by specified authorization id. Returns the number
of locks released.
integer
UnlockRows
text
auth\_token
Description
-----------

Remove all locks held by specified authorization id. Returns the number
of locks released.

Availability: 1.1.3

Examples
--------

::

            SELECT LockRow('towns', '353', 'priscilla');
            SELECT LockRow('towns', '2', 'priscilla');
            SELECT UnLockRows('priscilla');
            UnLockRows
            ------------
            2
            

See Also
--------

?
