package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import util.*;

import java.util.*;

/**
 * {@link CPUPlayer} reserves {@link Card}.
 */
public class ReserveCard extends Move {

    private int row;
    private int column;
    private boolean takingGold;
    private HashMap<Gem, Integer> toReturn = Utility.generateEmptyHashmap();

    /**
     * Stores information for {@link CPUPlayer} to reserve {@link Card}.
     * 
     * @param cpu {@link CPUPlayer}.
     * @param row Row of {@link Card} in market.
     * @param column Column of {@link Card} in market.
     * @param nobleTiles {@link NobleTile} available.
     * 
     */
    public ReserveCard(CPUPlayer cpu, int row, int column, List<NobleTile> nobleTiles) {
        super(cpu);
        this.row = row;
        this.column = column;

        Card[][] marketAfterReserving = Utility.generateMarketClone(cpu.getGameState().getMarket());
        Card c = marketAfterReserving[row][column];
        marketAfterReserving[row][column] = null;

        ArrayList<Card> handAfterReserving = new ArrayList<>();
        for (Card reserveCard : cpu.getReserveHand()) {
            handAfterReserving.add(reserveCard);
        }
        handAfterReserving.add(c);

        HashMap<Gem, Integer> tokensAfterReserving = Utility.generateHashMapClone(cpu.getTokens());
        if (cpu.getGameState().getBank().get(Gem.Gold) > 0) {
            takingGold = true;

            tokensAfterReserving.replace(Gem.Gold, tokensAfterReserving.get(Gem.Gold) + 1);

            int totalGemCount = Utility.getTotalGems(tokensAfterReserving);
            if (totalGemCount > 10) {
                RemoveGems.getGemsToRemove(tokensAfterReserving, toReturn, totalGemCount, cpu, nobleTiles);
            }
        }

        setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(tokensAfterReserving, cpu.getBonuses(),
                marketAfterReserving, nobleTiles, handAfterReserving)
                + ExpectedValueCalculator.getReserveValue(c, nobleTiles, cpu.getBonuses(), tokensAfterReserving));

    }

    /**
     * Gets row of {@link Card} (1, 2, 3) in market.
     * 
     * @return Row of {@link Card} in market.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets column of {@link Card} (1, 2, 3, 4) in market.
     * 
     * @return Column of {@link Card} in market.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets takingGold.
     * 
     * @return True if there is gold to draw. False if otherwise.
     */
    public boolean getTakingGold() {
        return takingGold;
    }

    /**
     * Gets tokens that {@link CPUPlayer} needs to return.
     * 
     * @return HashMap of {@link Gem} and respective quantities.
     */
    public HashMap<Gem, Integer> getToReturn() {
        return toReturn;
    }

    /**
     * Performs reserveCard action and prints message to indicate what move the {@link CPUPlayer} makes.
     */
    public void doMove() {
        System.out.println("CPU is reserving card: "+cpu.getGameState().getMarket()[row][column]);
        cpu.getGameState().reserveCard(cpu);
    }
}
