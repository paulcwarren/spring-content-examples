package examples.gcs;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

@Configuration
public class GCSConfig {

    static {
        System.setProperty("spring.content.gcp.storage.bucket", "test");
    }

    @Bean
    public static Storage storage()
            throws IOException {
        return LocalStorageHelper.getOptions().getService();
    }
}
