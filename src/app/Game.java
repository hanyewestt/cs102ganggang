package app;

import java.util.*;
import java.lang.*;
import item.*;
import util.*;
import config.*;

public class Game {

    private static int playerNumber;
    private static List<Player> players;
    private static HashMap<Gem, Integer> bank = new HashMap<Gem, Integer>(Gem.values().length);
    private static Deck<Card>[] decks = new Deck<Card>[3];
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in); // can like that??
    private static long seed;

    /**
     * Entry point of the game program. 
     * Prompts the user to enter the number of players, creates a new Game instance,
     * and conducts rounds until win condition is reached. 
     * Once the game ends, it retrieves the winners using <code>getWinner()</code>
     * and prints out the winning players.
     *
     * @param args 
     */
    public static void main(String[] args) {

        int playerNumber = Utility.askForNum(sc, 2, 4, "Enter number of players (between 2 and 4): ");

        Game game = new Game(playerNumber);

        boolean lastRound = false;
        int roundNumber = 1;
        while (!lastRound) {
            System.out.println("\n---------- Round " + roundNumber + " ----------");
            for (int i = 0; i < playerNumber; i++) {
                System.out.println("\n=== " + players.get(i).getName() + "'s turn ===");
                System.out.println(players.get(i));
                doPlayerTurn(players.get(i));
                lastRound = hitWinCondition(players.get(i));
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
     * Initializes the game board with the specified number of players and cards.
     * Sets up the bank, noble tiles, and player objects.
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
            decks[i] = Configuration.getDeck(i);
            decks[i].shuffleDeck(seed);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                market[i][j] = decks[i].draw();
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
     * and creating a corresponding <code>Player</code> for each entry.
     *
     * @param playerNumber the total number of players participating in the game
     */
    public static void setPlayerArray(int playerNumber) {
        System.out.println("\nThe first player is the youngest.");
        for (int i = 0; i < playerNumber; i++) {
            System.out.print("Enter player " + (i + 1) + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name);
            players.add(player);
        }
    }

    /**
     * Executes a player's turn by presenting three available options, 
     * Draw tokens, Reserve a card and Buy a card
     * The player is repeatedly prompted to enter a choice until a valid
     * action is performed.
     *
     * @param player the <code>Player</code> whose turn is being executed
     */

    public static void doPlayerTurn(Player player) {

        boolean turnDone = false;
        while (!turnDone) {
            // show board every turn? might affect the choice they make
            printBoard();

            System.out.println("Turn Options: ");
            System.out.println("1. Draw tokens");
            System.out.println("2. Reserve a card");
            System.out.println("3. Buy a card");
            System.out.println();

            switch (Utility.askForNum(sc, 1, 4, "Please enter your choice: ")) {
                case 1:
                    turnDone = drawToken(player);
                    break;
                case 2:
                    turnDone = reserveCard(player);
                    break;
                case 3:
                    turnDone = buyCard(player);
                    List<NobleTile> visitingNobles = visitingNobles(player);
                    if (visitingNobles.size() > 1) {
                        int choice = Utility.askForNum(sc, 1, visitingNobles.size(), "Please select a noble: ");
                        // display choices
                        System.out.println("todo: display visiting nobles");
                        NobleTile noble = visitingNobles.get(choice - 1); // choice 1 corresponds to idx 0
                        player.addNobleTile(noble);
                        nobles.remove(noble);
                    } else if (visitingNobles.size() == 1) {
                        player.addNobleTile(visitingNobles.get(0));
                        nobles.remove(visitingNobles.get(0));
                    }
                    break;
                case 4:
                //skip turn????
            }
        }
    }

    /**
     * Checks whether the player has met the win condition.
     * The win condition is reached when the player's points total is 15.
     *
     * @param p the <code>Player</code> being checked
     * @return true if the player has reached the win condition, false otherwise
     */
    public static boolean hitWinCondition(Player p) {
        return p.getPoints() == 15;
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
     * Determines which <code>NobleTile</code> are visiting the specified player.
     * 
     * @param p the <code>Player</code> being checked
     * @return a list of <code>NobleTile</code> that are visiting the player
     */
    public static List<NobleTile> visitingNobles(Player p) {
        List<NobleTile> result = new ArrayList<>();
        HashMap<Gem, Integer> playerTokens = p.getTokens();

        for (NobleTile n : nobles) {
            boolean qualify = true;
            HashMap< Gem, Integer> nobleTokens = n.getTokens();
            for (Map.Entry<Gem, Integer> entry : nobleTokens.entrySet()) {
                if (playerTokens.get(entry.getKey()) < entry.getValue()) {
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
     * Performs the reserve card action. The player selects a card to reserve,
     * and receives 1 gold if the bank has gold available.
     * 
     * @param p the <code>Player</code> performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean reserveCard(Player p) {

        int[] choice = Utility.getPositionOnBoard(sc);
        // convert choice to corresponding card
        Card card = market[choice[0]][choice[1]];

        boolean success = p.reserveCard(card);
        if (!success) {
            System.out.println("error: unable to reserve Card. Hand Limit reached");
            return false;
        }

        // add to player
        // add gold if gold in bank
        if (bank.get(Gem.Gold) > 0) {
            p.addToken(Gem.Gold, 1);
            bank.put(Gem.Gold, bank.get(Gem.Gold) - 1);
        }
        // remove from market
        System.out.println("todo: remove from market");

        return true;
    }

    /**
     * Performs the buy card action. The player selects a card to buy,
     * and the player's tokens and the bank are updated accordingly.
     * 
     * @param p the <code>Player</code> performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean buyCard(Player p) {

        System.out.println("todo: buy from reserve");

        int[] choice = Utility.getPositionOnBoard(sc);
        // convert choice to corresponding card
        Card card = market[choice[0]][choice[1]];// p.buyCard(card, sc);

        // I assume that if the card is not in market, value at that pos is null.
        if (card == null) {
            System.out.println("error: card does not exist in market");
            return false;
        }

        boolean success = p.buyCard(card, sc);
        if (!success) {
            System.out.println("error: unable to buy card");
            return false;
        }
        // remove from market
        System.out.println("todo: remove from market");

        return true;
    }

    /**
     * Performs the draw token action. The player may choose to take 2 tokens of the same type, 3 tokens of different types, or cancel action.
     * If the player has more than 10 tokens, prompts the user to return excess
     * The player's tokens and the bank are updated accordingly.
     * 
     * @param p the <code>Player</code> performing the action
     * @return true if the action was successfully performed, false otherwise
     */
    public static boolean drawToken(Player currentPlayer) {

        boolean validAction = false;

        while (!validAction) {
            System.out.println("Token options: ");
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
                    bank.put(g, bank.get(g) - 1);
                    currentPlayer.addToken(g, 1);
                }

                validAction = true;

            } else if (choice == 2) {
                Gem g = pickTwoSameGem();

                if (g == null) {
                    continue; // user cancelled 
                }

                bank.put(g, bank.get(g) - 2);
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
            System.out.println("You have more than 10 tokens. Return 1 token:");
            String input = sc.nextLine();

            try {
                Gem g = Gem.valueOf(input);

                if (currentPlayer.getTokens().get(g) > 0) {
                    currentPlayer.removeToken(g, 1);
                    bank.put(g, bank.get(g) + 1);
                    totalTokens--;
                } else {
                    System.out.println("You don't have that token.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid gem.");
            }
        }

        return true;
    }

    /**
     * Prompts the player to select three tokens of different types,
     * ensuring that the selected tokens are available in the bank.
     *
     * @return a set of <code>Gem</code> selected by the player
    */
    private static Set<Gem> pickThreeDifferentGems() {
        Set<Gem> chosen = new HashSet<>();

        while (chosen.size() < 3) {
            try {
                System.out.println("Enter gem (Diamond, Ruby, Sapphire, Emerald, Onyx) or 'cancel': ");
                String gemInput = sc.nextLine();

                if (gemInput.equalsIgnoreCase("cancel")) {
                    return new HashSet<>(); // return empty set 
                }

                Gem g = Gem.valueOf(gemInput);

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
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid gem.");
            }
        }

        return chosen;
    }

    /**
     * Prompts the player to select a token or cancel the action.
     * Ensures that the selected token type is available in the bank
     * (at least four tokens must be present).
     *
     * @return the <code>Gem</code> selected by the player
    */
    private static Gem pickTwoSameGem() {
        while (true) {
            System.out.println("Enter gem (Diamond, Ruby, Sapphire, Emerald, Onyx) or 'cancel':");
            String gemInput = sc.nextLine();

            if (gemInput.equalsIgnoreCase("cancel")) {
                return null;  // handled in drawToken
            }

            try {
                Gem g = Gem.valueOf(gemInput);

                if (g == Gem.Gold) {
                    System.out.println("Unable to take gold this way.");
                    continue;
                }
                if (bank.get(g) < 4) {
                    System.out.println("Need at least 4 in bank to take 2.");
                    continue;
                }

                return g;  // valid gem found

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid gem.");
            }
        }
    }

    /**
     * Prints the current state of the board, including all cards and noble tiles.
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

        for (int i = 1; i <= 3; i++) {
            System.out.printf("Deck <%d>\n", i);
            for (int j = 1; j <= 4; j++) {
                System.out.printf("%d.%d %s\n", i, j, market[i - 1][j - 1].toString());
            }
        }
        System.out.printf("------------------------------------------------------------------\n");

        printNobles();

        System.out.printf("------------------------------------------------------------------\n\n");
    }

    /**
     * Prints noble tiles.
     */
    public static void printNobles() {

        System.out.printf("<NOBLE TILES>\n");
        for (int i = 0; i < nobles.size(); i++) {
            System.out.printf("%s\n", nobles.get(i));
        }
    }

    // Overloaded printPlayer method 1
    public static void printPlayer(String name) {
        // incomplete
    }

    // Overloaded printPlayer method 2
    public static void printPlayer(int no) {

    }

    public static void printAllPlayers() {

    }

    public static void printCommandList() {

    }

}
