package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import util.*;

import java.util.*;

public class BuyCard extends Move {

    private int buyLocation; // 1 for market, 2 for reserve
    private int row; // Represents deck no, if 0 it represents reserve hand
    private int column;
    private int reserveIdx;
    private Map<Gem, Integer> toPay;

    public BuyCard(CPUPlayer cpu, int row, int column, HashMap<Gem, Integer> toPay,
            ArrayList<NobleTile> availNobles, ArrayList<Integer> nobleIdx) {
        buyLocation = 1;
        this.row = row;
        this.column = column;
        this.toPay = toPay;

        Card[][] marketAfterBuying = Utility.generateMarketClone(cpu.getGameState().getMarket());
        Card c = marketAfterBuying[row - 1][column - 1];
        marketAfterBuying[row - 1][column - 1] = null;

        HashMap<Gem, Integer> tokensAfterBuying = Utility.generateHashMapClone(cpu.getTokens());
        Utility.subtract(tokensAfterBuying, toPay);

        HashMap<Gem, Integer> currProduction = Utility.generateHashMapClone(cpu.getProduction());

        setPointsGain(PointsCalculator.calculatePoints(currProduction, c, availNobles, nobleIdx));
        setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(tokensAfterBuying, currProduction, marketAfterBuying,
                availNobles, cpu.getReserveHand()));
    }

    public BuyCard(CPUPlayer cpu, int reserveIdx, HashMap<Gem, Integer> toPay,
            ArrayList<NobleTile> availNobles, ArrayList<Integer> nobleIdx) {
        buyLocation = 2;
        this.reserveIdx = reserveIdx;
        this.toPay = toPay;

        ArrayList<Card> reserveAfterBuying = new ArrayList<>();
        for (Card c : cpu.getReserveHand()) {
            reserveAfterBuying.add(c);
        }

        Card c = reserveAfterBuying.get(reserveIdx);
        reserveAfterBuying.remove(reserveIdx);

        HashMap<Gem, Integer> tokensAfterBuying = Utility.generateHashMapClone(cpu.getTokens());
        Utility.subtract(tokensAfterBuying, toPay);

        HashMap<Gem, Integer> currProduction = Utility.generateHashMapClone(cpu.getProduction());

        setPointsGain(PointsCalculator.calculatePoints(currProduction, c, availNobles, nobleIdx));
        setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(tokensAfterBuying, currProduction,
                cpu.getGameState().getMarket(), availNobles, reserveAfterBuying));
    }

    public int getBuyLocation() {
        return buyLocation;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getReserveIdx() {
        return reserveIdx;
    }

    public Map<Gem, Integer> getToPay() {
        return toPay;
    }

    public void doMove(CPUPlayer cpu) {

    }
}
