package item.cpu;

import item.*;

public class CPUPlayer extends Player {
    private Move optimalMove;
    private static Game splendor;
    
    public CPUPlayer(Game splendor) {
        super();
        this.splendor = splendor;
    }

    public Game getGameState() {
        return splendor;
    }

    public Move getMove() {
        return optimalMove;
    }

    public void calculateOptimalMove() {
        HashMap<Gem, Integer> bank = splendor.getBank();
        ArrayList<Gem> availableGems = new ArrayList<>();

        for (Gem g : Gem.values()) {
            if (g.equals(Gem.Gold)) {
                continue;
            }

            int amountLeft = bank.get(g);
            if (amountLeft >= 2) {
                Draw2Gems draw2 = new Draw2Gems(this, g);
                optimalMove = draw2.isBetterMove(optimalMove) ? draw2 : optimalMove;
            }
            if (amountLeft >= 1) {
                availableGems.add(g);
            }
        }

        for (int i = 0; i <= availableGems.size() - 3; i++) {
            Draw3Gems draw3 = new Draw3Gems(this, availableGems.get(i), 
                            availableGems.get(i + 1), availableGems.get (i + 2));
            optimalMove = draw3.isBetterMove(optimalMove) ? draw3 : optimalMove;            
        }

        
    }
}
