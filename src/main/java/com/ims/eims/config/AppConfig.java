package com.ims.eims.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * The Root Application Configuration.
 * <p>
 * "Hi Spring fans! Welcome to the configuration class where we manually wire up all the beautiful beans
 * that make our application tick. No auto-configuration magic here, just pure, explicit Java config.
 * Look at how clean and understandable it is!"
 */
@Configuration
@ComponentScan(basePackages = "com.ims.eims",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
    }
)
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ims.eims.repository")
@EnableCaching
@EnableScheduling
public class AppConfig {

    // "We need a DataSource! Let's spin up an embedded H2 database. It's fast, it's easy, and it's right here in memory!"
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("eimsdb")
                .build();
    }

    // "JPA is powerful, and we need an EntityManagerFactory to harness that power.
    // We'll use Hibernate as our provider because it's the standard, right?"
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.ims.eims.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        // "Let's see the SQL! It's always good to know what your ORM is doing under the hood."
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaProperties(properties);

        return em;
    }

    // "Transactions are crucial. We want ACID properties, and Spring's PlatformTransactionManager gives us that declarative goodness."
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    // "Performance matters! Let's enable caching with a simple ConcurrentMapCacheManager.
    // In production, you might want Redis or Hazelcast, but this is perfect for getting started."
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("notices");
    }
}
