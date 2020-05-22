package examples.stores;

import examples.models.Document;

import org.springframework.content.commons.repository.AssociativeStore;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.rest.StoreRestResource;

public interface DocumentContentStore extends ContentStore<Document, String> {
}
