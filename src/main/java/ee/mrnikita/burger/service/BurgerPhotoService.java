package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import ee.mrnikita.burger.models.Venue;
import ee.mrnikita.burger.models.VenuePhoto;
import ee.mrnikita.burger.models.dto.PhotoUrlsDto;
import ee.mrnikita.burger.models.dto.QminderBurgerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class BurgerPhotoService {
    @Value("${foursquare.client.id}")
    private String client_id;
    @Value("${foursquare.client.secret}")
    private String client_secret;
    @Value("${foursquare.version}")
    private String version;

    private static final String FOURSQUARE_PHOTO_URL = "https://api.foursquare.com/v2/venues/%s/photos?" +
            "client_id=%s&client_secret=%s&group=%s&v=%s";
    private static final String QMINDER_API_URL = "https://pplkdijj76.execute-api.eu-west-1.amazonaws.com/prod/recognize";

    private final RestTemplate restTemplate;

    @Autowired
    public BurgerPhotoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Venue> addPhotoUrlToVenues(List<Venue> venues) {
        List<Venue> venuesWithPhoto = new ArrayList<>();
        String group = "venue";
        for (Venue venue : venues) {
            String id = venue.getId();
            String url = String.format(FOURSQUARE_PHOTO_URL, id, client_id, client_secret, group, version);
            String json = restTemplate.getForObject(url, String.class);
            Configuration conf = getJsonPathConfig();
            TypeRef<List<VenuePhoto>> venuePhotoList = new TypeRef<>() {
            };
            try {
                List<VenuePhoto> venuePhotos = JsonPath.using(conf)
                        .parse(json)
                        .read("$.response.photos.items", venuePhotoList);
                if (venuePhotos.size() != 0) {
                    String photoUrl = parseVenuePhotos(venuePhotos);
                    if(!photoUrl.isBlank()){
                        venue.setPhotoUrl(photoUrl);
                        venuesWithPhoto.add(venue);
                    }
                }
            } catch (Exception ex) {
                System.out.printf("%s - Has no picture%n", venue.getName());
            }
        }
        return venuesWithPhoto;
    }

    private String parseVenuePhotos(List<VenuePhoto> venuePhotos) {
        List<String> photoUrls = new ArrayList<>();
        for (VenuePhoto venuePhoto : venuePhotos) {
            String photoUrl = venuePhoto.getPrefix() +
                    venuePhoto.getWidth() + "x" + venuePhoto.getHeight() +
                    venuePhoto.getSuffix();
            photoUrls.add(photoUrl);
        }
        PhotoUrlsDto photoUrlsDto = new PhotoUrlsDto(photoUrls);

        return venuePhotoWithBurger(photoUrlsDto);
    }

    private String venuePhotoWithBurger(PhotoUrlsDto photoUrls) {
        try {
            QminderBurgerResponseDto response = restTemplate.postForObject(QMINDER_API_URL, photoUrls, QminderBurgerResponseDto.class);
            if (response.getUrlWithBurger() != null)
                return response.getUrlWithBurger();
        } catch (HttpClientErrorException ex) {
            System.out.println(photoUrls.getUrls() + " " + ex);
            return "";
        }
        return "";
    }

    private Configuration getJsonPathConfig() {
        return Configuration
                .builder()
                .mappingProvider(new JacksonMappingProvider())
                .build();
    }
}
