package config;

import java.io.*;
import java.util.*;

import item.*;
import util.Utility;

/**
 * This class loads information like {@link Card} and {@link NobleTile} data,
 * number of starting {@link Gem}s in bank, and the number of points needed to
 * win a game from the data directory.
 */
public class Configuration {

    private static int nobleTilePoints;
    private static int pointsToWin;
    private static int[] startingGems = {0, 0, 0};

    private static Deck<Card> deck1 = new Deck<Card>();
    private static Deck<Card> deck2 = new Deck<Card>();
    private static Deck<Card> deck3 = new Deck<Card>();

    private static Deck<NobleTile> nobleTiles = new Deck<NobleTile>();

    private static boolean hasDataBeenLoaded = false;

    /**
     * Getter to retrieve how many points a {@link NobleTile}is worth.
     *
     * @return The amount of points a {@link NobleTile} is worth.
     */
    public static int getNobleTilePoints() {
        return nobleTilePoints;
    }

    /**
     * Getter to retrieve how many points are needed to win.
     *
     * @return The amount of points needed to win.
     */
    public static int getPointsToWin() {
        return pointsToWin;
    }

    /**
     * Getter to retrieve the starting number of {@link Gem}s in the bank based
     * on a player no.
     *
     * @param playerNo The amount of players in the game. Guaranteed to be
     * between 2 to 4 (inclusive).
     * @return The amount of starting {@link Gem}s in the bank.
     */
    public static int getStartingGems(int playerNo) {
        return startingGems[playerNo - 2];
    }

    /**
     * Getter to retrieve a deep copy of a deck of {@link Card} to be used.
     *
     * @param deckNo The deck no to be retrieved.
     * @return The deep copy of said deck.
     */
    public static Deck<Card> getDeck(int deckNo) {
        Deck<Card> result = null;

        switch (deckNo) {
            case 1:
                result = new Deck<Card>(deck1);
                break;
            case 2:
                result = new Deck<Card>(deck2);
                break;
            case 3:
                result = new Deck<Card>(deck3);
                break;
        }

        return result;
    }

    /**
     * Getter to retrieve a deep copy of the noble tile deck.
     *
     * @return The deep copy of the noble tile deck.
     */
    public static Deck<NobleTile> getNobleTiles() {
        return new Deck<NobleTile>(nobleTiles);
    }

    /**
     * Loads all the relevant data from files to the Configuration class. This
     * method MUST be called only once before using any of the above getters.
     */
    public static void load() {
        if (hasDataBeenLoaded) {
            return;
        }

        Scanner numLoader = null;
        Scanner deck1Loader = null;
        Scanner deck2Loader = null;
        Scanner deck3Loader = null;
        Scanner nobleTilesLoader = null;

        ArrayList<Scanner> scanners = new ArrayList<>();
        scanners.add(numLoader);
        scanners.add(deck1Loader);
        scanners.add(deck2Loader);
        scanners.add(deck3Loader);
        scanners.add(nobleTilesLoader);

        Properties properties = new Properties();

        String filePath = System.getProperty("user.dir") + "/src/config/config.properties";

        try {
            FileInputStream file = new FileInputStream(filePath);
            properties.load(file);

            deck1Loader = setupScanner(properties.getProperty("deck1"));
            deck2Loader = setupScanner(properties.getProperty("deck2"));
            deck3Loader = setupScanner(properties.getProperty("deck3"));
            nobleTilesLoader = setupScanner(properties.getProperty("nobletiles"));

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            closeScanners(scanners);
            return;

        } catch (IOException e) {
            System.out.println("Could not load config.properties");
            closeScanners(scanners);
            return;
        }  

        // try {
        //     numLoader = setupScanner("src/data/numbers.txt");
        //     deck1Loader = setupScanner("src/data/deck1.csv");
        //     deck2Loader = setupScanner("src/data/deck2.csv");
        //     deck3Loader = setupScanner("src/data/deck3.csv");
        //     nobleTilesLoader = setupScanner("src/data/nobletiles.csv");
        // } catch (FileNotFoundException e) {
        //     System.out.println(e.getMessage());
        //     closeScanners(scanners);
        //     return;
        // }

        try {
            nobleTilePoints = Integer.parseInt(properties.getProperty("nobleTilePoints"));
            pointsToWin = Integer.parseInt(properties.getProperty("pointsToWin"));

            startingGems[0] = Integer.parseInt(properties.getProperty("startingGems2Players"));
            startingGems[1] = Integer.parseInt(properties.getProperty("startingGems3Players"));
            startingGems[2] = Integer.parseInt(properties.getProperty("startingGems4Players"));

        } catch (NumberFormatException e) {
            System.out.println("File has invalid format");
            closeScanners(scanners);
            return;
        }

        // try {
        //     nobleTilePoints = getNextNo(numLoader);
        //     pointsToWin = getNextNo(numLoader);

        //     for (int i = 0; i < 3; i++) {
        //         startingGems[i] = getNextNo(numLoader);
        //     }
        // } catch (NumberFormatException e) {
        //     System.out.println("File has invalid format");
        //     closeScanners(scanners);
        //     return;
        // }

        try {
            fillCardDeck(deck1, deck1Loader);
            fillCardDeck(deck2, deck2Loader);
            fillCardDeck(deck3, deck3Loader);
            fillNobleTileDeck(nobleTiles, nobleTilesLoader);
            
        } catch (NumberFormatException e) {
            System.out.println("File has invalid format");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong parameters parsed in");
            return;
        } finally {
            closeScanners(scanners);
        }

        hasDataBeenLoaded = true;
    }

