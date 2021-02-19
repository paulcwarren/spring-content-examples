package examples.s3;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.spring.core.Credentials;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
public class GCSConfig {

    @Bean
    public static Storage storage(CredentialsProvider credentialsProvider, GcpProjectIdProvider projectIdProvider)
            throws IOException {
        return StorageOptions.newBuilder()
                .setCredentials(credentialsProvider.getCredentials())
                .setProjectId(projectIdProvider.getProjectId()).build().getService();
    }

    @Bean
    public GcpProjectIdProvider gcpProjectIdProvider() {
        return new DefaultGcpProjectIdProvider();
    }

    @Bean
    public CredentialsProvider credentialsProvider() {
        try {
            return new DefaultCredentialsProvider(Credentials::new);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
