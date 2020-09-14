package ee.mrnikita.burger.models.dto;

import java.io.Serializable;
import java.util.List;

public class PhotoUrlsDto implements Serializable {
    private List<String> urls;

    public PhotoUrlsDto(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
