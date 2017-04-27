package examples;

import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableFilesystemStores
@Import(JpaConfig.class)
public class EnableFilesystemContentRepositoriesConfig {
	// 
}
