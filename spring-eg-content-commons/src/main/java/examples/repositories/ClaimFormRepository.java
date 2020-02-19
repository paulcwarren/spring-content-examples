package examples.repositories;

import examples.models.ClaimForm;

import org.springframework.data.repository.CrudRepository;

public interface ClaimFormRepository extends CrudRepository<ClaimForm, Long> {
}
