package item.cpu.move;

import item.*;
import item.cpu.*;
import app.*;
import java.util.*;
import util.*;

/**
 * Calculates points to be gained for performing a {@link Move}.
 */
public class PointsCalculator {

    /**
     * Calculates points to be gained for performing a {@link Move}.
     * 
     * @param currProduction Current levels of {@link Gem} production.
     * @param c {@link Card} to be bought.
     * @param availNobles {@link NobleTiles} available.
     * @param nobleIndx nobleIndx.
     * 
     * @return points.
     */
    public static int calculatePoints(HashMap<Gem, Integer> currProduction, Card c,
            ArrayList<NobleTile> availNobles, ArrayList<Integer> nobleIdx) {
        int points = 0;

        Gem newProduce = c.getGemType();
        currProduction.replace(newProduce, currProduction.get(newProduce) + 1);

        points += c.getPoints();

        for (int i = 0; i < availNobles.size(); i++) {
            NobleTile noble = availNobles.get(i);
            if (noble == null) {
                continue;
            }

            if (Utility.isGreaterOrEqual(currProduction, noble.getTokens())) {
                availNobles.set(i, null);
                nobleIdx.add(i);
            }
        }

        return points;
    }
}
