package ecnu.edu.iotbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HAConfig {

    @Value("${ha.api.url}")
    private String haApiUrl;

    @Value("${ha.api.token}")
    private String haApiToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getHaApiUrl() {
        return haApiUrl;
    }

    public String getHaApiToken() {
        return haApiToken;
    }
}
