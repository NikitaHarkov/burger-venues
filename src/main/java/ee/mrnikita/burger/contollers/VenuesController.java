package ee.mrnikita.burger.contollers;

import ee.mrnikita.burger.models.Venue;
import ee.mrnikita.burger.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/burgers")
public class VenuesController {
    private final VenueService venueService;

    @Autowired
    public VenuesController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public List<Venue> list() {
        return venueService.getVenues();
    }

}
