package examples.stores;

import org.springframework.content.commons.repository.Store;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource(path="docsStore")
public interface DocumentStore extends Store<String> {
}
