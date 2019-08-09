package examples;

import javax.sql.DataSource;

import com.nimbusds.jose.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.jpa.config.EnableJpaStores;
import org.springframework.content.jpa.config.JpaStoreConfigurer;
import org.springframework.content.jpa.config.JpaStoreProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static java.lang.String.format;

@Configuration
@EnableJpaRepositories(basePackages="examples.repositories")
@EnableTransactionManagement
@EnableJpaStores(basePackages="examples.stores")
public class SqlServerTestConfig {

    @Value("#{environment.SQLSERVER_HOST}")
    private String sqlServerHost;

    @Value("#{environment.SQLSERVER_DB_NAME}")
    private String sqlServerDbName;

    @Value("#{environment.SQLSERVER_USERNAME}")
    private String sqlServerUsername;

    @Value("#{environment.SQLSERVER_PASSWORD}")
    private String sqlServerPassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionString = format("jdbc:sqlserver://%s;databaseName=%s", sqlServerHost, sqlServerDbName);
        ds.setUrl(connectionString);
        ds.setUsername(sqlServerUsername);
        ds.setPassword(sqlServerPassword);

        System.out.println(format("----> sqlserver configuration: %s", Base64.encode(connectionString + ":" + sqlServerUsername + ":" + sqlServerPassword)));

        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.SQL_SERVER);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("examples.models");  	// Tell Hibernate where to find Entities
        factory.setDataSource(dataSource());

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Value("/org/springframework/content/jpa/schema-drop-sqlserver.sql")
    private Resource dropBlobsSchema;

    @Value("/org/springframework/content/jpa/schema-sqlserver.sql")
    private Resource blobsSchema;

    @Bean
    DataSourceInitializer datasourceInitializer() {
        ResourceDatabasePopulator databasePopulator =
                new ResourceDatabasePopulator();

        databasePopulator.addScript(dropBlobsSchema);
        databasePopulator.addScript(blobsSchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource());
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

    @Bean
    public JpaStoreConfigurer configurer() {
        return new JpaStoreConfigurer() {
            @Override
            public void configure(JpaStoreProperties store) {
                store.commitTimeout(120);
            }
        };
    }
}
