package config;

import item.*;
import java.io.*;
import java.util.*;

public class Configuration {

    private static int nobleTilePoints;
    private static int pointsToWin;
    private static int[] startingGems = {0, 0, 0};

    private static Deck<Card> deck1 = new Deck<Card>();
    private static Deck<Card> deck2 = new Deck<Card>();
    private static Deck<Card> deck3 = new Deck<Card>();

    private static Deck<NobleTile> nobleTiles = new Deck<NobleTile>();

    public static int getNobleTilePoints() {
        return nobleTilePoints;
    }

    public static int getPointsToWin() {
        return pointsToWin;
    }

    public static int getStartingGems(int playerNo) {
        return startingGems[playerNo - 2];
    }

    public static Deck<Card> getDeck(int deckNo) {
        Deck<Card> result = null;

        switch (deckNo) {
            case 1:
                result = deck1;
                break;
            case 2:
                result = deck2;
                break;
            case 3:
                result = deck3;
                break;
        }

        return result;
    }

    public static Deck<NobleTile> getNobleTiles() {
        return nobleTiles;
    }

    public static void load() {
        Scanner numLoader;
        Scanner deck1Loader;
        Scanner deck2Loader;
        Scanner deck3Loader;
        Scanner nobleTilesLoader;

        try {
            numLoader = setupScanner("src/data/numbers.csv");
            deck1Loader = setupScanner("src/data/deck1.csv");
            deck2Loader = setupScanner("src/data/deck2.csv");
            deck3Loader = setupScanner("src/data/deck3.csv");
            nobleTilesLoader = setupScanner("src/data/nobletiles.csv");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }

        try {
            nobleTilePoints = getNextNo(numLoader);
            pointsToWin = getNextNo(numLoader);

            for (int i = 0; i < 3; i++) {
                startingGems[i] = getNextNo(numLoader);
            }
        } catch (NumberFormatException e) {
            System.out.println("File has invalid format");
            return;
        }

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
        }
    }

    private static Scanner setupScanner(String filePathAndName) throws FileNotFoundException {
        try {
            Scanner sc = new Scanner(new File(filePathAndName));
            return sc;
        } catch (FileNotFoundException e) {
            System.out.println("No " + filePathAndName);
            throw new FileNotFoundException("Could not load file " + filePathAndName);
        }
    }

    private static int getNextNo(Scanner fileScanner) {
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            if (line.equals("") || line.charAt(0) == '#') {
                continue;
            }

            return Integer.parseInt(line);
        }

        return -1;
    }

    private static void fillCardDeck(Deck<Card> deck, Scanner sc) {
        sc.useDelimiter(",|\r\n|\n");

        while (sc.hasNext()) {
            deck.addToDeck(new Card(fromCharToGem(sc.next().charAt(0)), sc.nextInt(), sc.nextInt(),
                    sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
        }
    }

    private static void fillNobleTileDeck(Deck<NobleTile> nobleTiles, Scanner nobleTilesLoader) {
        while (nobleTilesLoader.hasNextLine()) {
            String line = nobleTilesLoader.nextLine();
            String[] nobleTileGems = line.split(",");

            switch (nobleTileGems.length) {
                case 2:
                    nobleTiles.addToDeck(new NobleTile(fromCharToGem(nobleTileGems[0].charAt(0)),
                            fromCharToGem(nobleTileGems[1].charAt(0))));
                    break;
                case 3:
                    nobleTiles.addToDeck(new NobleTile(fromCharToGem(nobleTileGems[0].charAt(0)),
                            fromCharToGem(nobleTileGems[1].charAt(0)),
                            fromCharToGem(nobleTileGems[2].charAt(0))));
                    break;
            }
        }
    }

    private static Gem fromCharToGem(char character) {
        switch (character) {
            case 'D':
                return Gem.Diamond;
            case 'R':
                return Gem.Ruby;
            case 'S':
                return Gem.Sapphire;
            case 'E':
                return Gem.Emerald;
            case 'O':
                return Gem.Onyx;
            default:
                return null;
        }
    }
}
