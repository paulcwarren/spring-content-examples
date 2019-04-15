package support.cmis;

import java.util.UUID;

import org.springframework.content.commons.repository.ContentStore;

public interface DocumentStorage extends ContentStore<Document, UUID> {
	//
}
