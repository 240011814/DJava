package com.yhy.djava.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author： HouYong Yang
 * @Date： 2024/10/14 11:15
 * @Describe：
 */
@Aspect
@Component
public class ApiMeterAspect {

    private final ApiMeter apiMeter;

    public ApiMeterAspect(ApiMeter apiMeter) {
        this.apiMeter = apiMeter;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object Around(ProceedingJoinPoint point) throws Throwable {
        Object proceed;
        String url = "";
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        if (request != null) {
            // 获取请求的URL
            url = request.getRequestURI();
        }
        boolean result = true;
        try {
            proceed = point.proceed();
        } catch (Throwable e) {
            result = false;
            throw e;
        } finally {
            stopWatch.stop();
            apiMeter.counter(url, result);
            apiMeter.duration(url, stopWatch.getTotalTimeMillis(), result);
        }

        return proceed;
    }
}
