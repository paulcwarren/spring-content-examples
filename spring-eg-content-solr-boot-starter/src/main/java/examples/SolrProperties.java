package examples;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="examples")
public class SolrProperties {

	private String solrUrl = "http://localhost:8983/solr/solr";

	public String getSolrUrl() {
		return solrUrl;
	}
}
