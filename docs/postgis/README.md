# PostGIS Community Documentation

The Stratus PostGIS community docs mirror the PostGIS user manual.

All top-level pages have a blank RST version in /src, which contains an iframe referencing the actual HTML docs.

## Upgrading

1. Replace the existing ``doc-html-${pg.version}.tar.gz`` with the new version from https://download.osgeo.org/postgis/docs/

2. Update ``pg.version`` in [build/build.properties](../../build/build.properties)

2. Run a build and open [target/index.html](target/index.html). Click through each heading in the sidebar and look for any broken or missing pages. Add new ``.rst`` files and modify the page links in [../themes/server_rtd_theme/optional_body.html](../themes/server_rtd_theme/optional_body.html) accordinly.