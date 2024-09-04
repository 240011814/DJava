#!/usr/bin/env sh

current_datetime=$(date +%Y%m%d_%H%M%S)
git_c=$(git rev-parse --short HEAD)
app_name='demo'
image_name="djava:latest"
echo  ${image_name}
docker build -t ${image_name} -f ./Dockerfile
