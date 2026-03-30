package item.agent.cpu.move;

import item.*;
import java.util.*;
import util.*;

public class ExpectedValueCalculator {
    private static final int goldWeight = 6;
    private static final int valueLossPerRemoval = -5;
    private static final int nobleWeight = 3;

    public static int calculateExpectedValue(HashMap<Gem, Integer> tokens, HashMap<Gem, Integer> production,
            Card[][] market, List<NobleTile> nobles, List<Card> reserveHand) {
        int sum = 0;

        for (int i = 0; i < market.length; i++) {
            for (int j = 0; j < market[i].length; j++) {
                Card c = market[i][j];
                if (c == null) {
                    continue;
                }

                sum += getCardValue(c, nobles, production, tokens);
            }
        }

        for (Card c : reserveHand) {
            sum += getCardValue(c, nobles, production, tokens);
        }

        sum += tokens.get(Gem.Gold) * goldWeight;

        return sum;
    }

    public static int getCardValue(Card c, List<NobleTile> nobles, HashMap<Gem, Integer> production,
            HashMap<Gem, Integer> tokens) {
        int sum = 0;

        HashMap<Gem, Integer> cardCost = c.getTokens();

        Utility.discount(tokens, production);

        for (Gem g : Gem.values()) {
            if (g == Gem.Gold) {
                continue;
            }

            int difference = tokens.get(g) - cardCost.get(g);

            sum += difference < 0 ? difference : 0;
        }

        sum += c.getPoints();

        Gem produce = c.getGemType();
        for (NobleTile noble : nobles) {
            if (noble == null) {
                continue;
            }

            if (noble.getTokens().get(produce) > 0) {
                sum += nobleWeight;
            }
        }

        return sum;
    }

    public static int getReserveValue(Card c, List<NobleTile> nobles, HashMap<Gem, Integer> production,
            HashMap<Gem, Integer> tokens) {
        int sum = 0;

        Gem type = c.getGemType();
        for (NobleTile noble : nobles) {
            if (noble.getTokens().get(type) > 0) {
                sum++;
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
            sum -= 5;
        } else if (tokensNeeded < 5) {
            sum += 5 - tokensNeeded;
        }

        sum *= c.getPoints();

        return sum;
    }

    public static int getValueLossForRemoval(int tokenNoToRemove) {
        return tokenNoToRemove * valueLossPerRemoval;
    }
}
