package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import java.nio.channels.NonWritableChannelException;
import util.*;

import java.util.*;

public class ReserveCard extends Move {

    private int row;
    private int column;
    private boolean takingGold;
    private HashMap<Gem, Integer> toReturn = Utility.generateEmptyHashmap();

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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean getTakingGold() {
        return takingGold;
    }

    public HashMap<Gem, Integer> getToReturn() {
        return toReturn;
    }

    public void doMove() {
        System.out.println("CPU is reserving card: "+cpu.getGameState().getMarket()[row][column]);
        cpu.getGameState().reserveCard(cpu);
    }
}
