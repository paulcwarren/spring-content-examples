package examples.stores;

import examples.models.Document;
import org.springframework.content.commons.repository.AssociativeStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource(path="docs")
public interface DocumentAssociativeStore extends AssociativeStore<Document, String> {
}
