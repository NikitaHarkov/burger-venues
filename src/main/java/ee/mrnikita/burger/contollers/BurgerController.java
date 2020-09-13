package ee.mrnikita.burger.contollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.mrnikita.burger.models.Venue;
import ee.mrnikita.burger.service.BurgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/burgers")
public class BurgerController {
    private final BurgerService burgerService;

    @Autowired
    public BurgerController(BurgerService burgerService) {
        this.burgerService = burgerService;
    }

    @GetMapping
    public List<Venue> list() throws JsonProcessingException {
        return burgerService.getVenues();
    }

}
