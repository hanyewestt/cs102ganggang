package item.cpu.move;

import app.*;
import item.*;
import agent.*;

/**
 * Abstract class for all moves the {@link CPUPlayer} performs.
 */
public abstract class Move {

    private int expectedValue = 0;
    private int pointsGain = 0;
    private boolean winning = false;
    private int nobleIdx = 0;
    public CPUPlayer cpu;

    /**
     * Default constructor of {@link Move}.
     */
    public abstract void doMove();

    /**
     * Move performed by {@link CPUPlayer}.
     *
     * @param cpu {@link CPUPlayer}.
     */
    public Move(CPUPlayer cpu) {
        this.cpu = cpu;
    }

    /**
     * Gets expectedValue.
     *
     * @return expectedValue.
     */
    public int getExpectedValue() {
        return expectedValue;
    }

    /**
     * Gets pointsGain.
     *
     * @return pointsGain.
     */
    public int getPointsGain() {
        return pointsGain;
    }

    /**
     * Gets winning.
     *
     * @return winning.
     * True if after move is done, {@link CPUPlayer} has 15 or more points. False if otherwise.
     */
    public boolean getWinning() {
        return winning;
    }

    /**
     * Gets nobleIdx.
     *
     * @return nobleIdx.
     */
    public int getNobleIdx() {
        return nobleIdx;
    }

    /**
     * Sets expectedValue.
     *
     * @param expectedValue value to be set.
     */
    public void setExpectedValue(int expectedValue) {
        this.expectedValue = expectedValue;
    }

    /**
     * Sets pointsGain.
     *
     * @param pointsGain value to be set.
     */
    public void setPointsGain(int pointsGain) {
        this.pointsGain = pointsGain;
    }

    /**
     * Sets winning.
     *
     * @param winning boolean value to be set.
     * True if after move is done, {@link CPUPlayer} has 15 or more points. False if otherwise.
     */
    public void setWinning(boolean winning) {
        this.winning = winning;
    }

    /**
     * Sets nobleIdx.
     *
     * @param nobleIdx value to be set.
     */
    public void setNobleIdx(int nobleIdx) {
        this.nobleIdx = nobleIdx;
    }

    /**
     * Compares if a {@link Move} is better than another {@link Move}.
     *
     * @param other Another {@link Move} to be compared with.
     *
     * @return True if this {@link Move} is better than other {@link Move}. False if otherwise.
     */
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
