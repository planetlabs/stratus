#!/usr/bin/env bash

set -e 

if [[ -z ${1} ]]
then
  echo "usage: doc_test.sh DIRECTORY"
  exit 1
fi

doc_dirs="geoserver geowebcache install usermanual"
bad_modules=""
cd ${1}

for doc_dir in ${doc_dirs}
do
  echo "=== ${doc_dir} ============================================="
  result=0

  echo "+++ document status"
  if ! test/status_check.sh ${doc_dir}
  then
    result=1
  fi
  echo

  echo "+++ link check"
  if ! test/link_check.sh ${doc_dir}
  then
    result=1
  fi
  echo

  # better reporting results when individually compiling modules
  # capture mvn error code then grep for ERROR and WARNING to report
  echo "+++ sphinx warnings"
  tmp=$(mktemp)
  if mvn clean compile -Ptest -projects ${doc_dir} --quiet | grep -E "(WARNING|ERROR)" > ${tmp}
  then
    result=1
    # remove unnecesssary parts of the error string: maven adds [exec] and the full path of the file
    grep -v "BUILD ERROR" ${tmp} | sed s/[[:space:]]*\\[exec\\][[:space:]]*// | sed "s/$(echo $(pwd) | sed -e 's/\([[\/.*]\|\]\)/\\&/g')//g"
  fi
  rm ${tmp}

  if [ $result -eq 1 ] 
  then  
    bad_docs="${bad_docs} ${doc_dir}"
  fi
  
  echo
  echo
done

if [ -n "${bad_docs}" ]
then
  echo "ERRORS FOUND IN FOLLOWING MODULES: "
  for bad_doc in ${bad_docs}
  do
    echo "... ${bad_doc}"
  done
  exit 1
else
  exit 0
fi

