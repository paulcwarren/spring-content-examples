package examples.solr;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;

public interface DocumentContentRepository extends ContentStore<Document, String>, Searchable<String> {

}
