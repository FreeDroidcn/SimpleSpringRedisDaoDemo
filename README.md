# SimpleSpringRedisDaoDemo
基于spring-mvc和Redis整合的简单demo

Redis整合spring框架使用的持久层接口和实现类
该接口提供Redis数据库基本的增删改查的基本操作

在原来的项目基础上加入以下两个maven依赖包，经测试兼容spring4.3.9，其余版本的spring没有测试过。
请根据不同版本的spring框架选择不同版本的spring-data-redis。

    <dependency>
      <groupId>redis.clients</groupId>
      <artifactId>jedis</artifactId>
      <version>2.9.0</version>
    </dependency>
    
    <dependency>
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-redis</artifactId>
      <version>1.8.4.RELEASE</version>
    </dependency>
    
代码截取公司项目，所以没把整个项目放上来，只有Redis相关的部分代码，导入spring mvc项目里应该直接测试实体类的方法。
spring-data-redis的开发文档：https://docs.spring.io/spring-data/redis/docs/1.8.4.RELEASE/api/
开发文档讲得很详细了，对照Redis官方的开发文档，根据自己的业务需求能实现不同的功能
