package com.qcz.ds;

public interface MultipleDataSourceInitializer {

    MultipleDataSourceInitializer DEFAULT = new Default();

    String BEAN_NAME = "multipleDataSourceInitializer";

    class Default implements MultipleDataSourceInitializer{

    }
}
