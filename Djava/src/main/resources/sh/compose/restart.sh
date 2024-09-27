#!/bin/bash

# 检查是否传入了容器名和端口号参数
if [ -z "$1" ]; then
    echo "Error: Container name not provided."
    exit 1
fi

# 获取容器名和端口号参数
SERVICE_NAME=$1
HOST_PORT=$2
COMPOSE_FILE=$3

# 根据容器名称设置日志路径、主机端口和暴露端口
case $SERVICE_NAME in
    gateway)
        HOST_PORT=9999
        COMPOSE_FILE=admin-compose.yml
        ;;
    auth)
        HOST_PORT=3000
        COMPOSE_FILE=admin-compose.yml
        ;;
    upms)
        HOST_PORT=4000
        COMPOSE_FILE=admin-compose.yml
        ;;
    cm)
        HOST_PORT=10100
        COMPOSE_FILE=omc-compose.yml
        ;;
    fm)
        HOST_PORT=10200
        COMPOSE_FILE=omc-compose.yml
        ;;
    mt)
        HOST_PORT=10300
        COMPOSE_FILE=omc-compose.yml
        ;;
    st)
        HOST_PORT=10400
        COMPOSE_FILE=omc-compose.yml
        ;;
    ws)
        HOST_PORT=10500
        COMPOSE_FILE=omc-compose.yml
        ;;
    ta)
        HOST_PORT=10600
        COMPOSE_FILE=omc-compose.yml
        ;;
    ocpp201)
        HOST_PORT=10800
        COMPOSE_FILE=omc-compose.yml
        ;;
    admin)
        HOST_PORT=20000
        COMPOSE_FILE=oss-compose.yml
        ;;
    thirdparty)
        HOST_PORT=20200
        COMPOSE_FILE=oss-compose.yml
        ;;
    adapter)
        HOST_PORT=20100
        COMPOSE_FILE=oss-compose.yml
        ;;
    portal)
        HOST_PORT=30910
        COMPOSE_FILE=oss-compose.yml
        ;;
    *)
        echo "Log path for service '$SERVICE_NAME' not found."
        exit 1
        ;;
esac



# 检查是否有正在运行的指定容器
if [ "$(docker ps -q -f name=$SERVICE_NAME)" ]; then
    echo "Stopping running container $SERVICE_NAME..."
    docker-compose -f $COMPOSE_FILE  stop $SERVICE_NAME
else
    echo "No running container named $SERVICE_NAME."
fi

# 检查是否有指定的容器存在（包括停止的容器）
if [ "$(docker ps -a -q -f name=$SERVICE_NAME)" ]; then
    echo "Removing container $SERVICE_NAME..."
    docker rm -f $SERVICE_NAME
else
    echo "No container named $SERVICE_NAME to remove."
fi

# 清理无用的镜像
docker rmi $(docker images -f "dangling=true" -q)


# 重新运行容器，使用传入的端口
echo "Running new container $SERVICE_NAME on port $HOST_PORT..."
docker-compose -f $COMPOSE_FILE start  $SERVICE_NAME

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
docker logs $SERVICE_NAME
exit 1
