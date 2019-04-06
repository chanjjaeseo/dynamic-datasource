package com.qcz.ds;

import java.util.Set;

public class MultipleDataSource {

    private Set<String> dataSources;

    private String defaultDataSource;


    public Set<String> getDataSources() {
        return dataSources;
    }

    public void setDataSources(Set<String> dataSources) {
        this.dataSources = dataSources;
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(String defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }
}
