package examples.solrboot;


import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;

public interface SOPDocumentContentRepository extends ContentStore<SOPDocument, String>, Searchable<String> {
}
