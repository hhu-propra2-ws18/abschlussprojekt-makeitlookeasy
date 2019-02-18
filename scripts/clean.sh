#!/usr/bin/env bash

i=""
j=`pwd`

for i in $j/{build/logs/services,build/logs/container-logs,build/logs,pgsql-01/data,pgsql-01/conf,pgsql-01,PPdata}
do
        if [[ ! -d "$i" ]]; then
          echo "$i" does not exist
        else
          echo "$i" exists. deleting...
          rm -rf "$i"
        fi
done
