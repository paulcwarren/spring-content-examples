package examples.gcs;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

@Configuration
public class GCSConfig {

    private static final String BUCKET_NAME = "test";

    static {
        System.setProperty("spring.content.gcp.storage.bucket", BUCKET_NAME);
    }

    @Bean
    public static Storage storage()
            throws IOException {
        return LocalStorageHelper.getOptions().getService();
    }

    @Bean
    public String bucketName() {
        return BUCKET_NAME;
    }
}
