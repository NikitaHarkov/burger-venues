package ee.mrnikita.burger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QminderBurgerResponseDto {
    private String urlWithBurger;
    private Set<String> error;

    public String getUrlWithBurger() {
        return urlWithBurger;
    }

    public void setUrlWithBurger(String urlWithBurger) {
        this.urlWithBurger = urlWithBurger;
    }

    public Set<String> getError() {
        return error;
    }

    public void setError(Set<String> error) {
        this.error = error;
    }
}
