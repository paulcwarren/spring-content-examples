package examples;
import java.net.URI;

import org.springframework.content.commons.repository.AssociativeStore;
import org.springframework.content.commons.repository.Store;

public interface URIResourceStore extends AssociativeStore<Claim, URI> {

}
