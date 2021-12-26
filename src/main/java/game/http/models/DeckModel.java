package game.http.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckModel implements HttpModel {
    private String[] ids;

    public List<String> getDeckIds() {
        return Arrays.asList(ids);
    }
}
