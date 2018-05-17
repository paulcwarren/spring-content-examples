package examples.repositories;

import examples.models.Claim;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "claims", path = "claims")
public interface ClaimRepository extends CrudRepository<Claim, String> {

}
