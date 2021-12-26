package game.helper.battle;

import game.objects.CardBase;
import game.objects.enums.FightOutcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {
    private CardBase monster1;
    private CardBase monster2;
    private String user1;
    private String user2;

    private int roundNumber;

    private FightOutcome fightOutcome;


    private String getFightOutcomeString() {
        return switch (this.fightOutcome) {
            case ATTACKER -> "{\"" + this.user1 + "\": " + this.monster1.toString() + "}";
            case DEFENDER -> "{\"" + this.user2 + "\": " + this.monster2.toString() + "}";
            case TIE -> "Tie";
        };
    }

    @Override
    public String toString() {
        return "{\"Round\": {" +
                "\"Round number\": " + this.roundNumber + ", " +
                "\"Monsters\": {\"" + this.user1 + "\": " + this.monster1.toString() + ", \"" + this.user2 + "\": " + this.monster2.toString() + "}, " +
                "\"Round outcome\": " + this.getFightOutcomeString() +
                "}}";
    }
}
