package ee.mrnikita.burger.service;

import ee.mrnikita.burger.models.dto.QminderBurgerResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class QminderApiService extends Service {
    private static final Logger log = LoggerFactory.getLogger(QminderApiService.class);
    private static final String QMINDER_API_URL = "https://pplkdijj76.execute-api.eu-west-1.amazonaws.com/prod/recognize";

    public QminderApiService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String venuePhotoWithBurger(Map<String, List<String>> photoUrls) {
        try {
            QminderBurgerResponseDto response = restTemplate.postForObject(QMINDER_API_URL, photoUrls, QminderBurgerResponseDto.class);
            if (response.getUrlWithBurger() != null)
                return response.getUrlWithBurger();
        } catch (HttpClientErrorException ex) {
            log.error("QminderApiService - venuePhotoWithBurger" + ex);
            System.out.println("Error in venuePhotoWithBurger:  -> " + ex.getMessage());
            return NO_PHOTO;
        }
        return NO_PHOTO;
    }
}
