package examples;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="spring.eg")
public class SolrProperties {

	private String solrUrl = "http://localhost:8983/solr/solr";

	public String getSolrUrl() {
		return solrUrl;
	}
}
