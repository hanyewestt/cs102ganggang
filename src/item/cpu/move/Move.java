package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;

public abstract class Move {

    private int expectedValue;
    private int pointsGain;
    private boolean winning;

    public abstract void doMove(Game splendor, CPUPlayer cpu);

    public int getExpectedValue() {
        return expectedValue;
    }

    public int getPointsGain() {
        return pointsGain;
    }

    public boolean getWinning() {
        return winning;
    }

    public void setExpectedValue(int expectedValue) {
        this.expectedValue = expectedValue;
    }

    public void setPointsGain(int pointsGain) {
        this.pointsGain = pointsGain;
    }

    public void setWinning(boolean winning) {
        this.winning = winning;
    }

    public boolean isBetterMove(Move other) {
        if (winning && !(other.getWinning())) {
            return true;
        }

        if (pointsGain != other.getPointsGain()) {
            return pointsGain > other.getPointsGain();
        }

        return expectedValue > other.getExpectedValue();
    }
}
