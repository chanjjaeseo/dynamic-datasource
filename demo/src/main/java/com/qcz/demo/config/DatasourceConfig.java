package com.qcz.demo.config;

import com.qcz.ds.MultipleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DatasourceConfig {

    @Bean("multipleDataSource")
    public MultipleDataSource multipleDataSource() {
        MultipleDataSource multipleDataSource = new MultipleDataSource();
        Set<String> datasource = new HashSet<>();
        datasource.add("spring.datasource.test1");
        datasource.add("spring.datasource.test2");
        multipleDataSource.setDataSources(datasource);
        multipleDataSource.setDefaultDataSource("spring.datasource.test1");
        return multipleDataSource;
    }

}
