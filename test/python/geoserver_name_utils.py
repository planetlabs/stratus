#!/usr/bin/python

"""
geoserver_name_utils.py

Utility class for generating PostGIS database and table names, and GeoServer 
workspace, store, and layer names, based on certain proviced parameters

Names are of the format ${prefix}{###}, where the appropriate name_pad parameter dictates the number of #
"""
class GeoserverNameUtil:

    def __init__(self,
            database_name_pad=4,
            table_name_pad=4,
            workspace_name_pad=4,
            coverage_name_pad=4,
            coveragestore_name_pad=4,
            datastore_name_pad=4,

            database_prefix="db",
            workspace_prefix="ws",
            coverage_prefix="cov",
            table_prefix="cov",
            coveragestore_prefix="cs",
            datastore_prefix="ds"):

        self.database_name_pad=database_name_pad
        self.table_name_pad=table_name_pad
        self.workspace_name_pad=workspace_name_pad
        self.coverage_name_pad=coverage_name_pad
        self.coveragestore_name_pad=coveragestore_name_pad
        self.datastore_name_pad=datastore_name_pad

        self.database_prefix=database_prefix
        self.table_prefix=table_prefix
        self.workspace_prefix=workspace_prefix
        self.coverage_prefix=coverage_prefix
        self.coveragestore_prefix=coveragestore_prefix
        self.datastore_prefix=datastore_prefix


    """
    Get a generated database name of the format ${database_prefix}{###} where `database_name_pad` dictates the number of # 
    """
    def get_gen_database_name(self, i):
        istring=str(i)
        istring=istring.zfill(self.database_name_pad)
        return self.database_prefix+istring

    """
    Get a generated table name of the format ${table_prefix}{###} where `table_name_pad` dictates the number of # 
    """
    def get_gen_table_name(self, i):
        istring=str(i)
        istring=istring.zfill(self.table_name_pad)
        return self.table_prefix+istring

    """
    Get a generated coverage name of the format ${coverage_prefix}{###} where `coverage_name_pad` dictates the number of # 
    """
    def get_gen_coverage_name(self, i):
        istring=str(i)
        istring=istring.zfill(self.coverage_name_pad)
        return self.coverage_prefix+istring

    """
    Get a generated coverage store name of the format ${coveragestore_prefix}{###} where `coveragestore_name_pad` dictates the number of # 
    """
    def get_gen_coverage_store_name(self, i):
        istring=str(i)
        istring=istring.zfill(self.coveragestore_name_pad)
        return self.coveragestore_prefix+istring

    """
    Get a generated workspace name of the format ${coverage_prefix}{###} where `workspace_name_pad` dictates the number of # 
    """
    def get_gen_workspace_name(self, i):
        istring = str(i)
        istring = istring.zfill(self.workspace_name_pad)
        return self.workspace_prefix+istring

    """
    Get a generated datastore name of the format ${datastore_prefix}{###} where `datastore_name_pad` dictates the number of # 
    """
    def get_gen_datastore_name(self, i):
        istring = str(i)
        istring = istring.zfill(self.datastore_name_pad)
        return self.datastore_prefix+istring

