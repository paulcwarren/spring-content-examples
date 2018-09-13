package examples.versioning;

import examples.config.JpaConfig;
import internal.org.springframework.versions.AuthenticationFacade;
import internal.org.springframework.versions.LockingService;
import internal.org.springframework.versions.jpa.JpaLockingServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
@EnableJpaRepositories(basePackages={"examples.versioning","internal.org.springframework.versions.jpa"})	 	// Tell Spring Data JPA where to find Repositories
@EnableTransactionManagement
@EnableFilesystemStores
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(examples.app.Application.class, args);
    }

    public class VersioningConfig {

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

        @Bean
        public LockingService versioningService() {
            return new JpaLockingServiceImpl(new JdbcTemplate(dataSource()), transactionManager(), auth()    );
        }

        @Value("/org/springframework/versions/jpa/schema-drop-hsqldb.sql")
        private ClassPathResource dropRepositoryTables;

        @Value("/org/springframework/versions/jpa/schema-hsqldb.sql")
        private ClassPathResource dataRepositorySchema;

        @Bean
        DataSourceInitializer datasourceInitializer(DataSource dataSource) {
            ResourceDatabasePopulator databasePopulator =
                    new ResourceDatabasePopulator();

            databasePopulator.addScript(dropRepositoryTables);
            databasePopulator.addScript(dataRepositorySchema);
            databasePopulator.setIgnoreFailedDrops(true);

            DataSourceInitializer initializer = new DataSourceInitializer();
            initializer.setDataSource(dataSource);
            initializer.setDatabasePopulator(databasePopulator);

            return initializer;
        }

        @Bean
        public AuthenticationFacade auth() {
            return new AuthenticationFacade();
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.HSQL).build();
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.HSQL);
            vendorAdapter.setGenerateDdl(true);

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("examples.versioning");  	// Tell Hibernate where to find Entities
            factory.setDataSource(dataSource());

            return factory;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {

            JpaTransactionManager txManager = new JpaTransactionManager();
            txManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return txManager;
        }

    }
}

