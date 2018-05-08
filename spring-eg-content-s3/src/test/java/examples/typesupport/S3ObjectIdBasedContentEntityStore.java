package examples.typesupport;

import com.amazonaws.services.s3.model.S3ObjectId;
import org.springframework.content.commons.repository.ContentStore;

public interface S3ObjectIdBasedContentEntityStore extends ContentStore<S3ObjectIdBasedContentEntity, S3ObjectId> {

}
