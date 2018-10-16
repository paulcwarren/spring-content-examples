package examples.versioning;

import org.springframework.content.commons.repository.ContentStore;

import java.util.UUID;

public interface VersionedDocumentStore extends ContentStore<VersionedDocument, UUID> {
}
