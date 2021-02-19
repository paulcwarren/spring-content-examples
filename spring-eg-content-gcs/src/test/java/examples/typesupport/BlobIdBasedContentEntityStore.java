package examples.typesupport;

import org.springframework.content.commons.repository.ContentStore;

import com.google.cloud.storage.BlobId;

public interface BlobIdBasedContentEntityStore extends ContentStore<BlobIdBasedContentEntity, BlobId> {

}
