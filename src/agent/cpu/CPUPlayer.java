package agent.cpu;

import agent.*;
import agent.cpu.move.*;
import app.*;
import item.*;
import util.*;

import java.util.*;

/**
 * Represents the {@link CPUPlayer} which extends {@link Player}.
 * {@link CPUPlayer} can perform all actions that a {@link Player} can do.
 */
public class CPUPlayer extends Player {

    private Move optimalMove;
    private static Game splendor;

    /**
     * {@link CPUPlayer} starts with 0 Gems for every type. {@link CPUPlayer}
     * has a name and turn order.
     *
     * @param name Name of {@link CPUPlayer}
     * @param order {@link CPUPlayer} order number
     */
    public CPUPlayer(String name, int order) {
        super(name, order);
    }

    /**
     * {@link CPUPlayer} starts with 0 Gems for every type. {@link CPUPlayer}
     * has a name and turn order.
     *
     * @param name Name of {@link CPUPlayer}
     * @param order {@link CPUPlayer} order number
     * @param splendor Game that {@link CPUPlayer} is playing in
     */
    public CPUPlayer(Game splendor, String name, int order) {
        super(name, order);
        this.splendor = splendor;
    }

    /**
     * Sets game which {@link CPUPlayer} is playing in.
     *
     * @param splendor Game that {@link CPUPlayer} is playing in
     */
    public void setGame(Game splendor) {
        this.splendor = splendor;
    }

    /**
     * Gets current {@link Game}.
     *
     * @return {@link Game}
     */
    public Game getGameState() {
        return splendor;
    }

    /**
     * Gets optimal move that {@link CPUPlayer} can perform.
     *
     * @return optimal {@link Move}
     */
    public Move getMove() {
        return optimalMove;
    }

    /**
     * Calculates most optimal move that {@link CPUPlayer} can perform in this
     * turn.
     */
    public void calculateOptimalMove() {
        optimalMove = new NoPossibleMove(this);

        Map<Gem, Integer> bank = splendor.getBank();
        ArrayList<Gem> availableGems = new ArrayList<>();
        ArrayList<Integer> possibleNobleIdx = new ArrayList<>();

        ArrayList<NobleTile> gameNoblesCopy = new ArrayList<>();
        for (NobleTile noble : splendor.getNobles()) {
            gameNoblesCopy.add(noble);
        }

        HashMap<Gem, Integer> playerBonuses = super.getBonuses();

        for (int i = 0; i < gameNoblesCopy.size(); i++) {
            if (Utility.isGreaterOrEqual(playerBonuses, gameNoblesCopy.get(i).getTokens())) {
                possibleNobleIdx.add(i);
                gameNoblesCopy.set(i, null);
            }
        }

        for (Gem g : Gem.values()) {
            if (g.equals(Gem.Gold)) {
                continue;
            }

            int amountLeft = bank.get(g);

            if (amountLeft >= 4) {
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
                Utility.discount(cardCost, playerBonuses);

                HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(super.getTokens(), cardCost);
                if (tokensToPay != null) {
                    BuyCard buyCard = new BuyCard(this, i, j, tokensToPay, gameNoblesCopy);
                    optimalMove = buyCard.isBetterMove(optimalMove) ? buyCard : optimalMove;
                }
                if (super.getReserveHandSize() < Player.MAX_RESERVE_HAND_SIZE) {
                    ReserveCard reserveCard = new ReserveCard(this, i, j, gameNoblesCopy);
                    optimalMove = reserveCard.isBetterMove(optimalMove) ? reserveCard : optimalMove;
                }

            }
        }

        for (int i = 0; i < super.getReserveHand().size(); i++) {
            HashMap<Gem, Integer> cardCost = Utility.generateHashMapClone(super.getReserveHand().get(i).getTokens());
            Utility.discount(cardCost, playerBonuses);

            HashMap<Gem, Integer> tokensToPay = Utility.findSubtractionAmount(super.getTokens(), cardCost);
            if (tokensToPay != null) {
                BuyCard buyCard = new BuyCard(this, i, tokensToPay, gameNoblesCopy);
                optimalMove = buyCard.isBetterMove(optimalMove) ? buyCard : optimalMove;
            }
        }

        if (optimalMove instanceof BuyCard bc) {
            for (int num : bc.getPossibleNobleIdx()) {
                possibleNobleIdx.add(num);
            }
        }

        if (possibleNobleIdx.size() != 0) {
            optimalMove.setPointsGain(optimalMove.getPointsGain() + NobleTile.getPoints());
            optimalMove.setNobleIdx(possibleNobleIdx.get(new Random().nextInt(possibleNobleIdx.size())));
        }

        if (super.getPoints() + optimalMove.getPointsGain() >= splendor.pointsToWin) {
            optimalMove.setWinning(true);
        }
    }

    /**
     * Updates points, production levels, and the {@link Player}’s remaining
     * tokens after the purchase of the {@link Card}.
     *
     * @param card {@link Card} to be purchased
     * @param toPay Tokens that {@link CPUPlayer} has to pay to buy
     * {@link Card}
     *
     * @return True if able to buy {@link Card}. False if otherwise.
     */
    public boolean buyCard(Card card, HashMap<Gem, Integer> toPay) {
        HashMap<Gem, Integer> remainingGems = Utility.generateEmptyHashmap();
        HashMap<Gem, Integer> playerTokens = getTokens();

        for (Gem g : Gem.values()) {
            remainingGems.put(g, playerTokens.get(g) - toPay.get(g));
        }
        super.addCard(card, remainingGems);
        return true;
    }

}
