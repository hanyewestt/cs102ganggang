package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import java.util.*;
import util.*;

/**
 * {@link CPUPlayer} draws {@link Gem}.
 */
public class DrawGems extends Move {

    private HashMap<Gem, Integer> toDraw = Utility.generateEmptyHashmap();
    private HashMap<Gem, Integer> toRemove = Utility.generateEmptyHashmap();

    /**
     * Stores information for {@link CPUPlayer} to draw 2 of {@link Gem}s.
     * 
     * @param cpu {@link CPUPlayer}
     * @param type {@link Gem}
     * @param availNobles {@link NobleTile}s available
     */
    public DrawGems(CPUPlayer cpu, Gem type, List<NobleTile> availNobles) {
        super(cpu);
        HashMap<Gem, Integer> newTokens = Utility.generateHashMapClone(cpu.getTokens());
        newTokens.replace(type, newTokens.get(type) + 2);
        toDraw.replace(type, 2);

        int currGemNo = Utility.getTotalGems(newTokens);
        int gemsToRemove = 0;
        if (currGemNo > 10) {
            gemsToRemove = currGemNo - 10;
            RemoveGems.getGemsToRemove(newTokens, toRemove, currGemNo, cpu, availNobles);
        }

        super.setExpectedValue(ExpectedValueCalculator.getValueLossForRemoval(gemsToRemove)
                + ExpectedValueCalculator.calculateExpectedValue(newTokens, cpu.getBonuses(),
                cpu.getGameState().getMarket(), availNobles, cpu.getReserveHand()));
    }

    /**
     * Stores information for {@link CPUPlayer} to draw 3 of {@link Gem}s.
     * 
     * @param cpu {@link CPUPlayer}
     * @param type1 First {@link Gem}
     * @param type2 Second{@link Gem}
     * @param type3 Third {@link Gem}
     * @param availNobles {@link NobleTile}s available
     */
    public DrawGems(CPUPlayer cpu, Gem type1, Gem type2, Gem type3, List<NobleTile> availNobles) {
        super(cpu);
        HashMap<Gem, Integer> newTokens = Utility.generateHashMapClone(cpu.getTokens());

        newTokens.replace(type1, newTokens.get(type1) + 1);
        newTokens.replace(type2, newTokens.get(type2) + 1);
        newTokens.replace(type3, newTokens.get(type3) + 1);

        toDraw.replace(type1, 1);
        toDraw.replace(type2, 1);
        toDraw.replace(type3, 1);

        int currGemNo = Utility.getTotalGems(newTokens);
        int gemsToRemove = 0;
        if (currGemNo > 10) {
            gemsToRemove = currGemNo - 10;
            RemoveGems.getGemsToRemove(newTokens, toRemove, currGemNo, cpu, availNobles);
        }

        super.setExpectedValue(ExpectedValueCalculator.getValueLossForRemoval(gemsToRemove)
                + ExpectedValueCalculator.calculateExpectedValue(newTokens, cpu.getBonuses(),
                cpu.getGameState().getMarket(), availNobles, cpu.getReserveHand()));
    }

    /**
     * Gets toDraw.
     * 
     * @return HashMap of {@link Gem} and respective quantities.
     */
    public HashMap<Gem, Integer> getToDraw() {
        return toDraw;
    }

    /**
     * Gets toRemove.
     * 
     * @return HashMap of {@link Gem} and thier respective quantities.
     */
    public HashMap<Gem, Integer> getToReturn() {
        return toRemove;
    }

    /**
     * Performs buyCard action and prints message to indicate what move the {@link CPUPlayer} makes. 
     */
    public void doMove() {
        System.out.println("CPU is drawing tokens:"+toDraw);
        cpu.getGameState().drawToken(cpu);
    }
}
