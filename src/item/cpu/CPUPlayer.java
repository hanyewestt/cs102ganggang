package item.cpu;

import item.*;
import app.*;
import item.cpu.move.*;
import util.*;

import java.util.*;

public class CPUPlayer extends Player {

    private Move optimalMove;
    private static Game splendor;

    public CPUPlayer(Game splendor) {
        super();
        this.splendor = splendor;
    }

    public Game getGameState() {
        return splendor;
    }

    public Move getMove() {
        return optimalMove;
    }

    public void calculateOptimalMove() {
        Map<Gem, Integer> bank = splendor.getBank();
        ArrayList<Gem> availableGems = new ArrayList<>();
        ArrayList<Integer> possibleNobleIdx = new ArrayList<>();

        ArrayList<NobleTile> gameNoblesCopy = new ArrayList<>();
        for (NobleTile noble : splendor.getNobles()) {
            gameNoblesCopy.add(noble);
        }

        HashMap<Gem, Integer> playerProduction = super.getProduction();

        for (int i = 0; i < gameNoblesCopy.size(); i++) {
            if (Utility.isGreaterOrEqual(playerProduction, gameNoblesCopy.get(i).getTokens())) {
                possibleNobleIdx.add(i);
                gameNoblesCopy.set(i, null);
            }
        }

        for (Gem g : Gem.values()) {
            if (g.equals(Gem.Gold)) {
                continue;
            }

            int amountLeft = bank.get(g);
            if (amountLeft >= 2) {
                DrawGems draw2 = new DrawGems(this, g, gameNoblesCopy);
                optimalMove = draw2.isBetterMove(optimalMove) ? draw2 : optimalMove;
            }
            if (amountLeft >= 1) {
                availableGems.add(g);
            }
        }

        for (int i = 0; i <= availableGems.size() - 3; i++) {
            DrawGems draw3 = new DrawGems(this, availableGems.get(i),
                    availableGems.get(i + 1), availableGems.get(i + 2), gameNoblesCopy);
            optimalMove = draw3.isBetterMove(optimalMove) ? draw3 : optimalMove;
        }

        Card[][] market = splendor.getMarket();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                Card c = market[i][j];
                if (c == null) {
                    continue;
                }

                HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(c.getTokens());
                Utility.discount(cardCost, playerProduction);

                HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(super.getTokens(), cardCost);
                if (tokensToPay != null) {
                    BuyCard buyCard = new BuyCard(this, i + 1, j + 1, tokensToPay, gameNoblesCopy, possibleNobleIdx);
                    optimalMove = buyCard.isBetterMove(optimalMove) ? buyCard : optimalMove;

                    ReserveCard reserveCard = new ReserveCard(this, i + 1, j + 1, gameNoblesCopy);
                    optimalMove = reserveCard.isBetterMove(optimalMove) ? reserveCard : optimalMove;
                }
            }
        }

        for (int i = 0; i < super.getReserveHand().size(); i++) {
            HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(c.getTokens());
            Utility.discount(cardCost, playerProduction);

            HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(super.getTokens(), cardCost);
            if (tokensToPay != null) {
                BuyCard buyCard = new BuyCard(this, i, tokensToPay, gameNoblesCopy, possibleNobleIdx);
                optimalMove = buyCard.isBetterMove(optimalMove) ? buyCard : optimalMove;
            }
        }

        if (possibleNobleIdx.size() != 0) {
            optimalMove.setPointsGain(optimalMove.getPointsGain() + 3);

            optimalMove.setNobleIdx(possibleNobleIdx.get(new Random().nextInt(possibleNobleIdx.size())));
        }

        if (super.getPoints() + optimalMove.getPointsGain() >= 15) {
            optimalMove.setWinning(true);
        }
    }
}
