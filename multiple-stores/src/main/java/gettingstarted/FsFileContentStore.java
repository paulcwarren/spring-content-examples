package gettingstarted;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.fs.store.FilesystemContentStore;
import org.springframework.content.rest.StoreRestResource;

@ConditionalOnProperty(
        value="spring.content.storage",
        havingValue = "fs",
        matchIfMissing = false)
@StoreRestResource
public interface FsFileContentStore extends FilesystemContentStore<File, String> {
}
