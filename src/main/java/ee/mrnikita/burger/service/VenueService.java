package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import ee.mrnikita.burger.models.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class VenueService extends Service {
    private static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/venues/search?" +
            "client_id=%s&client_secret=%s&query=%s&&near=%s&v=%s";

    private final VenuePhotoService photoService;

    @Autowired
    public VenueService(RestTemplate restTemplate, VenuePhotoService photoService) {
        super(restTemplate);
        this.photoService = photoService;
    }

    public List<Venue> getVenues() {
        String query = "Burger Food";
        String city = "Tartu";
        String url = String.format(FOURSQUARE_URL, client_id, client_secret, query, city, version);
        String json = restTemplate.getForObject(url, String.class);
        List<Venue> venues = convertToObject(json);
        photoService.addPhotoUrlToVenues(venues);
        return venues;
    }

    private List<Venue> convertToObject(String json) {
        Configuration conf = getJsonPathConfig();
        TypeRef<List<Venue>> venueList = new TypeRef<>() {};
        return JsonPath.using(conf).parse(json).read("$.response.venues", venueList);
    }
}
