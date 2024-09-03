#!/usr/bin/env sh

# 定义Harbor标签
docker tag djava:latest 106.52.158.70:9000/project/djava:latest

# 推送镜像到Harbor中
docker push 106.52.158.70:9000/project/djava:latest