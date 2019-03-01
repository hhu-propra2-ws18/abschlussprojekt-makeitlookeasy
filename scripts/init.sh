#!/usr/bin/env bash

i=""
j=`pwd`

for i in $j/{build/logs/services,build/logs/container-logs,pgsql-01/data,pgsql-01/conf,/PPdata}
do
        if [[ ! -d "$i" ]]; then
          echo creating... "$i"
          mkdir -p "$i"
#        else
#          echo "$i" !already exists!
        fi
done

if [[ ! -d "$HOME/jenkins_home" ]]; then
   mkdir ~/jenkins_home/
  else
    echo OK
fi

chmod +x $j/scripts/*.sh
