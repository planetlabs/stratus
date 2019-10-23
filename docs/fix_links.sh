#!/bin/bash

# 
# This script updates links in the documentation tree 
# so that component links are made absolute to a specified
# url.
#
# This script should be run only on docs hosted online at 
# suite.opengeo.org. 
#
if [ -z $2 ]; then
  echo "Usage: $0 <doc_dir> <base_url>"
  exit -1
fi

doc_dir=$1
base_url=$2

pushd $doc_dir > /dev/null
find . -name "*.html" -exec sed -i '' "s#class=\"abs-link\" href=\"/stratus-docs#href=\"$base_url#g" {} \;
popd
