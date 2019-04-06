package com.qcz.ds;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration implements ApplicationContextAware,BeanDefinitionRegistryPostProcessor {

    private ApplicationContext applicationContext;

    private Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    @Bean(name = "dynamicDataSource")
    public AbstractRoutingDataSource dynamicDataSource(MultipleDataSource multipleDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //如果指定的dataSource key不存在, 则报错
        dynamicDataSource.setLenientFallback(false);
        Map<Object, Object> dataSourceMap = new HashMap<>();
        for (String ds : multipleDataSource.getDataSources()) {
            DruidDataSource druidDataSource = applicationContext.getBean(ds, DruidDataSource.class);
            //设置默认数据库
            if(ds.equalsIgnoreCase(multipleDataSource.getDefaultDataSource())) {
                dynamicDataSource.setDefaultTargetDataSource(druidDataSource);
            }
            dataSourceMap.put(ds, druidDataSource);
        }
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        return dynamicDataSource;
    }

    @Bean(name = "transactionManager")
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(AbstractRoutingDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(AbstractRoutingDataSource dynamicDataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dynamicDataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath*:mapper*/**/*.xml"));
        SqlSessionFactory sqlSessionFactory = factoryBean.getObject();
        //设置默认全局超时时间
        Integer defaultStatementTimeout = sqlSessionFactory.getConfiguration().getDefaultStatementTimeout();
        if(defaultStatementTimeout == null) {
            sqlSessionFactory.getConfiguration().setDefaultStatementTimeout(30);
        }
        return sqlSessionFactory;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = MultipleDataSourceInitializer.BEAN_NAME)
    public MultipleDataSourceInitializer multipleDataSourceInitializer(DataSourcePropertiesProcessor processor, MultipleDataSource multipleDataSource) {
        for(String ds : multipleDataSource.getDataSources()) {
            DruidDataSource dataSource = applicationContext.getBean(ds, DruidDataSource.class);

            //读取配置文件
            processor.postProcessBeforeInitialization(dataSource, ds, ds);

            initDruidDataSource(dataSource);

        }
        return MultipleDataSourceInitializer.DEFAULT;
    }

    /**
     * 初始化druid 数据源
     * @param dataSource
     */
    private void initDruidDataSource(DruidDataSource dataSource) {

        try {

            //设置默认参数
            if(dataSource.getMaxActive() == 8 || dataSource.getMaxActive() == 5) {
                dataSource.setMaxActive(100);
            }

            if(dataSource.getInitialSize() == 0 || dataSource.getInitialSize() == 1) {
                dataSource.setInitialSize(10);
            }

            if(dataSource.getMinIdle() == 0) {
                dataSource.setMinIdle(10);
            }


            if(!dataSource.isPoolPreparedStatements()) {
                dataSource.setMaxPoolPreparedStatementPerConnectionSize(5);
            }

            //设置获取连接的最大等待时间为10s
            if(dataSource.getMaxWait() < 0 || dataSource.getMaxWait() > 5000L) {
                dataSource.setMaxWait(5000L);
            }

            if(dataSource.getValidationQuery() == null) {
                dataSource.setValidationQuery("SELECT 'x'");
            }

            if(dataSource.getValidationQueryTimeout() < 0) {
                dataSource.setValidationQueryTimeout(0);
            }

        } catch (Exception e) {
            logger.error("初始化druid数据源发生错误, ex: " + e.getMessage());
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 生成数据源对应的bean
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        MultipleDataSource multipleDataSource = applicationContext.getBean(MultipleDataSource.class);
        if(multipleDataSource == null) {
            throw new RuntimeException("multipleDataSource cannot be null, " +
                    "配置了MultipleDataSourceConfiguration就一定要定义MultipleDataSource");
        }
        Set<String> dataSources = multipleDataSource.getDataSources();
        String defaultDataSource = multipleDataSource.getDefaultDataSource();

        for(String ds : dataSources) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
            if(ds.equalsIgnoreCase(defaultDataSource)) {
                builder.getBeanDefinition().setPrimary(true);
            }
            registry.registerBeanDefinition(ds, builder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //
    }
}