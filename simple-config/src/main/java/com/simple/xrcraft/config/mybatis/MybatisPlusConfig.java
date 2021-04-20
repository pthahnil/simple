package com.simple.xrcraft.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by pthahnil on 2019/3/28.
 */
@Configuration
@MapperScan("com.simple.xrcraft.persist.mapper")
public class MybatisPlusConfig {
	/***
	 * plus 的性能优化
	 * @return
	 */
	/*@Bean
	public PerformanceInterceptor performanceInterceptor() {
		PerformanceInterceptor performanceInterceptor=new PerformanceInterceptor();
		*//*<!-- SQL 执行性能分析，开发环境使用，线上不推荐。 maxTime 指的是 sql 最大执行时长 -->*//*
		performanceInterceptor.setMaxTime(1000);
		*//*<!--SQL是否格式化 默认false-->*//*
		//performanceInterceptor.setFormat(true);
		return performanceInterceptor;
	}*/

	/**
	 *	 mybatis-plus分页插件
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		PaginationInterceptor page = new PaginationInterceptor();

		Properties props = new Properties();
		props.put("dialectType", "mysql");

		page.setProperties(props);
		return page;
	}

}
