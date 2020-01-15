package examples.stores;

import examples.models.Document;

import org.springframework.content.commons.repository.AssociativeStore;
import org.springframework.content.commons.repository.ContentStore;

public interface DocumentContentStore extends ContentStore<Document, String> {
}
