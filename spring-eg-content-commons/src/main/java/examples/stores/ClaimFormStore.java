package examples.stores;

import examples.models.ClaimForm;
import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;
import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;

@ContentStoreRestResource(path="claims")
public interface ClaimFormStore extends ContentStore<ClaimForm, String>, Renderable<ClaimForm> {

}
