package gettingstarted;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.content.s3.store.S3ContentStore;

@ConditionalOnProperty(
        value="spring.content.storage",
        havingValue = "s3",
        matchIfMissing = false)
@StoreRestResource
public interface S3FileContentStore extends S3ContentStore<File, String> {
}
