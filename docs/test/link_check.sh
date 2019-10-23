#!/usr/bin/env bash
# Checks each .rst file in the path for http/https links and tests each with wget --spider to see if they exist.

result=0

if [[ -z ${1} ]]
then
  echo "usage: link_check.sh DIRECTORY"
  exit 1
fi

# Check $2 in file $1 for wget success; skip localhost urls
check_link ()
{
  url=$(echo ${2} | tr -d '<>')
  if echo ${url} | grep -q "http://localhost:8080"
  then
    return
  fi

  if ! wget --spider --no-check-certificate --secure-protocol=SSLv3 --timeout=20 --waitretry=5 --tries=3 --user-agent="Mozilla/5.0 (X11; Linux) Gecko Firefox/5.0" --quiet ${url} > /dev/null
  then
    echo "[ERROR] ${url} in file ${1} returned an error"
    result=1
  fi
}
export -f check_link


# Extract all links in $1 and check them
check_file ()
{
  for link in $(grep -hoP "<https?://[^\s()<>]+(?:\([\w\d]+\)|([^[:punct:]\s]|/))>" ${1} | sort | uniq)
  do
    check_link ${1} ${link}
  done
}

# Find all .rst files in the specified path
for file in $(find ${1} -name "*.rst" -print)
do
  check_file ${file}
done

exit ${result}
