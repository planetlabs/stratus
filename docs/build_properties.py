# parses the build.properties file making it available to sphinx config files
import os
from StringIO import StringIO
from ConfigParser import ConfigParser

__all__ = ['server_version', 'server_version_short', 'gs_version', 'gs_version_short', 
           'gwc_version', 'gwc_version_short', 'pg_version', 'pg_version_short']

bpfile = open(os.path.join(os.path.dirname(__file__), 'build.properties'))
buf = StringIO()
buf.write('[build]')
buf.write(bpfile.read())
buf.seek(0, os.SEEK_SET)

cp = ConfigParser()
cp.readfp(buf)

def clean_snapshot(ver):
    return ver.replace('-SNAPSHOT', '.x')

def short_version(ver):
    return ver.split('-')[0]

def x_version(ver):
    return ver + '.x'

server_version = clean_snapshot(cp.get('build', 'stratus.version'))
server_version_short = short_version(cp.get('build', 'stratus.version'))

gs_version = x_version(cp.get('build', 'gs.major_version'))
gs_version_short = cp.get('build', 'gs.major_version')

gwc_version = x_version(cp.get('build', 'gwc.major_version'))
gwc_version_short = cp.get('build', 'gwc.major_version')

pg_version = clean_snapshot(cp.get('build', 'pg.version'))
pg_version_short = short_version(cp.get('build', 'pg.version'))