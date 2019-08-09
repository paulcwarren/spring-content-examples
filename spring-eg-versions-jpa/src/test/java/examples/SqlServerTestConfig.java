package examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
import org.springframework.versions.jpa.config.JpaLockingAndVersioningConfig;

import static java.lang.String.format;

@Configuration
@EnableJpaRepositories(basePackages={"tests.versioning", "org.springframework.versions"})
@EnableTransactionManagement
@EnableFilesystemStores(basePackages="tests.versioning")
@Import(JpaLockingAndVersioningConfig.class)
public class SqlServerTestConfig {

    @Bean
    File filesystemRoot() {
        try {
            return Files.createTempDirectory("").toFile();
        } catch (IOException ioe) {}
        return null;
    }

    @Bean
    FileSystemResourceLoader fileSystemResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }

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
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.SQL_SERVER);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("tests.versioning");  	// Tell Hibernate where to find Entities
        factory.setDataSource(dataSource());

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Value("/org/springframework/versions/jpa/schema-drop-sqlserver.sql")
    private Resource dropVersionSchema;

    @Value("/org/springframework/versions/jpa/schema-sqlserver.sql")
    private Resource createVersionSchema;


    @Bean
    DataSourceInitializer datasourceInitializer() {
        ResourceDatabasePopulator databasePopulator =
                new ResourceDatabasePopulator();

        databasePopulator.addScript(dropVersionSchema);
        databasePopulator.addScript(createVersionSchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource());
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

}
