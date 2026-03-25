package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import java.util.*;
import util.*;

public class DrawGems extends Move {

    private HashMap<Gem, Integer> toDraw = Utility.generateEmptyHashmap();
    private HashMap<Gem, Integer> toRemove = Utility.generateEmptyHashmap();

    public DrawGems(CPUPlayer player, Gem type, List<NobleTile> availNobles) {
        HashMap<Gem, Integer> newTokens = Utility.generateHashMapClone(player.getTokens());
        newTokens.replace(type, newTokens.get(type) + 2);
        toDraw.replace(type, 2);

        int currGemNo = Utility.getTotalGems(newTokens);
        if (currGemNo > 10) {
            RemoveGems.getGemsToRemove(newTokens, toRemove, currGemNo, player, availNobles);
        }

        super.setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(newTokens, player.getProduction(),
                player.getGameState().getMarket(), availNobles, player.getReserveHand()));
    }

    public DrawGems(CPUPlayer player, Gem type1, Gem type2, Gem type3, List<NobleTile> availNobles) {
        HashMap<Gem, Integer> newTokens = Utility.generateHashMapClone(player.getTokens());

        newTokens.replace(type1, newTokens.get(type1) + 1);
        newTokens.replace(type2, newTokens.get(type2) + 1);
        newTokens.replace(type3, newTokens.get(type3) + 1);

        toDraw.replace(type1, 1);
        toDraw.replace(type2, 1);
        toDraw.replace(type3, 1);

        int currGemNo = Utility.getTotalGems(newTokens);
        if (currGemNo > 10) {
            RemoveGems.getGemsToRemove(newTokens, toRemove, currGemNo, player, availNobles);
        }

        super.setExpectedValue(ExpectedValueCalculator.calculateExpectedValue(newTokens, player.getProduction(),
                player.getGameState().getMarket(), availNobles, player.getReserveHand()));
    }

    public void doMove(CPUPlayer cpu) {
        // to implement
    }
}
