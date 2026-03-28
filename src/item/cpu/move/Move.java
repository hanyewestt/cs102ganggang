package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;

public abstract class Move {

    private int expectedValue = 0;
    private int pointsGain = 0;
    private boolean winning = false;
    private int nobleIdx = 0;
    public CPUPlayer cpu;

    public abstract void doMove();

    public Move(CPUPlayer cpu) {
        this.cpu = cpu;
    }

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
        return nobleIdx;
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

    public void setNobleIdx(int nobleIdx) {
        this.nobleIdx = nobleIdx;
    }

    public boolean isBetterMove(Move other) {
        if (other instanceof NoPossibleMove) {
            return true;
        }
        if (winning && !(other.getWinning())) {
            return true;
        }

        if (pointsGain != other.getPointsGain()) {
            return pointsGain > other.getPointsGain();
        }

        return expectedValue > other.getExpectedValue();
    }
}
