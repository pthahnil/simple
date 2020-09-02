package com.simple.xrcraft.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by pthahnil on 2019/3/28.
 */
@Configuration
public class DruidConfig {

	@Bean(name="dataSource",initMethod = "init", destroyMethod = "close")
	@ConfigurationProperties(prefix="spring.datasource.druid")
	public DataSource dataSource(){
		return new DruidDataSource();
	}

	// 配置事物管理器
	@Bean(name="transactionManager")
	public DataSourceTransactionManager transactionManager(){
		return new DataSourceTransactionManager(dataSource());
	}

}
