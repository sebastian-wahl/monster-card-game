package game.http.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class DeckModel implements HttpModel {
    @JsonProperty
    private String[] ids;

    public List<String> getDeckIds() {
        return Arrays.asList(ids);
    }
}
