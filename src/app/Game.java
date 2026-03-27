package app;

import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import config.*;
import display.*;
import item.*;
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
    private static int roundNumber;
    private static int pointsToWin;

    /**
     * Entry point of the game program. Prompts the user to enter the number of
     * players, creates a new Game instance, and conducts rounds until win
     * condition is reached. Once the game ends, it retrieves the winners using
     * {@link getWinner()} and prints out the winning players.
     *
     * @param args
     */
    public static void main(String[] args) {
        String msg = "Enter number of players (between 2 and 4): ";
        int playerNumber = Utility.askForNum(sc, 2, 4, msg);
        msg = String.format("Enter number of computer players (between 0 and %d): ", playerNumber - 1);
        int cpuNumber = Utility.askForNum(sc, 0, playerNumber - 1, msg);

        Game game = new Game(playerNumber);

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

    /**
     * Initializes the game board with the specified number of players and
     * cards. Sets up the bank, noble tiles, and player objects.
     *
     * @param playerNumber the number of players in the game
     */
    public Game(int playerNumber) {
        this(playerNumber, (new Random()).nextLong());
    }

    public Game(int playerNumber, long seed) {
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

        setPlayerArray(playerNumber, cpuNumber);
    }

    public long getSeed() {
        return seed;
    }

    /**
     * Initializes the player array by prompting each player to enter their name
     * and creating a corresponding {@link Player} for each entry.
     *
     * @param playerNumber the total number of players participating in the game
     */
    public static void setPlayerArray(int playerNumber, int cpuNumber) {
        System.out.println("\nThe first player is the youngest.");
        int i;
        for (i = 1; i <= playerNumber - cpuNumber; i++) {
            System.out.print("Enter player " + i + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name, i);
            players.add(player);
        }
        // int cpuIdx = 1;
        // for (; i <= playerNumber; i++) {
        // String name = "CPU" + cpuIdx;
        // CPUPlayer player = new CPUPlayer(game, name, i + 1);
        // players.add(player);
        // cpuIdx++;
        // }
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

        // if (player instanceof CPUPlayer cpu) {
        // cpu.getMove().doMove();
        // return;
        // }
        while (!turnDone) {
            Display.clearScreen();

            Display.printBoard(player, roundNumber, bank, nobles, market);

            Display.printOtherPlayers(playersToPrint);

            Display.printReserved(toPrintReserved, player);

            Display.turnOptionDisplay();

            switch (Utility.askForNum(sc, 1, 6, "Please enter your choice: ")) {
                case 1:
                    turnDone = drawToken(player);
                    break;
                case 2:
                    turnDone = reserveCard(player);
                    break;
                case 3:
                    turnDone = buyCard(player);
                    break;
                case 4:
                    toPrintReserved = !toPrintReserved;
                    break;
                case 5:
                    playersToPrint = Display.choosePlayersToPrint(sc, players, player);

                    // Display.clearScreen();
                    // int newPrintPlayerNo = Display.printPlayerNo(sc, players.size());
                    // if (newPrintPlayerNo != -1) {
                    // printPlayerNo = newPrintPlayerNo;
                    // }
                    break;
                case 6:
                    int idx = players.indexOf(player);
                    player = adminPerms(player);
                    players.set(idx, player);
                    break;
            }
        }
        nobleSelection(player);

        try {
            System.out.println("\nYour turn has ended, continuing to next player...");
            TimeUnit.SECONDS.sleep(3);
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
            // if (p instanceof CPUPlayer cpu) {
            // NobleSelection move = (NobleSelection) cpu.getMove();
            // choice = move.getNobleIdx();
            // } else {
            choice = Utility.askForNum(sc, 1, visitingNobles.size(), "\nPlease select a noble: ");
            // }
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
     *         otherwise
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
        HashMap<Gem, Integer> playerProduction = player.getProduction();

        for (NobleTile n : nobles) {
            boolean qualify = true;
            HashMap<Gem, Integer> nobleTokens = n.getTokens();
            for (Map.Entry<Gem, Integer> entry : nobleTokens.entrySet()) {
                if (playerProduction.get(entry.getKey()) < entry.getValue()) {
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
            Display.reserveCardDisplay();
            int choice;
            // if (p instanceof CPUPlayer cpu) {
            // ReserveCard move = (ReserveCard) cpu.getMove();
            // choice = move.getReserveLocation();
            // } else {
            choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");
            System.out.println();
            // }

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
        int[] choice2;
        // if (p instanceof CPUPlayer cpu) {
        // ReserveCard move = (ReserveCard) cpu.getMove();
        // // todo
        // choice2 = move.getType();
        // } else {
        choice2 = Utility.getPositionOnBoard(sc);
        // }
        // convert choice to corresponding card

        if (choice2 == null) {
            return false;
        }

        Card card = market[choice2[0]][choice2[1]];
        if (card == null) {
            System.out.println("‼️ No card at this position. ‼️");
            return false;
        }
        // remove from market
        market[choice2[0]][choice2[1]] = decks.get(choice2[0]).draw();
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
        int choice2;
        // if (p instanceof CPUPlayer cpu) {
        // ReserveCard move = (ReserveCard) cpu.getMove();
        // // todo
        // choice2 = move.getType();
        // } else {
        choice2 = Utility.askForNum(sc, 0, 3, "Enter deck no. (1, 2, 3), 0 to cancel: ");
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
            // if (player instanceof CPUPlayer cpu) {
            // BuyCard move = (BuyCard) cpu.getMove();
            // choice = move.getBuyLocation();
            // } else {
            Display.buyCardDisplay();
            choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");
            System.out.println();
            // }

            if (choice == 0) {
                return false;
            }

            if (choice == 1) {
                // get card position
                int[] pos;
                // if (player instanceof CPUPlayer cpu) {
                // BuyCard move = (BuyCard) cpu.getMove();
                // // todo
                // pos = move.getType();
                // } else {
                pos = Utility.getPositionOnBoard(sc);
                if (pos == null) {
                    return false;
                }

                // }
                Card card = market[pos[0]][pos[1]];

                if (card == null) {
                    System.out.println("‼️ No card at that position ‼️");
                    continue;
                }

                Map<Gem, Integer> pBefore = player.getTokens();
                boolean success = player.buyCard(card, sc);
                if (!success) {
                    System.out.println("‼️ Unable to buy that card. ‼️");
                    continue;
                }

                Map<Gem, Integer> pAfter = player.getTokens();

                // update bank
                for (Gem g : Gem.values()) {
                    int diff = pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                // remove from market
                market[pos[0]][pos[1]] = decks.get(pos[0]).draw();
                return true;
            }

            if (choice == 2) {
                // buy from reserve
                List<Card> hand = player.getReserveHand();
                if (hand.isEmpty()) {
                    System.out.println("‼️ You have no reserved cards. ‼️");
                    continue;
                }

                int idx;
                // if (p instanceof CPUPlayer cpu) {
                // BuyCard move = (BuyCard) cpu.getMove();
                // // todo
                // idx = move.getType();
                // } else {
                System.out.println("Your reserved cards:");
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ". " + hand.get(i));
                }
                System.out.println();

                idx = Utility.askForNum(sc, 1, hand.size(), "Enter card number: ") - 1;
                // }

                Card card = hand.get(idx);

                Map<Gem, Integer> pBefore = player.getTokens();
                boolean success = player.buyCard(card, sc);
                if (!success) {
                    System.out.println("‼️ Unable to buy that card. ‼️");
                    continue;
                }

                Map<Gem, Integer> pAfter = player.getTokens();

                for (Gem g : Gem.values()) {
                    int diff = pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                player.removeReserveCard(idx);
                return true;
            }
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

        // if (player instanceof CPUPlayer cpu) {
        // DrawToken move = cpu.getMove();
        // // todo
        // chosen = move.getTokens();
        // } else {
        chosen = drawTokenFromPlayer();
        if (chosen == null) {
            // player has chosen not to continue
            return false;
        }
        // }

        for (Gem g : chosen.keySet()) {
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
     *         cancels.
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
        // if (currentPlayer instanceof CPUPlayer cpu) {
        // DrawToken move = cpu.getMove();
        // // todo
        // returnAmt = move.getReturnAmt();
        // } else {
        boolean confirmReturn = false;
        while (!confirmReturn) {
            returnAmt = getReturnAmtFromPlayer(player);
            System.out.println("Returning : " + returnAmt);
            confirmReturn = Utility.willProceed(sc, "Confirm that these are the tokens you want to return? (Y/N): ");
        }
        // }

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
     *         cancels.
     */
    private static HashMap<Gem, Integer> pickThreeDifferentGems() {
        HashMap<Gem, Integer> chosen = new HashMap<>();
        while (chosen.size() < 3) {
            Gem g = Utility.askForGem(sc, "Enter gem (diamond/ruby/sapphire/emerald/onyx) or cancel: ");

            if (g == null) {
                return null;
            }

            if (bank.get(g) <= 0) {
                System.out.println("‼️ Bank does not have this gem. ‼️");
                continue;
            }

            if (chosen.containsKey(g)) {
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
        HashMap<Gem, Integer> chosen = new HashMap<>();
        while (chosen.size() < 1) {
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
     * Prompts the {@link Player} to select gems to return, if the number of
     * tokens in their hand exceeds 10.
     *
     * @param player the {@link Player} performing the action
     * @return a hashmap of the tokens to return to bank.
     */
    public static HashMap<Gem, Integer> getReturnAmtFromPlayer(Player player) {
        HashMap<Gem, Integer> returnAmt = new HashMap<>();
        HashMap<Gem, Integer> pTokens = player.getTokens();
        int total = player.getTokenAmount();
        System.out.println("\nGems: " + player.displayTokens());
        while (total > 10) {
            System.out.println("‼️ You have more than 10 tokens. ‼️");
            System.out.println("You have to return " + (total - 10) + " tokens. ");
            Gem g = Utility.askForGem(sc,
                    "Return 1 token (diamond/ruby/sapphire/emerald/onyx/gold), or 'cancel' to reset: ", true);
            if (g == null) {
                System.out.println("Reseting return amounts.\n");
                total = player.getTokenAmount();
                returnAmt.clear();
                continue;
            }

            Integer amtPerGem = returnAmt.get(g);
            if (amtPerGem == null) {
                amtPerGem = 0;
            }

            // check that player has at least the amt they want to return
            if (pTokens.get(g) >= ++amtPerGem) {
                returnAmt.put(g, amtPerGem);
                System.out.println("");
                total--;
            } else {
                System.out.println("‼️ You don't have enough tokens for that. ‼️\n");
            }
        }

        return returnAmt;
    }

    /**
     * Admin Permissions Allows user to set token, set production, set points
     *
     * @param p the current player
     */
    public static Player adminPerms(Player p) {
        boolean finishAction = false;
        while (!finishAction) {
            if (p instanceof Admin a) {
                System.out.println("1. Set Token");
                System.out.println("2. Set Production");
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
                        a.setProduction(g, amt);
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
