package com.qcz.demo;

import com.qcz.demo.bean.DataSource;
import com.qcz.ds.DataSourcePropertiesProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSourceProcessorTest {

    @Autowired
    private DataSourcePropertiesProcessor processor;

    @Test
    public void test(){
        DataSource dataSource = new DataSource();
        processor.postProcessBeforeInitialization(dataSource,"spring.datasource.base", "spring.datasource.base");
        System.out.println(dataSource.getUsername());
    }

}
