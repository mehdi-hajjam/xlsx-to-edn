#!/bin/bash

set -eu
FILE=$1
NB_HOSP=$2
DATA=resources/data.edn
PREV_DATA=resources/previous-data.edn
if [ -f "$FILE" ]; then
    echo "$FILE .... check"
else 
    echo "ERROR: $FILE does not exist."
    exit 1
fi

if [ -f "$DATA" ]; then
    echo "Changing $DATA to $PREV_DATA .... check"
    mv resources/data.edn resources/previous-data.edn
else
  if [ -f "$PREV_DATA" ]; then
    echo "$PREV_DATA .... check"
  else
    echo "ERROR: $PREV_DATA is missing."
    exit 2
  fi
fi
echo "Adding xlsx data to previous-data.edn"
java -jar target/xlsx-to-edn-0.1.0-SNAPSHOT-standalone.jar $1 $2
