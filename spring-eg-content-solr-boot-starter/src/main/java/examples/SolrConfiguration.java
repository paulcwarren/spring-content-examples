package examples;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfiguration {

	@Autowired private SolrProperties props;
	
	public SolrConfiguration() {
	}
	
	@Bean
	public SolrClient solrClient() {
      return new HttpSolrClient(props.getSolrUrl());
	}
}
