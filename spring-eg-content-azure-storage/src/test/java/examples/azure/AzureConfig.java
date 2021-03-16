package examples.azure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureConfig {

    @Value("#{environment.AZURE_STORAGE_ENDPOINT}")
    private String endpoint;

    @Value("#{environment.AZURE_STORAGE_CONNECTION_STRING}")
    private String connString;

    @Bean
    public BlobServiceClientBuilder storage() {
            return new BlobServiceClientBuilder()
            .endpoint(endpoint)
            .connectionString(connString);
    }
}
