package examples.solrboot;

import org.apache.solr.client.solrj.SolrClient;
import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public SolrClient solr(SolrProperties solrProperties) {
        solrProperties.setUrl(SolrTestContainer.solrUrl());
        solrProperties.setUser("solr");
        solrProperties.setPassword("SolrRocks");
        return SolrTestContainer.getSolrClient();
    }
}
