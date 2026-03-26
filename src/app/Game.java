package app;

import config.*;
import display.*;
import item.*;
import java.lang.*;
import java.util.*;
import util.*;

public class Game {

    private static int playerNumber;
    private static int cpuNumber;
    private static List<Player> players;
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static ArrayList<Deck<Card>> decks = new ArrayList<>();
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in); // can like that??
    private static long seed;
    private static int roundNumber;

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
        msg = String.format("Enter number of compter players (between 0 and %d): ", playerNumber - 1);
        int cpuNumber = Utility.askForNum(sc, 0, playerNumber - 1, msg);

        Game game = new Game(playerNumber);

        boolean lastRound = false;
        roundNumber = 1;
        while (!lastRound) {
            for (int i = 0; i < playerNumber; i++) {
                Display.clearScreen();
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
        //     String name = "CPU" + cpuIdx;
        //     CPUPlayer player = new CPUPlayer(game, name, i + 1);
        //     players.add(player);
        //     cpuIdx++;
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
        boolean printReserved = false;
        int printPlayerNo = players.indexOf(player);

        // if (player instanceof CPUPlayer cpu) {
        //     cpu.getMove().doMove();
        //     return;
        // }
        while (!turnDone) {
            Display.displayRoundAndPlayer(player.getName(), roundNumber);
            Display.printBoard(bank, nobles, market);

            if (printReserved) {
                player.printReserved();
            }
            System.out.println(players.get(printPlayerNo));

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
                    Display.clearScreen();
                    printReserved = !printReserved;
                    break;
                case 5:
                    Display.clearScreen();
                    printPlayerNo = printPlayer();
                    break;
                case 6:
                    int idx = players.indexOf(player);
                    player = adminPerms(player);
                    players.set(idx, player);
                    break;
            }
        }
        nobleSelection(player);
    }

    public static void nobleSelection(Player p) {
        List<NobleTile> visitingNobles = visitingNobles(p);
        if (visitingNobles.size() > 1) {
            // display choices
            for (int i = 0; i < visitingNobles.size(); i++) {
                NobleTile t = visitingNobles.get(i);
                System.out.println((i + 1) + ": " + t);
            }

            int choice;
            // if (p instanceof CPUPlayer cpu) {
            //     NobleSelection move = (NobleSelection) cpu.getMove();
            //     choice = move.getNobleIdx();
            // } else {
            choice = Utility.askForNum(sc, 1, visitingNobles.size(), "Please select a noble: ");
            // }

            NobleTile noble = visitingNobles.get(choice - 1); // choice 1 corresponds to idx 0
            p.addNobleTile(noble);
            nobles.remove(noble);
        } else if (visitingNobles.size() == 1) {
            p.addNobleTile(visitingNobles.get(0));
            nobles.remove(visitingNobles.get(0));
        }
    }

    /**
     * Checks whether the player has met the win condition. The win condition is
     * reached when the player's points total is 15.
     *
     * @param p the {@link Player} being checked
     * @return true if the player has reached the win condition, false otherwise
     */
    public static boolean hitWinCondition(Player p) {

        // todo
        return p.getPoints() >= 15;
    }

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
     * Determines which {@link NobleTile} are visiting the specified player.
     *
     * @param p the {@link Player} being checked
     * @return a list of {@link NobleTile} that are visiting the player
     */
    public static List<NobleTile> visitingNobles(Player p) {
        List<NobleTile> result = new ArrayList<>();
        HashMap<Gem, Integer> playerProduction = p.getProduction();

        for (NobleTile n : nobles) {
            boolean qualify = true;
            HashMap< Gem, Integer> nobleTokens = n.getTokens();
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
            System.out.println("Your hand size is full.");
            return false;
        }

        boolean validAction = false;

        while (!validAction) {
            Display.reserveCardDisplay();
            int choice;
            // if (p instanceof CPUPlayer cpu) {
            //     ReserveCard move = (ReserveCard) cpu.getMove();
            //     choice = move.getReserveLocation();
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
     * @return returns if the action is succesful
     */
    public static boolean reserveFromMarket(Player player) {
        int[] choice2;
        // if (p instanceof CPUPlayer cpu) {
        //     ReserveCard move = (ReserveCard) cpu.getMove();
        //     // todo
        //     choice2 = move.getType();
        // } else {
        choice2 = Utility.getPositionOnBoard(sc);
        // }
        // convert choice to corresponding card

        if (choice2 == null) {
            return false;
        }

        Card card = market[choice2[0]][choice2[1]];
        if (card == null) {
            System.out.println("No card at this position.");
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
     * @return returns if the action is succesful
     */
    public static boolean reserveFromDeck(Player player) {
        int choice2;
        // if (p instanceof CPUPlayer cpu) {
        //     ReserveCard move = (ReserveCard) cpu.getMove();
        //     // todo
        //     choice2 = move.getType();
        // } else {
        choice2 = Utility.askForNum(sc, 0, 3, "Enter deck no. (1, 2, 3), 0 to cancel: ");
        if (choice2 == 0) {
            return false;
        }
        // }
        Card card = decks.get(choice2 - 1).draw();
        if (card == null) {
            System.out.println("Deck is empty");
            return false;
        }
        // add to hand
        player.reserveCard(card);
        return true;
    }

    public static void returnExcessTokens(Player player) {

        if (player.getTokenAmount() <= 10) {
            return;
        }
        // player's amount of token exceeds 10.
        HashMap<Gem, Integer> returnAmt;
        // if (currentPlayer instanceof CPUPlayer cpu) {
        //     DrawToken move = cpu.getMove();
        //     // todo
        //     returnAmt = move.getReturnAmt();
        // } else {
        returnAmt = getReturnAmtFromPlayer(player);
        // }

        for (Gem g : returnAmt.keySet()) {
            bank.replace(g, bank.get(g) + returnAmt.get(g));
            player.removeToken(g, returnAmt.get(g));
        }
    }

    /**
     * Performs the buy card action. The {@link Player} selects a card to buy,
     * and the player's tokens and the bank are updated accordingly.
     *
     * @param p the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean buyCard(Player player) {

        while (true) {
            int choice;
            // if (player instanceof CPUPlayer cpu) {
            //     BuyCard move = (BuyCard) cpu.getMove();
            //     choice = move.getBuyLocation();
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
                //     BuyCard move = (BuyCard) cpu.getMove();
                //     // todo
                //     pos = move.getType();
                // } else {
                pos = Utility.getPositionOnBoard(sc);
                if (pos == null) {
                    return false;
                }

                // }
                Card card = market[pos[0]][pos[1]];

                if (card == null) {
                    System.out.println("No card at that position");
                    continue;
                }

                Map<Gem, Integer> pBefore = player.getTokens();
                boolean success = player.buyCard(card, sc);
                if (!success) {
                    System.out.println("Unable to buy that card.");
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
                    System.out.println("You have no reserved cards.");
                    continue;
                }

                int idx;
                // if (p instanceof CPUPlayer cpu) {
                //     BuyCard move = (BuyCard) cpu.getMove();
                //     // todo
                //     idx = move.getType();
                // } else {
                System.out.println("Your reserved cards:");
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ". " + hand.get(i));
                }

                idx = Utility.askForNum(sc, 1, hand.size(), "Enter card number: ") - 1;
                // }

                Card card = hand.get(idx);

                Map<Gem, Integer> pBefore = player.getTokens();
                boolean success = player.buyCard(card, sc);
                if (!success) {
                    System.out.println("Unable to buy that card.");
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
     * @param p the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean drawToken(Player currentPlayer) {

        HashMap<Gem, Integer> chosen;

        // if (currentPlayer instanceof CPUPlayer cpu) {
        //     DrawToken move = cpu.getMove();
        //     // todo
        //     chosen = move.getTokens();
        // } else {
        chosen = drawTokenFromPlayer(currentPlayer);
        if (chosen == null) {
            // player has chosen not to continue
            return false;
        }
        // }

        for (Gem g : chosen.keySet()) {
            bank.replace(g, bank.get(g) - chosen.get(g));
            currentPlayer.addToken(g, chosen.get(g));
        }

        returnExcessTokens(currentPlayer);

        return true;
    }

    /**
     * Handles the choice to draw 3 or draw 2 tokens. Should only be accessed if
     * player is not a cpu.
     *
     * @param p The Player
     * @return a hashmap of the tokens to draw. null if player cancels.
     */
    public static HashMap<Gem, Integer> drawTokenFromPlayer(Player p) {
        while (true) {
            Display.drawTokenDisplay();

            int choice = Utility.askForNum(sc, 0, 2, "Please enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1:
                    return pickThreeDifferentGems(p);
                case 2:
                    return pickTwoSameGem(p);
                default:
                    return null;
            }
        }
    }

    /**
     * Prompts the player to select three tokens of different types, ensuring
     * that the selected tokens are available in the bank.
     *
     * @return a hashmap of the tokens to draw
     */
    private static HashMap<Gem, Integer> pickThreeDifferentGems(Player p) {
        HashMap<Gem, Integer> chosen = new HashMap<>();
        while (chosen.size() < 3) {
            Gem g = Utility.askForGem(sc, "Enter gem (diamond/ruby/sapphire/emerald/onyx) or cancel: ");

            if (g == null) {
                return null;
            }

            if (bank.get(g) <= 0) {
                System.out.println("Bank does not have this gem.");
                continue;
            }

            if (chosen.containsKey(g)) {
                System.out.println("Already chosen.");
                continue;
            }

            chosen.put(g, 1);
        }

        return chosen;
    }

    /**
     * Prompts the player to select a token or cancel the action. Ensures that
     * the selected token type is available in the bank (at least four tokens
     * must be present).
     *
     * @return a hashmap of the tokens to draw
     */
    private static HashMap<Gem, Integer> pickTwoSameGem(Player p) {
        HashMap<Gem, Integer> chosen = new HashMap<>();
        while (chosen.size() < 1) {
            Gem g = Utility.askForGem(sc, "Enter gem (diamond/ruby/sapphire/emerald/onyx) or cancel: ");
            if (g == null) {
                return null;
            }

            if (bank.get(g) < 4) {
                System.out.println("Need at least 4 in bank to take 2.");
                continue;
            }

            chosen.put(g, 2);
        }

        return chosen;
    }

    /**
     * Prompts the user to select gems to return, if the number of tokens in
     * their hand exceeds 10.
     *
     * @param p the player.
     * @return a hashmap of the tokens to return to bank.
     */
    public static HashMap<Gem, Integer> getReturnAmtFromPlayer(Player p) {
        HashMap<Gem, Integer> returnAmt = new HashMap<>();
        HashMap<Gem, Integer> pTokens = p.getTokens();
        int total = p.getTokenAmount();
        while (total > 10) {
            p.displayTokens();
            System.out.println("You have more than 10 tokens. ");
            Gem g = Utility.askForGem(sc, "Return 1 token (diamond/ruby/sapphire/emerald/onyx/gold):", true);
            if (g == null) {
                System.out.println("Invalid input! Try again.");
                continue;
            }

            Integer amtPerGem = returnAmt.get(g);
            if (amtPerGem == null) {
                amtPerGem = 0;
            }

            // check that player has at least the amt they want to return
            if (pTokens.get(g) >= ++amtPerGem) {
                returnAmt.put(g, amtPerGem);
                total--;
            } else {
                System.out.println("You don't have enough tokens for that.");
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

    public static int printPlayer() {
        String display = String.format("Enter player number (1 - %d), 0 to cancel: ", players.size());

        int choice = Utility.askForNum(sc, 0, players.size(), display);
        return choice - 1;
    }
}
