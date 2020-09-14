package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class Service {
    @Value("${foursquare.client.id}")
    protected String client_id;
    @Value("${foursquare.client.secret}")
    protected String client_secret;
    @Value("${foursquare.version}")
    protected String version;

    protected final RestTemplate restTemplate;

    @Autowired
    public Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected Configuration getJsonPathConfig() {
        return Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .build();
    }
}
