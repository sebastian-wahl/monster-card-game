package game.helper.battle;

import game.objects.User;
import game.objects.enums.FightOutcome;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BattleReportHelper {
    private double startEloUser1;
    private double startEloUser2;
    private User user1;
    private User user2;

    // winner == ATTACKER -> user1
    // winner == DEFENDER -> user2
    private FightOutcome fightOutcome;

    private List<Round> rounds;

    public BattleReportHelper() {
        this.rounds = new ArrayList<>();
    }

    public void addRound(Round round) {
        this.rounds.add(round);
    }

    private String getFullReport() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < this.rounds.size(); i++) {
            sb.append(this.rounds.get(i).toString());
            if (i < this.rounds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String getWinnerString() {
        return switch (this.fightOutcome) {
            case ATTACKER -> this.user1.toNameString();
            case DEFENDER -> this.user2.toNameString();
            case TIE -> "\"Tie\"";
        };
    }


    @Override
    public String toString() {
        return "{\"Battle\": {" +
                "\"User1\": " + this.user1.toNameString() + ", " +
                "\"User2\": " + this.user2.toNameString() + ", " +
                "\"Fight outcome\":" + this.getWinnerString() + ", " +
                "\"Rounds played\":" + this.rounds.size() + ", " +
                "\"Detailed Report\":" + this.getFullReport() +
                "}}";
    }
}
