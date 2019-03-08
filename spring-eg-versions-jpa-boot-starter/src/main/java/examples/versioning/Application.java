package examples.versioning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @EnableJpaRepositories(basePackages={"tests.versioning", "org.springframework.versions"})
    @EnableFilesystemStores(basePackages = "tests.versioning")
    @EntityScan(basePackages = "tests.versioning")
    public static class ApplicationConfiguration {
        //
    }
}