package game.http.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class TradeModel implements HttpModel {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("CardToTrade")
    private String cardId;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("MinimumDamage")
    private String minDamage;
}
