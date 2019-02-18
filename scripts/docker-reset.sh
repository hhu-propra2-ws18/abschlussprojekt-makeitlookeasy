#!/usr/bin/env bash
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)
docker rmi $(docker images -aq)
docker system prune -af
yes | docker volume prune
