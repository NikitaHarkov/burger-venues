package ee.mrnikita.burger.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import ee.mrnikita.burger.models.Venue;
import ee.mrnikita.burger.models.VenuePhoto;
import ee.mrnikita.burger.models.dto.PhotoUrlsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VenuePhotoService extends Service {
    private static final Logger log = LoggerFactory.getLogger(VenuePhotoService.class);
    private static final String FOURSQUARE_PHOTO_URL = "https://api.foursquare.com/v2/venues/%s/photos?" +
            "client_id=%s&client_secret=%s&group=%s&v=%s";

    private final QminderApiService qminderApiService;

    public VenuePhotoService(RestTemplate restTemplate, QminderApiService qminderApiService) {
        super(restTemplate);
        this.qminderApiService = qminderApiService;
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
                    if (!photoUrl.isBlank()) {
                        venue.setPhotoUrl(photoUrl);
                        venuesWithPhoto.add(venue);
                    }
                }
            } catch (Exception ex) {
                log.error("BurgerPhotoService - addPhotoUrlToVenues", ex);
                System.out.println("Error in addPhotoUrlToVenues: " + ex);
            }
        }
        return venuesWithPhoto;
    }

    private String parseVenuePhotos(List<VenuePhoto> venuePhotos) {
        List<String> photoUrls = venuePhotos.stream()
                .map(photo -> photo.getPrefix() + photo.getWidth() + "x" + photo.getHeight() + photo.getSuffix())
                .collect(Collectors.toList());
        PhotoUrlsDto photoUrlsDto = new PhotoUrlsDto(photoUrls);
        return qminderApiService.venuePhotoWithBurger(photoUrlsDto);
    }
}
