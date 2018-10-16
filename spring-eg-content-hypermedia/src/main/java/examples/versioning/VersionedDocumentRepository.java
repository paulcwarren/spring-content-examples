package examples.versioning;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.versions.LockingAndVersioningRepository;

public interface VersionedDocumentRepository extends JpaRepository<VersionedDocument, Long>, LockingAndVersioningRepository<VersionedDocument, Long> {
}
