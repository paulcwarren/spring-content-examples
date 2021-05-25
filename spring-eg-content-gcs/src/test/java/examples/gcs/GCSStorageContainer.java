package examples.gcs;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

public class GCSStorageContainer {

    private static final String DOCKER_IMAGE_NAME = "fsouza/fake-gcs-server:v1.24.0";

    private static Storage storage = null;

    private GCSStorageContainer() {

    }

    public static Storage getStorage() {

        if (storage == null) {
            storage = LocalStorageHelper.getOptions().getService();
        }

        return storage;
    }
}
