package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import ee.mrnikita.burger.models.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class BurgerService {
    @Value("${foursquare.client.id}")
    private String client_id;
    @Value("${foursquare.client.secret}")
    private String client_secret;
    @Value("${foursquare.version}")
    private String version;
    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/venues/search?" +
            "client_id=%s&client_secret=%s&query=%s&&near=%s&v=%s";

    private final RestTemplate restTemplate;
    private final BurgerPhotoService photoService;

    @Autowired
    public BurgerService(RestTemplate restTemplate, BurgerPhotoService photoService) {
        this.restTemplate = restTemplate;
        this.photoService = photoService;
    }

    public List<Venue> getVenues() {
        String query = "Burger, Food";
        String city = "Tartu";
        String url = String.format(FOURSQUARE_URL, client_id, client_secret, query, city, version);
        String json = restTemplate.getForObject(url, String.class);
        List<Venue> venues = convertToObject(json);
        return photoService.addPhotoUrlToVenues(venues);
    }

    private List<Venue> convertToObject(String json) {
        Configuration conf = getJsonPathConfig();
        TypeRef<List<Venue>> venueList = new TypeRef<>() {};
        return JsonPath.using(conf).parse(json).read("$.response.venues", venueList);
    }

    private Configuration getJsonPathConfig() {
        return Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .build();
    }
}
