package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import java.util.*;
import util.*;

/**
 * {@link CPUPlayer} removes {@link Gem} from their hand.
 */
public class RemoveGems {

    /**
     * If {@link CPUPlayer} has more than 10{@link Gem}s, remove specfied {@link Gem}s.
     * 
     * @param newTokens Current amount of {@link Gem}s {@link CPUPlayer} has.
     * @param toRemove Gems to remove.
     * @param currGemNo Number of {@link Gem}s in newTokens.
     * @param player {@link CPUPlayer}.
     * @param availNoble {@link NobleTile}s available.
     */
    public static void getGemsToRemove(HashMap<Gem, Integer> newTokens, HashMap<Gem, Integer> toRemove, int currGemNo, CPUPlayer player, List<NobleTile> availNobles) {
        while (currGemNo > 10) {
            int bestValue = Integer.MIN_VALUE;
            Gem typeToRemove = Gem.Diamond;
            for (Gem g : Gem.values()) {
                if (newTokens.get(g) == 0) {
                    continue;
                }
                HashMap<Gem, Integer> tokensAfterRemoval = Utility.generateHashMapClone(newTokens);
                tokensAfterRemoval.replace(g, tokensAfterRemoval.get(g) - 1);

                int expectedValue = ExpectedValueCalculator.calculateExpectedValue(tokensAfterRemoval, player.getBonuses(),
                        player.getGameState().getMarket(), availNobles, player.getReserveHand());
                if (expectedValue > bestValue) {
                    bestValue = expectedValue;
                    typeToRemove = g;
                }
            }

            toRemove.replace(typeToRemove, toRemove.get(typeToRemove) + 1);
            newTokens.replace(typeToRemove, newTokens.get(typeToRemove) - 1);
            currGemNo--;
        }
    }
}
