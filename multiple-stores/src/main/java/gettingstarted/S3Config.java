package gettingstarted;

import org.springframework.content.s3.config.EnableS3Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableS3Stores(basePackages="gettingstarted")
public class S3Config {

    @Bean
    public Object test2() {
        return 1;
    }
}
