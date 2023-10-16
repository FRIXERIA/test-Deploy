package sit.int221.sastt3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import sit.int221.sastt3.jwt.JWTProperties;
import sit.int221.sastt3.repositories.CustomRepositoryClass;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryClass.class)
@EnableConfigurationProperties({JWTProperties.class})
public class SasTt3Application {

    public static void main(String[] args) {
        SpringApplication.run(SasTt3Application.class, args);
    }

}
