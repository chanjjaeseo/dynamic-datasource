package com.qcz.ds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class DataSourceHolder {

    private static ThreadLocal<DataSourceContainer> dataSource = ThreadLocal.withInitial(
            DataSourceContainer::new
    );

    public static <T> T callInDataSource(String dataSource, Callable<T> callable) {
        try {
            putDataSource(dataSource);
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            removeDataSource(dataSource);
        }
    }

    public static void runInDataSource(String dataSource, Runnable runnable) {
        try {
            putDataSource(dataSource);
            runnable.run();
        } finally {
            removeDataSource(dataSource);
        }
    }

    public static String getDataSource(){
        return dataSource.get().getDataSource();
    }

    public static void putDataSource(String datasource){
        dataSource.get().putDataSource(datasource);
    }

    public static void removeDataSource(String datasource) {
        dataSource.get().removeDataSource(datasource);
    }

    public static void clear(){
        dataSource.remove();
    }

    private static class DataSourceContainer {

        private static Logger logger = LoggerFactory.getLogger(DataSourceHolder.class);

        private List<String> dataSourceList = new LinkedList<>();

        void removeDataSource(String dataSource){
            if (logger.isDebugEnabled()) {
                logger.debug("移除数据库之前数据源信息: " + dataSource);
            }
            int lastIndex = dataSourceList.size() - 1;
            if (dataSource.equals(dataSourceList.get(lastIndex))) {
                dataSourceList.remove(lastIndex);
            }
        }

        void putDataSource(String dataSource){
            if (logger.isDebugEnabled()) {
                logger.debug("请求数据库之前数据源信息: " + dataSource);
            }
            dataSourceList.add(dataSource);
        }

        String getDataSource(){

            if (dataSourceList.isEmpty()) return null;

            return dataSourceList.get(dataSourceList.size() - 1);
        }
    }

}
