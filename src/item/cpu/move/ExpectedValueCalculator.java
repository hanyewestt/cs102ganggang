package item.cpu.move;

import item.*;
import item.cpu.*;
import app.*;
import java.util.*;
import util.*;

public class ExpectedValueCalculator {

    public static int calculateExpectedValue(HashMap<Gem, Integer> tokens, HashMap<Gem, Integer> production,
            Card[][] market, List<NobleTile> nobles, List<Card> reserveHand) {
        int sum = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; i < 4; i++) {
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

        // Gold has a weight of 3
        sum += tokens.get(Gem.Gold) * 3;

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

        sum += c.getPOINTS();

        Gem produce = c.getGEMTYPE();
        for (NobleTile noble : nobles) {
            if (noble.getTokens().get(produce) > 0) {
                sum++;
            }
        }

        return sum;
    }

    public static int getReserveValue(Card c, List<NobleTile> nobles, HashMap<Gem, Integer> production,
            HashMap<Gem, Integer> tokens) {
        int sum = 0;

        Gem type = c.getGEMTYPE();
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

        sum *= c.getPOINTS();

        return sum;
    }
}
