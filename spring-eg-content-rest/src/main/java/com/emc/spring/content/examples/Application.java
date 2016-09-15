package com.emc.spring.content.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.mongo.config.EnableMongoContentRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import internal.org.springframework.content.rest.config.ContentRestConfiguration;

@SpringBootApplication
@ComponentScan(basePackages={"internal.org.springframework.content.docx4j"})
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	@Configuration
	@EnableMongoRepositories()
	@EnableMongoContentRepositories()
	@Import(ContentRestConfiguration.class)
	public static class MongoRepositoriesConfiguration extends AbstractSpringContentMongoConfiguration
	{
	}
}
