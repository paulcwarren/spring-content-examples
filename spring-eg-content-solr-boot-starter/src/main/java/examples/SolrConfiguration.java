package examples;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfiguration {

	public SolrConfiguration() {
	}
	
	@Bean
	public SolrClient solrClient() {
      return new HttpSolrClient("http://localhost:8983/solr/solr");
	}
}
