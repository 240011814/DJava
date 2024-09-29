prometheus

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

docker run -d   --name=prometheus  -v /home/prometheus.yml:/etc/prometheus/prometheus.yml   -p 9090:9090 prom/prometheus
docker run -d   --name=grafana -p 9800:3000  -v /path/to/grafana/data:/var/lib/grafana grafana/grafana-enterprise


prometheus.yml 新增
- job_name: 'Djava'

  scrape_interval: 5s

  static_configs:
    - targets: ['localhost:8080']
