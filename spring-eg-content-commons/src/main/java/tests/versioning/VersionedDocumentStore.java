package tests.versioning;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.rest.StoreRestResource;

import java.util.UUID;

@StoreRestResource(path="versionedDocumentsContent")
public interface VersionedDocumentStore extends ContentStore<VersionedDocument, UUID> {
}
