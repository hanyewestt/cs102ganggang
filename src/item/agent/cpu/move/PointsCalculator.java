package item.agent.cpu.move;

import item.*;
import item.agent.cpu.*;
import app.*;
import java.util.*;
import util.*;

public class PointsCalculator {

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
                nobleIdx.add(i);
            }
        }

        return points;
    }
}
