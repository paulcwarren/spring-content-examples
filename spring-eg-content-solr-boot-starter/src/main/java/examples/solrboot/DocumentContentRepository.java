package examples.solrboot;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource(path="documentsContent")
public interface DocumentContentRepository extends ContentStore<Document, String>, Searchable<String> {

}
