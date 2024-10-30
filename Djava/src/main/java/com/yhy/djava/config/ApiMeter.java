package com.yhy.djava.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @Author： HouYong Yang
 * @Date： 2024/10/14 11:08
 * @Describe：
 */
@Component
public class ApiMeter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PrometheusMeterRegistry meterRegistry;
    private final Iterable<Tag> commonTag;
    private final Duration[] durations;

    public ApiMeter(PrometheusMeterRegistry meterRegistry, Iterable<Tag> commonTag) {
        this.meterRegistry = meterRegistry;
        this.commonTag = commonTag;
        this.durations = new Duration[]{Duration.ofMillis(100),
                Duration.ofMillis(300),
                Duration.ofMillis(600),
                Duration.ofMillis(1000),
                Duration.ofMillis(2000),
                Duration.ofMillis(5000),
                Duration.ofMillis(10000),
                Duration.ofMillis(60000)
        };
    }


    public void counter(String name, boolean result) {
        try {
            List<Tag> tags = new ArrayList<>();
            commonTag.forEach(tag -> tags.add(Tag.of(tag.getKey(), tag.getValue())));
            tags.add(Tag.of("name", name));
            tags.add(Tag.of("result", String.valueOf(result)));
            Counter.builder("api_request_count")
                    .description("api request count")
                    .tags(tags)
                    .register(meterRegistry)
                    .increment();
        } catch (Exception e) {
            logger.error("prometheus add histogram error, counter {}", e.toString());
        }
    }


    public void duration(String name, long time, boolean result) {
        try {
            List<Tag> tags = new ArrayList<>();
            commonTag.forEach(tag -> tags.add(Tag.of(tag.getKey(), tag.getValue())));
            tags.add(Tag.of("name", name));
            tags.add(Tag.of("result", String.valueOf(result)));
            Timer.builder("api_request_time")
                    .description("api request time statistics")
                    .tags(tags)
                    .serviceLevelObjectives(durations)
                    .maximumExpectedValue(Duration.ofMinutes(3))
                    .publishPercentileHistogram()
                    .register(meterRegistry)
                    .record(time, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("prometheus add histogram error, duration {}", e.toString());
        }
    }


}
