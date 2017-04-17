package examples.typesupport;

import java.util.UUID;

import org.springframework.content.commons.repository.ContentStore;

public interface UUIDBasedContentEntityStore extends ContentStore<UUIDBasedContentEntity, UUID> {

}
