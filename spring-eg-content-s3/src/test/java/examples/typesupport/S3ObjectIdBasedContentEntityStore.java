package examples.typesupport;

import org.springframework.content.s3.S3ObjectId;
import org.springframework.content.commons.repository.ContentStore;

public interface S3ObjectIdBasedContentEntityStore extends ContentStore<S3ObjectIdBasedContentEntity, S3ObjectId> {

}
