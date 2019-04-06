package com.qcz.demo.service;

import com.qcz.demo.dao.TestDAO;
import com.qcz.ds.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private TestDAO testDAO;

    @DataSource(value = "spring.datasource.test2")
    public String getName(){
        return testDAO.selectName(1);
    }

}
