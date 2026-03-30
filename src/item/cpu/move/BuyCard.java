package item.cpu.move;

import app.*;
import item.*;
import agent.*;
import util.*;

import java.util.*;

/**
 * {@link CPUPlayer} buys {@link Card}.
 */
public class BuyCard extends Move {

    private int buyLocation; // 1 for market, 2 for reserve
    private int row; // Represents deck no, if 0 it represents reserve hand
    private int column;
    private int reserveIdx;
    private Map<Gem, Integer> toPay;

    /**
     *  Stores information for {@link CPUPlayer} to buy {@link Card} from market.
     * 
     * @param cpu {@link CPUPlayer}
     * @param row Row of the {@link CPUPlayer}
     * @param column Column of the {@link CPUPlayer}
     * @param toPay HashMap of tokens needed
     * @param availNobles {@link NobleTile}s play
     * @param nobleIdx {@link NobleTile} index
     */
    public BuyCard(CPUPlayer cpu, int row, int column, HashMap<Gem, Integer> toPay,
            ArrayList<NobleTile> availNobles, ArrayList<Integer> nobleIdx) {
        super(cpu);
        buyLocation = 1;
        this.row = row;
        this.column = column;
        this.toPay = toPay;

        Card[][] marketAfterBuying = Utility.generateMarketClone(cpu.getGameState().getMarket());
        Card c = marketAfterBuying[row][column];
        marketAfterBuying[row][column] = null;

        HashMap<Gem, Integer> tokensAfterBuying = Utility.generateHashMapClone(cpu.getTokens());
        Utility.subtract(tokensAfterBuying, toPay);

        HashMap<Gem, Integer> currBonuses = Utility.generateHashMapClone(cpu.getBonuses());

        setPointsGain(PointsCalculator.calculatePoints(currBonuses, c, availNobles, nobleIdx));
        setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(tokensAfterBuying, currBonuses, marketAfterBuying,
                availNobles, cpu.getReserveHand()));
    }

    /**
     *  Stores information for {@link CPUPlayer} to buy {@link Card} from reserve hand.
     * 
     * @param cpu {@link CPUPlayer}
     * @param reserveIdx Reserve hand index.
     * @param toPay HashMap of tokens needed
     * @param availNobles {@link NobleTile}s play
     * @param nobleIdx {@link NobleTile} index
     */
    public BuyCard(CPUPlayer cpu, int reserveIdx, HashMap<Gem, Integer> toPay,
            ArrayList<NobleTile> availNobles, ArrayList<Integer> nobleIdx) {
        super(cpu);
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

        HashMap<Gem, Integer> currBonuses = Utility.generateHashMapClone(cpu.getBonuses());

        setPointsGain(PointsCalculator.calculatePoints(currBonuses, c, availNobles, nobleIdx));
        setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(tokensAfterBuying, currBonuses,
                cpu.getGameState().getMarket(), availNobles, reserveAfterBuying));
    }

    /**
     * Gets location of {@link Card}.
     * 1 for market.
     * 2 for reserve.
     * 
     * @return buyLocation.
     */
    public int getBuyLocation() {
        return buyLocation;
    }

    /**
     * Gets row of {@link Card} in market.
     * Represents deck no. (1, 2, 3) or reserve hand (0).
     * 
     * @return row.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets column of {@link Card} (1, 2, 3, 4) in market.
     * 
     * @return column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets reserve index.
     * 
     * @return reserveIdx.
     */
    public int getReserveIdx() {
        return reserveIdx;
    }

    /**
     * Gets tokens that {@link CPUPlayer} needs to pay.
     * 
     * @return Map of {@link Gem} and respective quantities.
     */
    public Map<Gem, Integer> getToPay() {
        return toPay;
    }

    /**
     * Performs buyCard action and prints message to indicate what move the {@link CPUPlayer} makes.
     */
    public void doMove() {
        if (buyLocation == 1) {
            System.out.println("CPU is buying card from market" + cpu.getGameState().getMarket()[row][column]);
        } else {
            System.out.println("CPU is buying card from reserve" + cpu.getReserveHand().get(reserveIdx));
        }
        cpu.getGameState().buyCard(cpu);
    }
}
