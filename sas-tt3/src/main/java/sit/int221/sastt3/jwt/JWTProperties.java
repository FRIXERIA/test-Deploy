package sit.int221.sastt3.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secretKey;
    private int tokenValidity;
    private int refreshTokenValidity;
    private String reFreshTokenSecretKey;
}
