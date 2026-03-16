
import app.*;
import config.*;
import item.*;
import java.util.*;

public class Test {
    // for code testing purposes

    public static void main(String[] args) {
        // Scanner sc = new Scanner(System.in);

        // int testCaseNo = 0;
        // // Card card1 = new Card('D', 3, 2, 0, 2, 3, 0);

        // // System.out.println(card1.toString());
        // // Configuration.load();
        // // System.out.println(Configuration.getNobleTilePoints());
        // // System.out.println(Configuration.getPointsToWin());
        // // Deck<Card> deck1 = Configuration.getDeck(1);
        // // for (int i = 0; i < 10; i++) {
        // //     System.out.println(deck1.draw());
        // // }
        // // Test 1: Can easily buy the card
        // {
        //     System.out.println("Test Case " + ++testCaseNo);
        //     Card c = new Card(Gem.Ruby, 0, 1, 1, 1, 1, 1);
        //     Player p = new Player("Ben");
        //     p.addToken(Gem.Diamond, 3);
        //     p.addToken(Gem.Ruby, 5);
        //     p.addToken(Gem.Sapphire, 5);
        //     p.addToken(Gem.Emerald, 6);
        //     p.addToken(Gem.Onyx, 4);
        //     System.out.println("Can buy: " + p.buyCard(c, sc));
        //     System.out.println("Cards owned: " + p.getNumberOfCards());
        //     System.out.println("");
        // }
        // // Test 2: No Gold, can't buy
        // {
        //     System.out.println("Test Case " + ++testCaseNo);
        //     Card c = new Card(Gem.Ruby, 0, 1, 1, 1, 1, 1);
        //     Player p = new Player("Ben");
        //     // p.addToken(Gem.Diamond, 3);
        //     // p.addToken(Gem.Ruby, 5);
        //     // p.addToken(Gem.Sapphire, 5);
        //     // p.addToken(Gem.Emerald, 6);
        //     // p.addToken(Gem.Onyx, 4);
        //     System.out.println("Can buy: " + p.buyCard(c, sc));
        //     System.out.println("Cards owned: " + p.getNumberOfCards());
        //     System.out.println("");
        // }
        // Test 3: Can buy, must use all gold
        // {
        //     System.out.println("Test Case " + ++testCaseNo);
        //     Card c = new Card(Gem.Ruby, 0, 1, 1, 1, 1, 2);
        //     Player p = new Player("Ben");
        //     p.addToken(Gem.Diamond, 3);
        //     p.addToken(Gem.Ruby, 1);
        //     p.addToken(Gem.Sapphire, 1);
        //     p.addToken(Gem.Emerald, 1);
        //     p.addToken(Gem.Onyx, 1);
        //     p.addToken(Gem.Gold, 1);
        //     System.out.println("Can buy: " + p.buyCard(c, sc));
        //     System.out.println("Cards owned: " + p.getNumberOfCards());
        //     System.out.println("");
        // }
        // Test 4: Can buy, must use some gold
        // {
        //     System.out.println("Test Case " + ++testCaseNo);
        //     Card c = new Card(Gem.Ruby, 0, 1, 1, 1, 1, 2);
        //     Player p = new Player("Ben");
        //     p.addToken(Gem.Diamond, 3);
        //     p.addToken(Gem.Ruby, 1);
        //     p.addToken(Gem.Sapphire, 1);
        //     p.addToken(Gem.Emerald, 1);
        //     p.addToken(Gem.Onyx, 1);
        //     p.addToken(Gem.Gold, 7);

        //     System.out.println("Can buy: " + p.buyCard(c, sc));
        //     System.out.println("Cards owned: " + p.getNumberOfCards());
        //     System.out.print(p);
        //     System.out.println("");
        // }

        // Test 5: Testing production discounts
        // {
        //     System.out.println("Test Case " + ++testCaseNo);
        //     Card c = new Card(Gem.Ruby, 0, 1, 1, 1, 1, 2);
        //     Player p = new Player("Ben");
        //     p.addToken(Gem.Diamond, 3);
        //     p.addToken(Gem.Ruby, 0);
        //     p.addToken(Gem.Sapphire, 1);
        //     p.addToken(Gem.Emerald, 1);
        //     p.addToken(Gem.Onyx, 0);
        //     p.addToken(Gem.Gold, 7);

        //     p.addProduction(Gem.Ruby);
        //     p.addProduction(Gem.Onyx);

        //     System.out.println("Can buy: " + p.buyCard(c, sc));
        //     System.out.println("Cards owned: " + p.getNumberOfCards());
        //     System.out.println("");
        // }

        Game game = new Game(3);
        System.out.println(game.getSeed());
    }
 }


