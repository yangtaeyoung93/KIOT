package com.airguard.config.database;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(value = "com.airguard.mapper.readonly", sqlSessionFactoryRef = "readSqlSessionFactory")
public class ReadDatabaseConfig {

  @Bean(name = "readDataSource")
  @ConfigurationProperties(prefix = "spring.read-only.datasource")
  public DataSource readDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "readSqlSessionFactory")
  public SqlSessionFactory readSqlSessionFactory(
      @Autowired @Qualifier("readDataSource") DataSource readDataSource,
      ApplicationContext applicationContext) throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(readDataSource);
    factoryBean
        .setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));
    factoryBean
        .setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/readonly/*.xml"));

    return factoryBean.getObject();
  }

  @Bean(name = "readSqlSession")
  public SqlSession readSqlSession(
      @Autowired @Qualifier("readSqlSessionFactory") SqlSessionFactory readSqlSessionFactory) {
    return new SqlSessionTemplate(readSqlSessionFactory);
  }

  @Bean(name = "readTransactionManager")
  public DataSourceTransactionManager readTransactionManager(
      @Autowired @Qualifier("readDataSource") DataSource readDataSource) {
    return new DataSourceTransactionManager(readDataSource);
  }
}
