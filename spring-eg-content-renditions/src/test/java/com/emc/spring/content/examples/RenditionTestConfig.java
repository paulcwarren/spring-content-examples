package com.emc.spring.content.examples;


import org.springframework.content.mongo.config.EnableMongoContentRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = {"internal.org.springframework.content.docx4j"})
@EnableMongoRepositories
@EnableMongoContentRepositories
public class RenditionTestConfig extends AbstractSpringContentMongoConfiguration  {

}
