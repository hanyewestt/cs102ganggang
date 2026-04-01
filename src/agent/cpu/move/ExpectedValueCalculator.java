package agent.cpu.move;

import java.util.*;

import item.*;
import agent.cpu.*;
import util.*;

/**
 * Calculates expected value of performing a {@link Move}.
 */
public class ExpectedValueCalculator {

    private static final int goldWeight = 3;
    private static final int valueLossPerRemoval = -10;
    private static final int nobleWeight = 2;

    /**
     * Calculates expected value of performing a move without considering
     * points.
     *
     * @param tokens {@link CPUPlayer} current tokens.
     * @param production {@link CPUPlayer} current production levels.
     * @param market {@link Card}s in the market.
     * @param nobles {@link NobleTile}s available in game.
     * @param reserveHand {@link Card}s in reserve hand.
     *
     * @return Expected value of {@link Move}.
     */
    public static int calculateExpectedValue(HashMap<Gem, Integer> tokens, HashMap<Gem, Integer> production,
            Card[][] market, List<NobleTile> nobles, List<Card> reserveHand) {
        int sum = 0;

        for (int i = 0; i < market.length; i++) {
            for (int j = 0; j < market[i].length; j++) {
                Card c = market[i][j];
                if (c == null) {
                    continue;
                }

                sum += getCardValue(c, production, tokens);
            }
        }

        for (Card c : reserveHand) {
            sum += getCardValue(c, production, tokens);
        }

        for (NobleTile noble : nobles) {
            for (Gem g : Gem.values()) {
                int progressToNoble = Math.min(production.get(g), noble.getTokens().get(g));

                sum += progressToNoble * nobleWeight;
            }
        }

        sum += tokens.get(Gem.Gold) * goldWeight;

        return sum;
    }

    /**
     * Gets value of {@link Card} in market.
     *
     * @param c {@link Card} in the market.
     * @param production {@link Gem} production levels.
     * @param tokens {@link Gem}s owned by {@link CPUPlayer}.
     *
     * @return {@link Card} value.
     */
    public static int getCardValue(Card c, HashMap<Gem, Integer> production, HashMap<Gem, Integer> tokens) {
        int sum = 0;

        HashMap<Gem, Integer> cardCost = c.getTokens();

        Utility.discount(cardCost, production);

        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }

            int progressToCard = Math.min(tokens.get(g), cardCost.get(g));

            sum += progressToCard;
        }

        return sum;
    }

    /**
     * Gets value of {@link Card} in reserve hand.
     *
     * @param c {@link Card} in the reserve hand
     * @param nobles {@link NobleTile}s available
     * @param production {@link Gem} production levels
     * @param tokens {@link Gem} owned by {@link CPUPlayer}
     *
     * @return Reserve Card value.
     */
    public static int getReserveValue(Card c, List<NobleTile> nobles, HashMap<Gem, Integer> production,
            HashMap<Gem, Integer> tokens) {
        int sum = 0;

        Gem type = c.getGemType();
        for (NobleTile noble : nobles) {
            if (noble == null) {
                continue;
            }

            if (noble.getTokens().get(type) > 0) {
                sum += nobleWeight;
            }
        }

        HashMap<Gem, Integer> discountCost = Utility.generateHashMapClone(c.getTokens());
        Utility.discount(discountCost, production);

        int tokensNeeded = 0;
        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                tokensNeeded -= tokens.get(g);
            } else {
                int difference = discountCost.get(g) - tokens.get(g);
                tokensNeeded += difference < 0 ? 0 : difference;
            }
        }

        if (tokensNeeded < 0) {
            sum -= 3;
        } else if (tokensNeeded < 3) {
            sum += 3 - tokensNeeded;
        } else {
            sum -= 10;
        }

        sum *= c.getPoints();

        return sum;
    }

    /**
     * Calculate loss for removing tokens.
     *
     * @param tokenNoToRemove Token number to be removed
     *
     * @return Value lost
     */
    public static int getValueLossForRemoval(int tokenNoToRemove) {
        return tokenNoToRemove * valueLossPerRemoval;
    }
}
