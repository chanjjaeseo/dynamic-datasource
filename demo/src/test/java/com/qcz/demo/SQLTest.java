package com.qcz.demo;

import com.qcz.demo.dao.TestDAO;
import com.qcz.demo.service.TestService;
import com.qcz.ds.DataSource;
import com.qcz.ds.DataSourceHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SQLTest {

    @Autowired
    private TestDAO testDAO;

    @Autowired
    private TestService testService;

    @Test
    public void test(){
        String name = DataSourceHolder.callInDataSource("spring.datasource.test2",() ->testDAO.selectName(1));
        System.out.println(name);
    }

    @Test
    public void test2(){
        System.out.println(testService.getName());
    }

}
