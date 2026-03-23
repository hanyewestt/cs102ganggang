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
    private static ArrayList<Deck<Card>> decks = new ArrayList<>();
    private static Card[][] market = new Card[3][4];
    private static ArrayList<NobleTile> nobles;
    private static Scanner sc = new Scanner(System.in); // can like that??
    private static long seed;

    public static void main(String[] args) {

        System.out.print("Enter number of players (between 2 and 4): ");
        int playerNumber = enterNumber(2, 4);

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

    public static HashMap<Gem, Integer> getBank() {
        return bank;
    }

    public static Card[][] getMarket() {
        return market;
    }

    public static ArrayList<NobleTile> getNobles() {
        return nobles;
    }

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

    

    public static void setPlayerArray(int playerNumber) {
        System.out.println("\nThe first player is the youngest.");
        for (int i = 0; i < playerNumber; i++) {
            System.out.print("Enter player " + (i + 1) + " name: ");
            String name = sc.nextLine();
            Player player = new Player(name);
            players.add(player);
        }
    }

    public static int enterNumber(int min, int max) {
        boolean valid = false;
        int n = -1; // can anyhow initialise??
        while (!valid) {
            try {
                n = sc.nextInt();
                if (n < min || n > max) {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                    continue;
                }
                valid = true;
            } catch (InputMismatchException e) {
                System.out.print("Please enter a number between " + min + " and " + max + ": ");
            } finally {
                sc.nextLine();
            }
        }
        return n;
    }

    public static void doPlayerTurn(Player player) {

        boolean turnDone = false;
        while (!turnDone) {
            // show board every turn? might affect the choice they make
            printBoard();
            turnOptionDisplay();

            switch (enterNumber(1, 5)) {
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
                        System.out.print("Please select a noble: ");
                        int choice = enterNumber(1, visitingNobles.size());
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
                case 5:
                    int idx = players.indexOf(player);
                    player = adminPerms(player);
                    players.set(idx, player);
                    break;
                case 4:

                //skip turn????
            }
        }
    }

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

            int choice = enterNumber(0, 2);

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
                System.out.print("Enter deck no. (1, 2, 3): ");
                int choice2 = enterNumber(1, 3);
                Card card = decks.get(choice2 - 1).draw();
                if (card == null) {
                    System.out.println("Deck is empty");
                    continue;
                }

            }
            // add to player
            // add gold if gold in bank
            if (bank.get(Gem.Gold) > 0) {
                p.addToken(Gem.Gold, 1);
                bank.put(Gem.Gold, bank.get(Gem.Gold) - 1);
            }
            validAction = true;

        }

        return true;

    }

    public static boolean buyCard(Player p) {

        System.out.println("todo: buy from reserve");

        int[] choice = Utility.getPositionOnBoard(sc);
        // convert choice to corresponding card
        Card card = market[choice[0] - 1][choice[1] - 1];// p.buyCard(card, sc);

        // I assume that if the card is not in market, value at that pos is null.
        if (card == null) {
            System.out.println("error: card does not exist in market");
            return false;
        }

        Map<Gem, Integer> pBefore = p.getTokens();

        boolean success = p.buyCard(card, sc);
        if (!success) {
            System.out.println("error: unable to buy card");
            return false;
        }
        Map<Gem, Integer> pAfter = p.getTokens();

        for (Gem g : Gem.values()) {
            int diff = pBefore.get(g) - pAfter.get(g);
            bank.replace(g, diff + bank.get(g));
        }

        // remove from market
        market[choice[0] - 1][choice[1] - 1] = decks.get(choice[0] - 1).draw();

        return true;
    }

    // drawToken function 
    public static boolean drawToken(Player currentPlayer) {

        boolean validAction = false;

        while (!validAction) {
            System.out.println("Choose token option: ");
            System.out.println("1. Take 3 different tokens");
            System.out.println("2. Take 2 same tokens");
            System.out.println("0. Cancel");

            int choice = enterNumber(0, 2);

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
            System.out.println("You have more than 10 tokens. Return 1 token:");
            String input = sc.nextLine();

            try {
                Gem g = Gem.valueOf(input);

                if (currentPlayer.getTokens().get(g) > 0) {
                    currentPlayer.removeToken(g, 1);
                    bank.replace(g, bank.get(g) + 1);
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
     * Admin Permissions
     * Allows user to set token, set production, set points
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
        System.out.println("5. admin perms");
        System.out.println();
        System.out.print("Please enter your choice:");
    }

    /**
     * Displays the current board
     * Includes: Avaliable cards, Bank, Nobile Tiles
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
                System.out.printf("%d.%d %s\n", i, j, market[i - 1][j - 1].toString());
            }
        }
        System.out.printf("------------------------------------------------------------------\n");

        printNobles();

        System.out.printf("------------------------------------------------------------------\n\n");
    }

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
