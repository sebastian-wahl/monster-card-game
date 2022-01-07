package game.http.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTradeModel implements HttpModel {
    @JsonProperty("CardToTrade")
    private String cardId;

    @JsonProperty("DesiredCardName")
    private String desiredCardName;

    @JsonProperty("DesiredCoins")
    private int desiredCoins;
}
