package examples.versioning;

import examples.config.JpaConfig;
import examples.repositories.LockingRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryBaseClass = LockingRepositoryImpl.class)
public class VersioningConfig extends JpaConfig {



}
