package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import ee.mrnikita.burger.models.Venue;
import ee.mrnikita.burger.models.VenuePhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VenuePhotoService extends Service {
    private static final Logger log = LoggerFactory.getLogger(VenuePhotoService.class);
    private static final String NO_BURGER_PHOTO = "https://images-na.ssl-images-amazon.com/images/I/61WEhZbbmSL._AC_SL1194_.jpg";
    private static final String FOURSQUARE_PHOTO_URL = "https://api.foursquare.com/v2/venues/%s/photos?" +
            "client_id=%s&client_secret=%s&v=%s";

    private final QminderApiService qminderApiService;

    public VenuePhotoService(RestTemplate restTemplate, QminderApiService qminderApiService) {
        super(restTemplate);
        this.qminderApiService = qminderApiService;
    }

    public void addPhotoUrlToVenues(List<Venue> venues) {
        for (Venue venue : venues) {
            String id = venue.getId();
            String url = String.format(FOURSQUARE_PHOTO_URL, id, client_id, client_secret, version);
            try {
                String json = restTemplate.getForObject(url, String.class);
                List<VenuePhoto> venuePhotos = parsingPhotoJson(json);
                if (venuePhotos.size() != 0) {
                    String photoUrl = parseVenuePhotos(venuePhotos);
                    if (!photoUrl.equals(NO_PHOTO)) {
                        venue.setPhotoUrl(photoUrl);
                    } else
                        venue.setPhotoUrl(NO_BURGER_PHOTO);
                } else
                    venue.setPhotoUrl(NO_BURGER_PHOTO);
            } catch (Exception ex) {
                log.error("BurgerPhotoService - addPhotoUrlToVenues", ex);
                System.out.println("Error in addPhotoUrlToVenues: " + ex);
            }
        }
    }

    private List<VenuePhoto> parsingPhotoJson(String json) {
        Configuration conf = getJsonPathConfig();
        TypeRef<List<VenuePhoto>> venuePhotoList = new TypeRef<>() {
        };
        return JsonPath.using(conf)
                .parse(json)
                .read("$.response.photos.items", venuePhotoList);
    }

    private String parseVenuePhotos(List<VenuePhoto> venuePhotos) {
        Map<String, List<String>> photoUrls = venuePhotos.stream()
                .map(photo -> photo.getPrefix() + photo.getWidth() + "x" + photo.getHeight() + photo.getSuffix())
                .collect(Collectors.groupingBy(key -> "urls",
                        Collectors.mapping(photos -> photos, Collectors.toList())));
        return qminderApiService.venuePhotoWithBurger(photoUrls);
    }
}
