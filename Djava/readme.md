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

prometheus.yml 新增
- job_name: 'Djava'

  # Override the global default and scrape targets from this job every 5 seconds.
  scrape_interval: 5s

  static_configs:
    - targets: ['localhost:8080']
