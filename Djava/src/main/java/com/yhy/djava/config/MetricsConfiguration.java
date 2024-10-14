package com.yhy.djava.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author： HouYong Yang
 * @Date： 2024/9/9 14:18
 * @Describe：
 */
@Configuration
public class MetricsConfiguration {

    private String applicationName;
    private final Environment env;
    private final MeterRegistry meterRegistry;

    public MetricsConfiguration(@Value("${spring.application.name}") String applicationName,
                                Environment env ,
                                MeterRegistry meterRegistry) {
        this.applicationName = applicationName;
        this.env = env;
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config().commonTags(getCommonTags());
        };
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown"; // 如果获取主机名失败，则返回"unknown"
        }
    }


    @Bean
    public Iterable<Tag> getCommonTags() {
        List<Tag> result = new ArrayList<>();
        result.add( Tag.of("application", applicationName));
        result.add( Tag.of("env", Optional.ofNullable( this.env.getProperty("ENV")).orElse("")));
        String instance = getHostName(); // 获取主机名作为实例名
        result.add( Tag.of("instance", instance));
        result.add( Tag.of("version", Optional.ofNullable(env.getProperty("GIT_COMMIT")).orElse("")));
        return result;
    }

}
