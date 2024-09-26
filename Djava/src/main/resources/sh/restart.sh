#!/bin/bash

# 检查是否传入了容器名和端口号参数
if [ -z "$1" ]; then
    echo "Error: Container name not provided."
    exit 1
fi

# 获取容器名和端口号参数
CONTAINER_NAME=$1
HOST_PORT=$2

# 检查是否有正在运行的指定容器
if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
    echo "Stopping running container $CONTAINER_NAME..."
    docker stop $CONTAINER_NAME
else
    echo "No running container named $CONTAINER_NAME."
fi

# 检查是否有指定的容器存在（包括停止的容器）
if [ "$(docker ps -a -q -f name=$CONTAINER_NAME)" ]; then
    echo "Removing container $CONTAINER_NAME..."
    docker rm -f $CONTAINER_NAME
else
    echo "No container named $CONTAINER_NAME to remove."
fi

# 清理无用的镜像
docker rmi $(docker images -f "dangling=true" -q)

# 根据容器名称设置日志路径、主机端口和暴露端口
case $CONTAINER_NAME in
    wiseman-gateway)
        LOG_PATH="/logs/thor-gateway/thor-gateway.log"
        HOST_PORT=9999
        EXPOSE_PORT="-p 9999:9999"
        ;;
    wiseman-auth)
        LOG_PATH="/logs/thor-auth/thor-auth.log"
        HOST_PORT=3000
        EXPOSE_PORT="-p 3000:3000"
        ;;
    wiseman-upms)
        LOG_PATH="/logs/thor-upms/thor-upms.log"
        HOST_PORT=4000
        EXPOSE_PORT="-p 4000:4000"
        ;;
    wiseman-omc-cm)
        LOG_PATH="/logs/omc-service-cm/omc-service-cm.log"
        HOST_PORT=10100
        EXPOSE_PORT="-p 10100:10100"
        ;;
    wiseman-omc-fm)
        LOG_PATH="/logs/omc-service-fm/omc-service-fm.log"
        HOST_PORT=10200
        EXPOSE_PORT="-p 10200:10200"
        ;;
    wiseman-omc-mt)
        LOG_PATH="/logs/omc-service-mt/omc-service-mt.log"
        HOST_PORT=10300
        EXPOSE_PORT="-p 10300:10300"
        ;;
    wiseman-omc-st)
        LOG_PATH="/logs/omc-service-st/omc-service-st.log"
        HOST_PORT=10400
        EXPOSE_PORT="-p 10400:10400"
        ;;
    wiseman-omc-ws)
        LOG_PATH="/logs/omc-service-ws/omc-service-ws.log"
        HOST_PORT=10500
        EXPOSE_PORT="-p 10500:10500"
        ;;
    wiseman-omc-ta)
        LOG_PATH="/logs/omc-service-ta/omc-service-ta.log"
        HOST_PORT=10600
        EXPOSE_PORT="-p 10600:10600"
        ;;
    wiseman-omc-ocpp201)
        LOG_PATH="/logs/omc-med-ocpp201/omc-med-ocpp201.log"
        HOST_PORT=10700
        EXPOSE_PORT="-p 10700:10700 -p 2317:2317 -p 35000:35000 -p 36000:36000"
        ;;
    wiseman-oss-admin)
        LOG_PATH="/logs/thoross-admin/thoross-admin.log"
        HOST_PORT=20000
        EXPOSE_PORT="-p 20000:20000"
        ;;
    wiseman-oss-thirdparty)
        LOG_PATH="/logs/thoross-thirdparty/thoross-thirdparty.log"
        HOST_PORT=20200
        EXPOSE_PORT="-p 20200:20200"
        ;;
    wiseman-oss-adapter)
        LOG_PATH="/logs/thoross-adapter/thoross-adapter.log"
        HOST_PORT=20100
        EXPOSE_PORT="-p 20100:20100"
        ;;
    wiseman-oss-portal)
        LOG_PATH="/logs/thoross-portal/thoross-portal.log"
        HOST_PORT=30910
        EXPOSE_PORT="-p 30910:30910"
        ;;
    *)
        echo "Log path for service '$CONTAINER_NAME' not found."
        exit 1
        ;;
esac

# 重新运行容器，使用传入的端口
echo "Running new container $CONTAINER_NAME on port $HOST_PORT..."
docker run --restart=always -d --name $CONTAINER_NAME $EXPOSE_PORT -e "log.file.path=$LOG_PATH" --env-file env/wsm.env -v /data/deploy/data/thor/logs:/logs images.ideerworld.com/feature/$CONTAINER_NAME

# 检查容器是否成功启动
TIMEOUT=120  # 设置超时时间（秒）
INTERVAL=10  # 每次检查的时间间隔
ELAPSED=0

echo "开始检查服务是否在端口 $HOST_PORT 上运行..."

# 反复检查服务是否启动成功
while [ $ELAPSED -lt $TIMEOUT ]; do
    # 使用 curl 检查服务是否在指定端口上可用
    if curl --output /dev/null --silent --head --fail "http://localhost:$HOST_PORT/actuator/health"; then
        echo "服务在端口 $HOST_PORT 上成功启动！"
        exit 0
    else
        echo "服务尚未启动，等待 $INTERVAL 秒后重试..."
        sleep $INTERVAL
        ELAPSED=$((ELAPSED + INTERVAL))
    fi
done

# 如果超过超时时间
echo "服务未能在端口 $HOST_PORT 上成功启动（超时 $TIMEOUT 秒）。"
docker logs $CONTAINER_NAME
exit 1
