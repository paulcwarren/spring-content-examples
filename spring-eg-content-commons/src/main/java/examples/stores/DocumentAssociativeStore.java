package examples.stores;

import examples.models.Document;
import org.springframework.content.commons.repository.AssociativeStore;

public interface DocumentAssociativeStore extends AssociativeStore<Document, String> {
}
