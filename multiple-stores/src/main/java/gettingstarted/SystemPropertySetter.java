package gettingstarted;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SystemPropertySetter {

    @Autowired
    private Environment env;

    @PostConstruct
    public void setProperty() {

        System.setProperty("aws.accessKeyId", env.getProperty("spring.content.aws.accessKeyId"));
        System.setProperty("aws.secretKey", env.getProperty("spring.content.aws.secretKey"));
    }
}
