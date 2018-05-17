package examples.solr;

import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.content.jpa.config.EnableJpaContentRepositories;
import org.springframework.content.jpa.config.EnableJpaStores;
import org.springframework.content.solr.EnableFullTextSolrIndexing;
import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableFullTextSolrIndexing
@EnableJpaRepositories
@EnableJpaStores
public class SolrConfig{

    @Autowired
    SolrProperties solrProperties;

    @Bean
    public ConversionService conversionService() {
        return new DefaultFormattingConversionService();
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
        factory.setPackagesToScan(getClass().getPackage().getName());
        factory.setDataSource(dataSource());

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(solrProperties.getUrl())
                .build();
    }

    @Value("/org/springframework/content/jpa/schema-drop-hsqldb.sql")
    private Resource dropReopsitoryTables;

    @Value("/org/springframework/content/jpa/schema-hsqldb.sql")
    private Resource dataReopsitorySchema;

    @Bean
    DataSourceInitializer datasourceInitializer(DataSource dataSource) {
        ResourceDatabasePopulator databasePopulator =
                new ResourceDatabasePopulator();

        databasePopulator.addScript(dropReopsitoryTables);
        databasePopulator.addScript(dataReopsitorySchema);
        databasePopulator.setIgnoreFailedDrops(true);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }
}
