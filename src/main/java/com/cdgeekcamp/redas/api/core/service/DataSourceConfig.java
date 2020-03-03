package com.cdgeekcamp.redas.api.core.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

        public final static String WRITE_DATASOURCE_KEY = "writeDataSource";
        public final static String READ_DATASOURCE_KEY = "readDataSource";

        @ConfigurationProperties(prefix = "spring.datasource.read")
        @Bean(name = READ_DATASOURCE_KEY)
        public DataSource readDataSource() {
                return DataSourceBuilder.create().build();
        }

        @ConfigurationProperties(prefix = "spring.datasource.write")
        @Bean(name = WRITE_DATASOURCE_KEY)
        @Primary
        public DataSource writeDataSource() {
                return DataSourceBuilder.create().build();
        }

        /**
         * 注入AbstractRoutingDataSource
         *
         * @param readDataSource
         * @param writeDataSource
         * @return
         * @throws Exception
         */
        @Bean
        public AbstractRoutingDataSource routingDataSource(
                @Qualifier(READ_DATASOURCE_KEY) DataSource readDataSource,
                @Qualifier(WRITE_DATASOURCE_KEY) DataSource writeDataSource) throws Exception {
                        DynamicDataSource dataSource = new DynamicDataSource();
                Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
                targetDataSources.put(WRITE_DATASOURCE_KEY, writeDataSource);
                targetDataSources.put(READ_DATASOURCE_KEY, readDataSource);
                dataSource.setTargetDataSources(targetDataSources);// 配置数据源
                dataSource.setDefaultTargetDataSource(writeDataSource);// 默认为主库用于写数据
                return dataSource;
        }

}