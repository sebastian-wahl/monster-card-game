package game.objects;

import game.objects.enums.CardsEnum;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class Trade {
    private UUID id;
    private User tradeUser;
    private CardBase tradeCard;

    private CardsEnum desiredCard;
    private int desiredCoins;

    private boolean tradFinished;
    private User tradedToUser;
    private CardBase tradedForCard;

    private Timestamp tradedAt;

    @Override
    public String toString() {
        String out =
                "{\"Trade\": {" +
                        "\"Id\": \"" + id + "\", " +
                        "\"TradeFrom\":" + tradeUser.toString() + ", " +
                        "\"Trading\": " + tradeCard.toString() + ", " +
                        "\"TradingForCard\": " + this.formatCardName(desiredCard) + ", " +
                        "\"TradingForCoins\": " + desiredCoins + ", " +
                        "\"TradeFinished\":" + tradFinished;
        if (this.tradFinished) {
            out += ", " +
                    "\"TradedTo\": " + tradedToUser.toString() + ", " +
                    "\"TradedFor\": " + this.formatNoCard(tradedForCard) + ", " +
                    "\"TradedAt\": \"" + tradedAt.toString().substring(0, tradedAt.toString().indexOf(".")) + "\"";
        }
        out += "}}";
        return out;
    }

    private String formatCardName(CardsEnum e) {
        return e == null ? "No card selected" : "\"" + e.getName() + "\"";
    }

    private String formatNoCard(CardBase c) {
        return c == null ? "No card selected" : c.toString();
    }
}