    /**
     * Method that returns a Scanner that is ready to scan a file.
     *
     * @param filePathAndName The file path to the desired file, and the file
     * name.
     * @return A Scanner ready to read the file.
     * @throws FileNotFoundException If the desired file cannot be found from
     * the given filepath.
     */
    private static Scanner setupScanner(String filePathAndName) throws FileNotFoundException {
        try {
            Scanner sc = new Scanner(new File(filePathAndName));
            return sc;
        } catch (FileNotFoundException e) {
            System.out.println("No " + filePathAndName);
            throw new FileNotFoundException("Could not load file " + filePathAndName);
        }
    }

    /**
     * Returns the next integer number in the file that exists on a line by
     * itself. Will ignore empty lines or commented lines (With a '#') in the
     * file.
     *
     * @param fileScanner The Scanner currently looking through the file.
     * @return The next integer retrieved from the file.
     * @throws NumberFormatException If a line that's not empty or a comment
     * can't be parsed into an integer.
     */
    private static int getNextNo(Scanner fileScanner) throws NumberFormatException {
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.equals("") || line.charAt(0) == '#') {
                continue;
            }

            return Integer.parseInt(line);
        }

        return -1;
    }

    /**
     * Will fill a given deck with {@link Card} using information from a file
     * Scanner.
     *
     * @param deck The deck to be filled with {@link Card}.
     * @param sc The Scanner reading from a file.
     */
    private static void fillCardDeck(Deck<Card> deck, Scanner sc) {
        sc.useDelimiter(",|\r\n|\n");

        while (sc.hasNext()) {
            deck.addToDeck(new Card(Utility.fromCharToGem(sc.next().charAt(0)), sc.nextInt(), sc.nextInt(),
                    sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
        }
    }

    /**
     * Will fill a given deck with {@link NobleTile} using information from a
     * file Scanner.
     *
     * @param deck The deck to be filled with {@link NobleTile}.
     * @param sc The Scanner reading from a file.
     */
    private static void fillNobleTileDeck(Deck<NobleTile> nobleTiles, Scanner nobleTilesLoader) {
        while (nobleTilesLoader.hasNextLine()) {
            String line = nobleTilesLoader.nextLine();
            String[] nobleTileGems = line.split(",");

            switch (nobleTileGems.length) {
                case 2:
                    nobleTiles.addToDeck(new NobleTile(Utility.fromCharToGem(nobleTileGems[0].charAt(0)),
                            Utility.fromCharToGem(nobleTileGems[1].charAt(0))));
                    break;
                case 3:
                    nobleTiles.addToDeck(new NobleTile(Utility.fromCharToGem(nobleTileGems[0].charAt(0)),
                            Utility.fromCharToGem(nobleTileGems[1].charAt(0)),
                            Utility.fromCharToGem(nobleTileGems[2].charAt(0))));
                    break;
            }
        }
    }

    /**
     * Closes all scanners parsed in.
     *
     * @param scanners An ArrayList of the Scanners to be closed.
     */
    private static void closeScanners(ArrayList<Scanner> scanners) {
        for (Scanner sc : scanners) {
            if (sc == null) {
                continue;
            }

            sc.close();
        }
    }
}
