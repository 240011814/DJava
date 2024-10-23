package com.yhy.djava.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author： HouYong Yang
 * @Date： 2024/10/18 16:32
 * @Describe：
 */
@Configuration
public class DruidMetricsConfig {
    private final DruidDataSource druidDataSource;
    private final Iterable<Tag> commonTag;
    private final MeterRegistry meterRegistry;

    public DruidMetricsConfig(DruidDataSource druidDataSource,
                              Iterable<Tag> commonTag,
                              MeterRegistry meterRegistry) {
        this.druidDataSource = druidDataSource;
        this.commonTag = commonTag;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void bindMetrics() {
        // 使用 Gauge 注册，使指标能够实时更新
        List<Tag> tags = new ArrayList<>();
        tags.add(commonTag.iterator().next());
        tags.add(Tag.of("pool","master"));
        // 当前活跃连接数
        Gauge.builder("druid_active_connections", druidDataSource, DruidDataSource::getActiveCount)
                .tags(tags)
                .description("Number of active connections in Druid pool")
                .register(meterRegistry);

        // 当前空闲连接数
        Gauge.builder("druid_idle_connections", druidDataSource, DruidDataSource::getPoolingCount)
                .tags(tags)
                .description("Number of idle connections in Druid pool")
                .register(meterRegistry);

        // 最大连接数
        Gauge.builder("druid_max_connections", druidDataSource, DruidDataSource::getMaxActive)
                .tags(tags)
                .description("Maximum number of connections in Druid pool")
                .register(meterRegistry);

        // 等待获取连接的线程数
        Gauge.builder("druid_waiting_threads", druidDataSource, DruidDataSource::getNotEmptyWaitThreadCount)
                .tags(tags)
                .description("Number of threads waiting for a connection in Druid pool")
                .register(meterRegistry);
    }
}
