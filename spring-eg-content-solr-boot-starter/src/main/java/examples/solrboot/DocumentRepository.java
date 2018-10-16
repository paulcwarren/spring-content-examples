package examples.solrboot;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends MongoRepository<Document, Long> {

}
