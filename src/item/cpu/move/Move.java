package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;

public abstract class Move {

    private int expectedValue;
    private int pointsGain;
    private boolean winning;
    private int nobleIdx;

    public abstract void doMove(CPUPlayer cpu);

    public int getExpectedValue() {
        return expectedValue;
    }

    public int getPointsGain() {
        return pointsGain;
    }

    public boolean getWinning() {
        return winning;
    }

    public int getNobleIdx() {

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
