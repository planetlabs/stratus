#!/usr/bin/env bash
# Checks each .rst file in the path for "Document Status"

if [[ -z ${1} ]]
then
  echo "usage: status_check.sh DIRECTORY"
  exit 1
fi

result=0

# Look for warning string in file
check_file ()
{
  if grep -HoE "Document status:[[:space:]][*][*].*[*][*]" ${1}
  then
    result=1
  fi
}

# Find all .rst files in the specified path
for file in $(find ${1} -name "*.rst" -print)
do
  check_file ${file} 
done


exit ${result}
