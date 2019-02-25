package de.propra2.ausleiherino24.email;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private Map<String, String> properties = new HashMap<>();
}
