#!/usr/bin/env sh

current_datetime=$(date +%Y%m%d_%H%M%S)
git_c=$(git rev-parse --short HEAD)
version_name="${current_datetime}_${git_c}"
image_name="djava:latest"
echo  ${image_name}
docker build --build-arg GIT_COMMIT=${version_name} -t ${image_name} .
