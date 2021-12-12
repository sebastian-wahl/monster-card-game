package game.objects;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
public class UserStatistics {
    @Getter
    private int winCount;
    @Getter
    private int loseCount;
    @Getter
    private int tieCount;

    public UserStatistics() {
    }

    public void addWin() {
        this.winCount++;
    }

    public void addLose() {
        this.loseCount++;
    }

    public void addTie() {
        this.tieCount++;
    }

}
