package examples.typesupport;

import org.springframework.content.azure.config.BlobId;
import org.springframework.content.commons.repository.ContentStore;

public interface BlobIdBasedContentEntityStore extends ContentStore<BlobIdBasedContentEntity, BlobId> {

}
