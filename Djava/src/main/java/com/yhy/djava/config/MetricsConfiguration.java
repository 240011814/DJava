package com.yhy.djava.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author： HouYong Yang
 * @Date： 2024/9/9 14:18
 * @Describe：
 */
@Configuration
public class MetricsConfiguration {

    @Value("${application.name}")
    private String applicationName;

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            String instance = getHostName(); // 获取主机名作为实例名
            registry.config().commonTags("application", applicationName, "instance", instance);
        };
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown"; // 如果获取主机名失败，则返回"unknown"
        }
    }
}
