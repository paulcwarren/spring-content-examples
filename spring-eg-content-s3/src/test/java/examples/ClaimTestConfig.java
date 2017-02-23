package examples;


import javax.sql.DataSource;

import com.amazonaws.regions.RegionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.content.s3.config.AbstractS3ContentRepositoryConfiguration;
import org.springframework.content.s3.config.EnableS3ContentRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

@Configuration
@ComponentScan
@EnableJpaRepositories
@EnableS3ContentRepositories
//@EnableContextResourceLoader
@EnableTransactionManagement
public class ClaimTestConfig extends AbstractS3ContentRepositoryConfiguration {

	@Autowired
	private Environment env;

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

	@Override
	public String bucket() {
		return env.getProperty("AWS_BUCKET");
	}

	@Override
	public Region region() {
		return RegionUtils.getRegion(env.getProperty("AWS_REGION"));
	}

}
