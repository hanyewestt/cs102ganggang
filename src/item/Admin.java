package item;

import java.util.*;

/**
 * For testing purposes
 */
public class Admin extends Player {

    private int adminNo;

    /**
     * Creates an Admin object. All default Admin objects have 999 tokens
     * 
     * @param adminNo identifier for Admin
     */
    public Admin(int adminNo) {
        super("admin", 0);
        this.adminNo = adminNo;

        // by default, give admin 999 tokens
        for (Gem g : Gem.values()) {
            super.addToken(g, 999);
        }

    }

    /**
     * Takes in relevant params to create admin object that inherits data
     * 
     * @param name name of player
     * @param tokens player's existing tokens in hand
     * @param reserveCards player's existing reserve {@link Card}
     * @param production player's existing production levels
     * @param nobles player's exisiting noble tiles
     * @param points player's existing points
     */
    public Admin(String name, HashMap<Gem, Integer> tokens, List<Card> reserveCards, HashMap<Gem, Integer> production, List<NobleTile> nobles, int points) {

        super(name, 0);
        for (Gem g : Gem.values()) {
            setToken(g, tokens.get(g));
            setBonuses(g, production.get(g));
        }
        for (Card c : reserveCards) {
            super.reserveCard(c);
        }
        for (NobleTile noble : nobles) {
            super.addNobleTile(noble);
        }
        setPoints(points);
    }

    /**
     * Takes in a player object and turns it into admin object
     * 
     * @param p player object
     */
    public Admin(Player p) {
        this(p.getName(), p.getTokens(), p.getReserveHand(), p.getBonuses(), p.getOwnedNobleTile(), p.getPoints());
    }

    /**
     * Sets the token for the player as per specified Gem and amount.
     *
     * @param g Gem Type
     * @param amt amount to set to
     */
    public void setToken(Gem g, int amt) {
        super.addToken(g, amt - super.getTokens().get(g));
    }

    /**
     * Sets the production for the player as per specified Gem and amount.
     *
     * @param g Gem Type
     * @param amt amount to set to
     */
    public void setBonuses(Gem g, int amt) {
        super.addBonuses(g, amt - super.getBonuses().get(g));
    }

    /**
     * Sets the points for the player as per specified by amount.
     *
     * @param amt amount to set to
     */
    public void setPoints(int amt) {
        super.addPoints(amt - super.getPoints());
    }

    // public static void main(String[] args) {
    //     Admin a = new Admin();
    //     System.out.println(a);
    //     a.setToken(Gem.Diamond, 1);
    //     System.out.println(a);
    //     a.setToken(Gem.Diamond, 100);
    //     System.out.println(a);
    // }
}
