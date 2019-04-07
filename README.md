## dynamic-datasource

### 1.引入maven依赖
pom.xml
```
<dependency>
    <groupId>com.qcz.ds</groupId>
    <artifactId>common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

setting.xml
```
<mirror>
    <id>nexus-aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://maven.aliyun.com/nexus/content/groups/public</url>
</mirror>
<mirror>
    <id>nexus-qcz</id>
    <mirrorOf>*</mirrorOf>
    <name>Nexus qcz</name>
    <url>http://47.107.127.220:8081/repository/maven-public/</url>
</mirror>
```

### 2.配置application.yml

```
server:
  port: 8090
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    test1:
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://xxx:3306/test1?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull
      username: xxx
      password: xxx
    test2:
      driver-class-name: com.mysql.jdbc.Driver
      url: jdbc:mysql://xxx:3306/test2?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&zeroDateTimeBehavior=convertToNull
      username: xxx
      password: xxx
```

### 3.配置多数据源

```
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
```
> 我们配置两个可访问的数据源

- spring.datasource.test1
- spring.datasource.test2

spring.datasource.test1作为默认的数据源

### 4.使用

> 我们建立了两个数据库test1和test2，建立相同的表结构，存储不同的数据

- test1

| id | name |
| ------ | ------ |
| 1 | db1 | 

- test2

| id | name |
| ------ | ------ |
| 1 | db2| 

> 现在我们用mybatis 结合多数据源访问这两个数据库。

```
//方式一: Callable访问
String name = DataSourceHolder.callInDataSource("spring.datasource.test2",() ->testDAO.selectName(1));
//方式二: 限定方法的数据源
@DataSource(value = "spring.datasource.test2")
public String getName(){
    return testDAO.selectName(1);
}
//方式三: Runable访问（不关注返回值）
DataSourceHolder.runInDataSource("spring.datasource.test2",() ->testDAO.selectName(1));
//方式四: 使用默认数据源
String name = testDAO.selectName(1)；
```





