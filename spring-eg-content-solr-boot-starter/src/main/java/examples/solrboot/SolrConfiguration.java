package examples.solrboot;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrConfiguration {

    @Bean
    public SolrClient solrClient(SolrProperties props) {
        return new HttpSolrClient.Builder(props.getUrl())
                .build();
    }

}
