package examples;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;

public interface DocumentContentRepository extends ContentStore<Document, Integer>, Searchable<Integer> {

}
