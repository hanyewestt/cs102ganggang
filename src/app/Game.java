package app;

import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import config.*;
import display.*;
import item.*;
import item.cpu.*;
import item.cpu.move.*;
import java.awt.geom.GeneralPath;
import jdk.jshell.execution.Util;
import util.*;

public class Game {

    private static int playerNumber;
    private static int cpuNumber;
    private static List<Player> players;
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static ArrayList<Deck<Card>> decks = new ArrayList<>();
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in);
    private static long seed;
    public static int roundNumber;
    public static int pointsToWin;
    public static Game game;

    /**
     * Entry point of the game program. Prompts the user to enter the number of
     * players, creates a new Game instance, and conducts rounds until win
     * condition is reached. Once the game ends, it retrieves the winners using
     * {@link #getWinner()} and prints out the winning players.
     *
     * @param args
     */
    public static void main(String[] args) {
        String msg = "Enter number of players (between 2 and 4): ";
        int playerNumber = Utility.askForNum(sc, 2, 4, msg);
        msg = String.format("Enter number of computer players (between 0 and %d): ", playerNumber - 1);
        int cpuNumber = Utility.askForNum(sc, 0, playerNumber - 1, msg);

        Game.game = new Game(playerNumber, cpuNumber);
        setGameForCPU();
        new Display(game);

        boolean lastRound = false;
        roundNumber = 1;
        while (!lastRound) {
            for (int i = 0; i < playerNumber; i++) {
                doPlayerTurn(players.get(i));
                if (!lastRound) {
                    lastRound = hitWinCondition(players.get(i));
                }
            }
            roundNumber++;
        }
        Display.printWinner(getWinner());
        sc.close();
    }

    public static void setGameForCPU() {
        for (Player player : players) {
            if (player instanceof CPUPlayer cpu) {
                cpu.setGame(game);
            }
        }
    }

    /**
     * Initializes the game board with the specified number of players and
     * cards. Sets up the bank, noble tiles, and player objects.
     *
     * @param playerNumber the number of players in the game
     */
    public Game(int playerNumber, int cpuNumber) {
        this(playerNumber, cpuNumber, (new Random()).nextLong());
    }

    public Game(int playerNumber, int cpuNumber, long seed) {
        Configuration.load();

        this.playerNumber = playerNumber;
        this.cpuNumber = cpuNumber;
        this.seed = seed;
        this.pointsToWin = Configuration.getPointsToWin();
        players = new ArrayList<>(playerNumber);

        int startingGems = Configuration.getStartingGems(playerNumber);
        for (Gem gem : Gem.values()) {
            bank.put(gem, startingGems);
        }

        for (int i = 0; i < 3; i++) {
            Deck<Card> deck = new Deck<>(Configuration.getDeck(i + 1));
            decks.add(deck);
            decks.get(i).shuffleDeck(seed);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                market[i][j] = decks.get(i).draw();
            }
        }

        nobles = new ArrayList<NobleTile>(playerNumber + 1);
        Deck<NobleTile> nobleTileDeck = Configuration.getNobleTiles();
        nobleTileDeck.shuffleDeck(seed);
        for (int i = 0; i < playerNumber + 1; i++) {
            nobles.add(nobleTileDeck.draw());
        }

        setPlayerArray();
    }

    public long getSeed() {
        return seed;
    }

    public Card[][] getMarket() {
        return market;
    }

    public List<NobleTile> getNobles() {
        return nobles;
    }

    public Map<Gem, Integer> getBank() {
        return bank;
    }

    /**
     * Initializes the player array by prompting each player to enter their name
     * and creating a corresponding {@link Player} for each entry.
     *
     * @param playerNumber the total number of players participating in the game
     */
    public static void setPlayerArray() {
        System.out.println("\nThe first player is the youngest.");
        int i;
        for (i = 1; i <= playerNumber - cpuNumber; i++) {
            System.out.print("Enter player " + i + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name, i);
            players.add(player);
        }
        int cpuIdx = 1;
        for (i = 1; i <= cpuNumber; i++) {
            String name = "CPU" + cpuIdx;
            Player player = new CPUPlayer(name, i + 1);
            players.add(player);
            cpuIdx++;
        }
    }

    /**
     * Executes a player's turn by presenting three available options, Draw
     * tokens, Reserve a card and Buy a card The player is repeatedly prompted
     * to enter a choice until a valid action is performed.
     *
     * @param player the {@link Player} whose turn is being executed
     */
    public static void doPlayerTurn(Player player) {
        boolean turnDone = false;
        boolean toPrintReserved = false;

        Map<Integer, Player> playersToPrint = new TreeMap<>();

        int printPlayerNo = players.indexOf(player);

        if (player instanceof CPUPlayer cpu) {
            System.out.println("Computer is making its move...");
            cpu.calculateOptimalMove();
            Move move = cpu.getMove();
            if (move != null) {
                move.doMove();
            }

        } else if (!Display.hideSkipOption(player)) {
            System.out.println("There are no available options for you. Your turn is skipped.");
        } else {

            while (!turnDone) {
                Display.clearScreen();
                Display.printBoard(player, roundNumber, bank, nobles, market);
                Display.printOtherPlayers(playersToPrint);
                Display.printReserved(toPrintReserved, player);
                Display.turnOptionDisplay(player);

                switch (Utility.askForNum(sc, 1, 6, "Please enter your choice: ")) {
                    case 1:
                        if (Display.showDrawToken()) {
                            turnDone = drawToken(player);
                        } else {
                            System.out.println("This is not a valid option!");
                        }
                        break;
                    case 2:
                        if (Display.showReserveCard(player)) {
                            turnDone = reserveCard(player);
                        } else {
                            System.out.println("This is not a valid option!");
                        }
                        break;
                    case 3:
                        if (Display.showBuyCard(player)) {
                            turnDone = buyCard(player);
                        } else {
                            System.out.println("This is not a valid option!");
                        }
                        break;
                    case 4:
                        toPrintReserved = !toPrintReserved;
                        break;
                    case 5:
                        playersToPrint = Display.choosePlayersToPrint(sc, players, player);
                        break;
                    case 6:
                        int idx = players.indexOf(player);
                        player = adminPerms(player);
                        players.set(idx, player);
                        break;
                }
            }
        }

        nobleSelection(player);

        try {
            System.out.println("\nThe turn has ended, continuing to next player...\n");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            return;
        }

    }

    /**
     * Prompts the {@link Player} to choose a {@link NobleTile} if more than one
     * visits the player. Adds the chosen {@link NobleTile} to the
     * {@link Player}
     *
     * @param player the {@link Player} whose turn is being executed
     */
    public static void nobleSelection(Player player) {
        List<NobleTile> visitingNobles = visitingNobles(player);

        if (visitingNobles.size() == 0) {
            return;
        }

        NobleTile noble = null;

        if (visitingNobles.size() > 1) {
            System.out.println("\nOne or more nobles have visited you!\n");

            Display.printNobles(visitingNobles);

            int choice;
            if (player instanceof CPUPlayer cpu) {
                Move move = cpu.getMove();
                choice = move.getNobleIdx();
            } else {
                choice = Utility.askForNum(sc, 1, visitingNobles.size(), "\nPlease select a noble: ");
            }
            noble = visitingNobles.get(choice - 1); // choice 1 corresponds to idx 0
        } else {
            noble = visitingNobles.get(0);
        }
        System.out.println("\nA noble has visited you!");
        player.addNobleTile(noble);
        nobles.remove(noble);

    }

    /**
     * Checks whether the player has met the win condition. The win condition is
     * reached when the player's points total is 15.
     *
     * @param player the {@link Player} being checked
     * @return true if the {@link Player} has reached the win condition, false
     * otherwise
     */
    public static boolean hitWinCondition(Player player) {

        return player.getPoints() >= pointsToWin;
    }

    /**
     * Checks if the player’s points are equal to or more than 15.
     *
     * @param p the {@link Player} being checked.
     * @return a list of {@link Player} objects that have hit the win condition.
     */
    public static List<Player> getWinner() {
        Collections.sort(players);
        List<Player> winningPlayers = new ArrayList<>();
        winningPlayers.add(players.get(0));

        int idx = 0;
        while (idx + 1 <= players.size() - 1) {
            Player p1 = players.get(idx);
            Player p2 = players.get(idx + 1);

            if (p1.compareTo(p2) == 0) {
                winningPlayers.add(p2);
                idx++;
            } else {
                break;
            }

        }
        return winningPlayers;

    }

    /**
     * Determines which {@link NobleTile}s are visiting the specified player.
     * One or more {@link NobileTile}s may visit a player.
     *
     * @param p the {@link Player} being checked
     * @return a list of {@link NobleTile} that are visiting the player
     */
    public static List<NobleTile> visitingNobles(Player player) {
        List<NobleTile> result = new ArrayList<>();
        HashMap<Gem, Integer> playerBonuses = player.getBonuses();

        for (NobleTile n : nobles) {
            boolean qualify = true;
            HashMap<Gem, Integer> nobleTokens = n.getTokens();
            for (Map.Entry<Gem, Integer> entry : nobleTokens.entrySet()) {
                if (playerBonuses.get(entry.getKey()) < entry.getValue()) {
                    qualify = false;
                    break;
                }
            }
            if (qualify) {
                result.add(n);
            }

        }
        return result;
    }

    /**
     * Performs the reserve card action. The {@link Player} selects a card to
     * reserve, and receives 1 gold if the bank has gold available.
     *
     * @param player the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean reserveCard(Player player) {

        if (player instanceof CPUPlayer cpu) {
            reserveFromMarket(player);
        } else {
            if (player.getReserveHandSize() == 3) {
                try {
                    System.out.println("\n‼️ Your hand size is full. ‼️");
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    return false;
                }
                return false;
            }

            boolean validAction = false;

            while (!validAction) {
                int choice;

                Display.reserveCardDisplay();
                choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");
                System.out.println();
                switch (choice) {
                    case 0:
                        return false;
                    case 1:
                        validAction = reserveFromMarket(player);
                        break;
                    case 2:
                        validAction = reserveFromDeck(player);
                        break;

                }
            }

        }

        // add gold if gold in bank
        if (bank.get(Gem.Gold) > 0) {
            player.addToken(Gem.Gold, 1);
            bank.put(Gem.Gold, bank.get(Gem.Gold) - 1);
        }

        returnExcessTokens(player);

        return true;

    }

    /**
     * Performs the reserve from market option.
     *
     * @param player the {@link Player} performing the action
     * @return returns true if the action is succesful, false otherwise
     */
    public static boolean reserveFromMarket(Player player) {
        int[] pos = new int[2];
        if (player instanceof CPUPlayer cpu) {
            ReserveCard move = (ReserveCard) cpu.getMove();
            pos[0] = move.getRow();
            pos[1] = move.getColumn();
        } else {
            pos = Utility.getPositionOnBoard(sc);
        }
        // convert choice to corresponding card

        if (pos == null) {
            return false;
        }

        Card card = market[pos[0]][pos[1]];
        if (card == null) {
            System.out.println("‼️ No card at this position. ‼️");
            return false;
        }
        // remove from market
        market[pos[0]][pos[1]] = decks.get(pos[0]).draw();
        // add to hand
        player.reserveCard(card);
        return true;

    }

    /**
     * Performs the reserve from deck option.
     *
     * @param player the {@link Player} performing the action
     * @return returns true if the action is successful, false otherwise
     */
    public static boolean reserveFromDeck(Player player) {
        int choice2 = Utility.askForNum(sc, 0, 3, "Enter deck no. (1, 2, 3), 0 to cancel: ");
        if (choice2 == 0) {
            return false;
        }
        // }
        Card card = decks.get(choice2 - 1).draw();
        if (card == null) {
            System.out.println("‼️ Deck is empty ‼️");
            return false;
        }
        // add to hand
        player.reserveCard(card);

        System.out.println("\nYou reserved this card:");
        System.out.println("🃏" + card);

        return true;
    }

    /**
     * Performs the buy card action. The {@link Player} selects a card to buy,
     * and the player's tokens and the bank are updated accordingly.
     *
     * @param player the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean buyCard(Player player) {

        while (true) {
            int choice;
            if (player instanceof CPUPlayer cpu) {
                BuyCard move = (BuyCard) cpu.getMove();
                choice = move.getBuyLocation();
            } else {
                Display.buyCardDisplay();
                choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");
                System.out.println();
            }

            if (choice == 0) {
                return false;
            }

            HashMap<Gem, Integer> toPay = Utility.generateEmptyHashmap();
            if (choice == 1) {
                // get card position
                int[] pos = new int[2];
                Card card = null;
                if (player instanceof CPUPlayer cpu) {
                    BuyCard move = (BuyCard) cpu.getMove();
                    // todo
                    pos[0] = move.getRow();
                    pos[1] = move.getColumn();
                    card = market[pos[0]][pos[1]];
                    toPay = new HashMap<>(move.getToPay());
                    cpu.buyCard(card, toPay);

                } else {
                    pos = Utility.getPositionOnBoard(sc);
                    if (pos == null) {
                        return false;
                    }
                    card = market[pos[0]][pos[1]];
                    if (card == null) {
                        System.out.println("‼️ No card at that position ‼️");
                        continue;
                    }

                    HashMap<Gem, Integer> pBefore = player.getTokens();
                    boolean success = player.buyCard(card, sc);
                    if (!success) {
                        System.out.println("‼️ Unable to buy that card. ‼️");
                        continue;
                    }
                    HashMap<Gem, Integer> pAfter = player.getTokens();
                    for (Gem g : Gem.values()) {
                        toPay.put(g, pBefore.get(g) - pAfter.get(g));
                    }

                }

                // remove from market
                market[pos[0]][pos[1]] = decks.get(pos[0]).draw();
            }

            if (choice == 2) {
                // buy from reserve
                List<Card> hand = player.getReserveHand();
                if (hand.isEmpty()) {
                    System.out.println("‼️ You have no reserved cards. ‼️");
                    continue;
                }

                int idx;
                if (player instanceof CPUPlayer cpu) {
                    BuyCard move = (BuyCard) cpu.getMove();
                    idx = move.getReserveIdx();
                    toPay = new HashMap<>(move.getToPay());
                    Card card = hand.get(idx);
                    cpu.buyCard(card, toPay);

                } else {
                    System.out.println("Your reserved cards:");
                    for (int i = 0; i < hand.size(); i++) {
                        System.out.println((i + 1) + ". " + hand.get(i));
                    }
                    System.out.println();

                    idx = Utility.askForNum(sc, 1, hand.size(), "Enter card number: ") - 1;
                    Card card = hand.get(idx);

                    HashMap<Gem, Integer> pBefore = player.getTokens();
                    boolean success = player.buyCard(card, sc);
                    if (!success) {
                        System.out.println("‼️ Unable to buy that card. ‼️");
                        continue;
                    }
                    HashMap<Gem, Integer> pAfter = player.getTokens();
                    for (Gem g : Gem.values()) {
                        toPay.put(g, pBefore.get(g) - pAfter.get(g));
                    }

                }

                player.removeReserveCard(idx);
            }
            // update bank
            for (Gem g : Gem.values()) {
                bank.put(g, bank.get(g) + toPay.get(g));
            }
            return true;
        }
    }

    /**
     * Performs the draw token action. Handles the overall logical flow of the
     * draw token action, including CPU actions and player actions. Updates
     * player and bank based on the hashmaps returned.
     *
     * @param player the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean drawToken(Player player) {

        HashMap<Gem, Integer> chosen;

        if (player instanceof CPUPlayer cpu) {
            DrawGems move = (DrawGems) cpu.getMove();
            chosen = move.getToDraw();
        } else {
            chosen = drawTokenFromPlayer();
            if (chosen == null) {
                // player has chosen not to continue
                return false;
            }
        }

        for (Gem g : Gem.values()) {
            bank.replace(g, bank.get(g) - chosen.get(g));
            player.addToken(g, chosen.get(g));
        }

        returnExcessTokens(player);

        return true;
    }

    /**
     * Handles the choice to draw 3 or draw 2 tokens. Should only be accessed if
     * player is not a cpu.
     *
     * @return a hashmap of the tokens to draw. returns null if {@link Player}
     * cancels.
     */
    public static HashMap<Gem, Integer> drawTokenFromPlayer() {
        while (true) {
            Display.drawTokenDisplay();

            int choice = Utility.askForNum(sc, 0, 2, "Please enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    return pickThreeDifferentGems();
                case 2:
                    return pickTwoSameGem();
                default:
                    return null;
            }
        }
    }

    /**
     * Returns excess tokens from the player, if tokens in player's hand exceed
     * 10.
     *
     * @param player the {@link Player} performing the action
     */
    public static void returnExcessTokens(Player player) {

        if (player.getTokenAmount() <= 10) {
            return;
        }
        // player's amount of token exceeds 10.
        HashMap<Gem, Integer> returnAmt = new HashMap<>();
        if (player instanceof CPUPlayer cpu) {
            if (cpu.getMove() instanceof DrawGems dg) {
                returnAmt = dg.getToReturn();
            } else if (cpu.getMove() instanceof ReserveCard rc) {
                returnAmt = rc.getToReturn();
            }
        } else {
            boolean confirmReturn = false;
            while (!confirmReturn) {
                returnAmt = player.getReturnAmt();
                System.out.println("Returning : " + returnAmt);
                confirmReturn = Utility.willProceed(sc, "Confirm that these are the tokens you want to return? (Y/N): ");
            }
        }

        for (Gem g : returnAmt.keySet()) {
            bank.replace(g, bank.get(g) + returnAmt.get(g));
            player.removeToken(g, returnAmt.get(g));
        }
    }

    /**
     * Prompts the player to select three tokens of different types, ensuring
     * that the selected tokens are available in the bank.
     *
     * @return a hashmap of the tokens to draw. returns null if {@link Player}
     * cancels.
     */
    private static HashMap<Gem, Integer> pickThreeDifferentGems() {
        HashMap<Gem, Integer> chosen = Utility.generateEmptyHashmap();
        while (Utility.getTotalGems(chosen) < 3) {
            Gem g = Utility.askForGem(sc, "Enter gem (diamond/ruby/sapphire/emerald/onyx) or cancel: ");

            if (g == null) {
                return null;
            }

            if (bank.get(g) <= 0) {
                System.out.println("‼️ Bank does not have this gem. ‼️");
                continue;
            }

            if (chosen.get(g) == 1) {
                System.out.println("‼️ Already chosen. ‼️");
                continue;
            }

            chosen.put(g, 1);
        }

        return chosen;
    }

    /**
     * Prompts the {@link Player} to select a token or cancel the action.
     * Ensures that the selected token type is available in the bank (at least
     * four tokens must be present).
     *
     * @return a hashmap of the tokens to draw
     */
    private static HashMap<Gem, Integer> pickTwoSameGem() {
        HashMap<Gem, Integer> chosen = Utility.generateEmptyHashmap();
        while (Utility.getTotalGems(chosen) < 1) {
            Gem g = Utility.askForGem(sc, "Enter gem (diamond/ruby/sapphire/emerald/onyx) or cancel: ");
            if (g == null) {
                return null;
            }

            if (bank.get(g) < 4) {
                System.out.println("‼️ Need at least 4 in bank to take 2. ‼️");
                continue;
            }

            chosen.put(g, 2);
        }

        return chosen;
    }

    /**
     * Admin Permissions Allows user to set token, set bonuses, set points
     *
     * @param p the current player
     */
    public static Player adminPerms(Player p) {
        boolean finishAction = false;
        while (!finishAction) {
            if (p instanceof Admin a) {
                System.out.println("1. Set Token");
                System.out.println("2. Set Bonuses");
                System.out.println("3. Set Points");
                System.out.println("0. Quit this page");

                int choice = Utility.askForNum(sc, 0, 3, "Enter your choice: ");

                Gem g = null;
                int amt = 0;
                switch (choice) {
                    case 0:
                        finishAction = true;
                        break;
                    case 1:
                        g = Utility.askForGem(sc, "Enter Gem type. Must be spelt: ");
                        amt = Utility.askForNum(sc, 0, Integer.MAX_VALUE, "Enter amount: ");
                        a.setToken(g, amt);
                        break;
                    case 2:
                        g = Utility.askForGem(sc, "Enter Gem type. Must be spelt: ");
                        amt = Utility.askForNum(sc, 0, Integer.MAX_VALUE, "Enter amount: ");
                        a.setBonuses(g, amt);
                        break;
                    case 3:
                        amt = Utility.askForNum(sc, 0, Integer.MAX_VALUE, "Enter amount: ");
                        a.setPoints(amt);
                        break;
                }
                System.out.println(a);

            } else {
                System.out.println("1. Make player into admin");
                System.out.println("0. Quit this page");
                int choice = Utility.askForNum(sc, 0, 1, "Enter your choice: ");
                if (choice == 0) {
                    break;
                }

                p = new Admin(p);
                System.out.println("Player is now admin");
            }
        }
        return p;
    }

}
