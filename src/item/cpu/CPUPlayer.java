package item.cpu;

import item.*;

public class CPUPlayer extends Player {
    private Move optimalMove;
    
    public CPUPlayer() {
        super();
    }

    public Move getMove() {
        return optimalMove;
    }
}
