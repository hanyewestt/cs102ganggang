package item.cpu.move;

import item.*;
import item.move.*;
import app.*;
import java.util.*;

public class MoveUtility {

    public static int calculateExpectedValue(CPUPlayer cpu) {
        int sum = 0;
        Game splendor = cpu.getGameState();
        ArrayList<NobleTile> nobles = splendor.getNobles();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; i < 4; i++) {
                Card c = splendor.getMarket()[i][j];
                sum += getCardValue(c, nobles, cpu);
            }
        }

        for (Card c : cpu.getReserveHand()) {
            sum += getCardValue(c, nobles, cpu);
        }

        return sum;
    }

    public static int getCardValue(Card c, ArrayList<NobleTile> nobles, CPUPlayer cpu) {
        int sum = 0;

        HashMap<Gem, Integer> cardCost = c.getTokens();

        cpu.discountCost(cardCost);

        for (Gem g : Gem.values()) {
            int difference = cpu.getTokens().get(g) - cardCost.get(g);

            sum += difference < 0 ? difference : 0;
        }

        sum += c.getPOINTS();

        Gem production = c.getGEMTYPE();
        for (NobleTile noble : nobles) {
            if (noble.getTokens().get(production) > 0) {
                sum++;
            }
        }

        return sum;
    }
}
