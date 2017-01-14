package examples;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SolrProperties.class)
public class SolrConfiguration {

	@Autowired private SolrProperties props;
	
	public SolrConfiguration() {
	}
	
	@Bean
	public SolrClient solrClient() {
      return new HttpSolrClient(props.getSolrUrl());
	}
}
