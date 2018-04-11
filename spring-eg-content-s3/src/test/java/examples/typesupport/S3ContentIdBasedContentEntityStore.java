package examples.typesupport;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.s3.S3ContentId;

public interface S3ContentIdBasedContentEntityStore extends ContentStore<S3ContentIdBasedContentEntity, S3ContentId> {

}
