package com.qcz.ds;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

@Aspect
@Order(-1)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class DatasourceInterceptor {

    @Around(value = "@annotation(ds)")
    public Object proceed(ProceedingJoinPoint pjp, DataSource ds) throws Throwable {
        DataSourceHolder.putDataSource(ds.value());
        try {
            return pjp.proceed();
        } finally {
            DataSourceHolder.removeDataSource(ds.value());
        }
    }

}
