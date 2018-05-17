package examples;

import examples.models.ClaimForm;
import org.springframework.content.commons.repository.AssociativeStore;

public interface URIResourceStore extends AssociativeStore<ClaimForm, String> {

}
