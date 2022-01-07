package game.objects;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatistics {
    @Getter
    private int winCount;
    @Getter
    private int loseCount;
    @Getter
    private int tieCount;

    public void addWin() {
        this.winCount++;
    }

    public void addLose() {
        this.loseCount++;
    }

    public void addTie() {
        this.tieCount++;
    }

    public double getWinRatio() {
        return (this.winCount + 0.0) / (this.loseCount + this.tieCount + 0.0);
    }

    public int getGamesPlayed() {
        return this.winCount + this.loseCount + this.tieCount;
    }

    public UserStatistics copy() {
        return new UserStatistics(this.winCount, this.loseCount, this.tieCount);
    }

}
