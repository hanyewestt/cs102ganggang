package app;

import config.*;
import item.*;
import java.lang.*;
import java.util.*;
import util.*;

public class Game {

    private static int playerNumber;
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
        int playerNumber = Utility.askForNum(sc, 2, 4, "Enter number of players (between 2 and 4): ");

        Game game = new Game(playerNumber);

        boolean lastRound = false;
        roundNumber = 1;
        while (!lastRound) {
            for (int i = 0; i < playerNumber; i++) {
                clearScreen();
                System.out.println("\n---------- Round " + roundNumber + " ----------");
                System.out.println("\n=== " + players.get(i).getName() + "'s turn ===");
                doPlayerTurn(players.get(i));
                if (!lastRound) {
                    lastRound = hitWinCondition(players.get(i));
                }
            }
            roundNumber++;
            // clear terminal
        }
        List<Player> winningPlayers = getWinner();
        for (int i = 0; i < winningPlayers.size(); i++) {
            System.out.println(winningPlayers.get(i).getName());
        }
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

        setPlayerArray(playerNumber);
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
    public static void setPlayerArray(int playerNumber) {
        System.out.println("\nThe first player is the youngest.");
        for (int i = 0; i < playerNumber; i++) {
            System.out.print("Enter player " + (i + 1) + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name, i + 1);
            players.add(player);
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
        boolean first = true;
        boolean turnDone = false;
        boolean printReserved = false;
        boolean printPlayer = false;
        int printPlayerNo = -1;

        while (!turnDone) {
            if (!first) {
                System.out.println("\n---------- Round " + roundNumber + " ----------");
                System.out.println("\n=== " + player.getName() + "'s turn ===");
            }
            // show board every turn? might affect the choice they make
            printBoard();

            if (printReserved) {
                player.printReserved();
                printReserved = false;
            } else if (printPlayer) {
                System.out.println(players.get(printPlayerNo - 1).toString());
                printPlayer = false;
            }

            if (first) {
                System.out.println(player);
                first = false;
            }
            turnOptionDisplay();

            switch (Utility.askForNum(sc, 1, 6, "")) {
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
                    clearScreen();
                    printReserved = true;
                    break;
                case 5:
                    clearScreen();
                    printPlayerNo = printPlayer();
                    if (printPlayerNo != 0) {
                        printPlayer = true;
                    }
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
            int choice = Utility.askForNum(sc, 1, visitingNobles.size(), "Please select a noble: ");
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
     * @param p the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean reserveCard(Player p) {

        if (p.getReserveHandSize() == 3) {
            System.out.println("Your hand size is full.");
            return false;
        }

        boolean validAction = false;

        while (!validAction) {
            System.out.println("Choose option: ");
            System.out.println("1. Reserve from market");
            System.out.println("2. Reserve from deck");
            System.out.println("0. Cancel");

            int choice = Utility.askForNum(sc, 0, 2, "Enter your choice: ");

            if (choice == 0) {
                return false;
            }

            if (choice == 1) {
                int[] choice2 = Utility.getPositionOnBoard(sc);
                // convert choice to corresponding card
                Card card = market[choice2[0] - 1][choice2[1] - 1];

                if (card == null) {
                    System.out.println("Market is empty");
                    continue;
                }

                // remove from market
                market[choice2[0] - 1][choice2[1] - 1] = decks.get(choice2[0] - 1).draw();

            }

            if (choice == 2) {
                int choice2 = Utility.askForNum(sc, 1, 3, "Enter deck no. (1, 2, 3): ");
                Card card = decks.get(choice2 - 1).draw();
                if (card == null) {
                    System.out.println("Deck is empty");
                    continue;
                }
                // add to hand
                p.reserveCard(card);

            }

            // add gold if gold in bank
            if (bank.get(Gem.Gold) > 0) {
                p.addToken(Gem.Gold, 1);
                bank.put(Gem.Gold, bank.get(Gem.Gold) - 1);
            }
            validAction = true;

        }

        return true;

    }

    /**
     * Performs the buy card action. The {@link Player} selects a card to buy,
     * and the player's tokens and the bank are updated accordingly.
     *
     * @param p the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean buyCard(Player p) {

        while (true){
            System.out.println();
            System.out.println("1. Buy from market"); 
            System.out.println("2. Buy from reserve"); 
            System.out.println("0. Cancel"); 
            System.out.println();
        
            int choice = Utility.askForNum(sc, 0, 2, "Enter your choice: "); 

            if (choice == 0){
                return false; 
            }

            if (choice == 1){
                // buy from market 
                int[] pos = Utility.getPositionOnBoard(sc); 
                Card card = market[pos[0] - 1][pos[1] - 1]; 

                if (card == null){
                    System.out.println("No card at that position"); 
                    continue; 
                }

                Map<Gem, Integer> pBefore = new HashMap<>(p.getTokens()); 
                boolean success = p.buyCard(card, sc); 
                if (!success){
                    System.out.println("Unable to buy that card.");
                    continue;  
                }

                Map <Gem, Integer> pAfter = p.getTokens(); 

                for (Gem g : Gem.values()) {
                    int diff =  pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                // remove from market
                market[pos[0] - 1][pos[1] - 1] = decks.get(pos[0] - 1).draw();
                return true;
            }

            if (choice == 2){
                // buy from reserve 
                List<Card> hand = p.getReserveHand(); 
                if (hand.isEmpty()){
                    System.out.println("You have no reserved cards.");
                    continue; 
                }

                System.out.println("Your reserved cards:");
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ". " + hand.get(i));
                }

                int idx = Utility.askForNum(sc, 1, hand.size(), "Enter card number: ") - 1;
                Card card = hand.get(idx);

                Map<Gem, Integer> pBefore = new HashMap<>(p.getTokens());
                boolean success = p.buyCard(card, sc);
                if (!success) {
                    System.out.println("Unable to buy that card.");
                    continue;
                }

                Map<Gem, Integer> pAfter = p.getTokens();

                for (Gem g : Gem.values()) {
                    int diff = pBefore.get(g) - pAfter.get(g);
                    bank.replace(g, diff + bank.get(g));
                }

                p.removeReserveCard(idx);
                return true;
            }
        }
    }

    /**
     * Performs the draw token action. The {@link Player} may choose to take 2
     * tokens of the same type, 3 tokens of different types, or cancel action.
     * If the {@link Player} has more than 10 tokens, prompts the user to return
     * excess The {@link Player}'s tokens and the bank are updated accordingly.
     *
     * @param p the {@link Player} performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean drawToken(Player currentPlayer) {

        boolean validAction = false;

        while (!validAction) {
            System.out.println();
            System.out.println("Token options: ");
            System.out.println();
            System.out.println("1. Take 3 different tokens");
            System.out.println("2. Take 2 same tokens");
            System.out.println("0. Cancel");
            System.out.println();

            int choice = Utility.askForNum(sc, 0, 2, "Please enter your choice: ");

            if (choice == 0) {
                return false;
            }

            // add token - option 1: 3 different tokens 
            if (choice == 1) {

                Set<Gem> chosen = pickThreeDifferentGems();

                if (chosen.isEmpty()) {
                    continue; // user cancelled 
                }

                for (Gem g : chosen) {
                    bank.replace(g, bank.get(g) - 1);
                    currentPlayer.addToken(g, 1);
                }

                validAction = true;

            } else if (choice == 2) {
                Gem g = pickTwoSameGem();

                if (g == null) {
                    continue; // user cancelled 
                }

                bank.replace(g, bank.get(g) - 2);
                currentPlayer.addToken(g, 2);

                validAction = true;
            }
        }

        // checksize
        // if exceed, prompt user to return tokens 
        int totalTokens = 0;
        for (Gem g : Gem.values()) {
            totalTokens += currentPlayer.getTokens().get(g);
        }

        while (totalTokens > 10) {

            currentPlayer.displayTokens();
            System.out.print("You have more than 10 tokens. Return 1 token:");
            String input = sc.nextLine().trim();

            if (input.length() != 1) {
                System.out.println("Invalid gem.");
                continue;
            }

            char c = Character.toUpperCase(input.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null && c == 'G') {
                g = Gem.Gold;
            }

            if (g == null) {
                System.out.println("Invalid gem.");
                continue;
            }

            if (currentPlayer.getTokens().get(g) > 0) {
                currentPlayer.removeToken(g, 1);
                bank.replace(g, bank.get(g) + 1);
                totalTokens--;
            } else {
                System.out.println("You don't have that token.");
            }
        }

        return true;
    }

    /**
     * Prompts the player to select three tokens of different types, ensuring
     * that the selected tokens are available in the bank.
     *
     * @return a set of {@link Gem} selected by the player
     */
    private static Set<Gem> pickThreeDifferentGems() {
        Set<Gem> chosen = new HashSet<>();

        while (chosen.size() < 3) {

            System.out.print("Enter gem (D/R/S/E/O) or 'cancel': ");
            String gemInput = sc.nextLine().trim();

            if (gemInput.isEmpty()) {
                continue;
            }

            if (gemInput.equalsIgnoreCase("cancel")) {
                return new HashSet<>(); // return empty set 
            }

            if (gemInput.length() != 1) {
                System.out.println("Invalid gem.");
                continue;
            }

            char c = Character.toUpperCase(gemInput.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null) {
                System.out.println("Invalid gem.");
                continue;
            }

            if (g == Gem.Gold) {
                System.out.println("Unable to take gold this way.");
                continue;
            }

            if (bank.get(g) <= 0) {
                System.out.println("Bank does not have this gem.");
                continue;
            }

            if (chosen.contains(g)) {
                System.out.println("Already chosen.");
                continue;
            }

            chosen.add(g);
        }

        return chosen;
    }

    /**
     * Prompts the player to select a token or cancel the action. Ensures that
     * the selected token type is available in the bank (at least four tokens
     * must be present).
     *
     * @return the {@link Gem} selected by the player
     */
    private static Gem pickTwoSameGem() {
        while (true) {
            System.out.print("Enter gem (D/R/S/E/O) or 'cancel':");
            String gemInput = sc.nextLine().trim();

            if (gemInput.isEmpty()) {
                continue;
            }

            if (gemInput.equalsIgnoreCase("cancel")) {
                return null;  // handled in drawToken
            }

            if (gemInput.length() != 1) {
                System.out.println("Invalid gem.");
                continue;
            }

            char c = Character.toUpperCase(gemInput.charAt(0));
            Gem g = Utility.fromCharToGem(c);

            if (g == null) {
                System.out.println("Invalid gem.");
                continue;
            }

            if (g == Gem.Gold) {
                System.out.println("Unable to take gold this way.");
                continue;
            }

            if (bank.get(g) < 4) {
                System.out.println("Need at least 4 in bank to take 2.");
                continue;
            }

            return g;  // valid gem found
        }

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

    public static void turnOptionDisplay() {
        System.out.println("1. Draw tokens");
        System.out.println("2. Reserve a card");
        System.out.println("3. Buy a card");
        System.out.println("4. Show reserved cards");
        System.out.println("5. Display player");
        System.out.println("6. admin perms");
        System.out.println();
        System.out.print("Please enter your choice:");
    }

    /**
     * Prints the current state of the board, including all cards and noble
     * tiles. Includes: Avaliable cards, Bank, Nobile Tiles
     */
    public static void printBoard() {
        System.out.printf("------------------------------------------------------------------\n");
        System.out.printf("Bank: ");
        System.out.printf(bank.get(Gem.Diamond) + "D , ");
        System.out.printf(bank.get(Gem.Ruby) + "R , ");
        System.out.printf(bank.get(Gem.Sapphire) + "S , ");
        System.out.printf(bank.get(Gem.Emerald) + "E , ");
        System.out.printf(bank.get(Gem.Onyx) + "O , ");
        System.out.printf(bank.get(Gem.Gold) + "G\n");

        System.out.println("Example Card");
        System.out.println("[Gem Produced | Prestige | Card Cost]");
        for (int i = 1; i <= 3; i++) {
            System.out.printf("Deck <%d>\n", i);
            for (int j = 1; j <= 4; j++) {
                if (market[i - 1][j - 1] == null) {
                    System.out.printf("%d.%d Empty\n", i, j);

                } else {
                    System.out.printf("%d.%d %s\n", i, j, market[i - 1][j - 1].toString());
                }
            }

            if (i != 3) {
                System.out.println();
            }
        }
        System.out.printf("------------------------------------------------------------------\n");

        printNobles();

        System.out.printf("------------------------------------------------------------------\n\n");
    }

    /**
     * Prints {@link NobleTile}s on the board.
     */
    public static void printNobles() {

        System.out.printf("<NOBLE TILES>\n");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%s\n", nobles.get(i));
        }
    }

    public static int printPlayer() {
        System.out.println("Enter player number:");
        System.out.println("0. Cancel");

        int choice = Utility.askForNum(sc, 0, players.size(), "");
        return choice;
    }

    public static void printWinner(List<Player> winningPlayers) {
        clearScreen();

        System.out.printf("------------------------------------------------------------------\n");
        System.out.printf(" ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖ THE WINNERS ARE  ˖.𖥔 ݁ ˖ ⊹ ࣪ ˖\n");
        for (Player p : winningPlayers) {
            System.out.printf(" > ");
            System.out.println(p.getName());
        }
        System.out.printf("------------------------------------------------------------------\n");
    }

    public static void clearScreen() {
        System.out.print("\033c");
    }
}


