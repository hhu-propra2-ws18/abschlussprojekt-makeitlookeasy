#!/usr/bin/env bash

i=""
j=`pwd`

for i in $j/{build,PPdata,out,.gradle,pgsql-01,uploads}
do
        if [[ ! -d "$i" ]]; then
          echo "$i" does not exist
        else
          echo "$i" exists. deleting...
          rm -rf "$i"
        fi
done
