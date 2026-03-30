package item.cpu.move;

import app.*;
import item.*;
import item.cpu.*;
import util.*;

import java.util.*;

/**
 * {@link CPUPlayer} does nothing.
 */
public class NoPossibleMove extends Move {
    /**
     * Stores information of {@link CPUPlayer} to do nothing.
     */
    public NoPossibleMove(CPUPlayer cpu) {
        super(cpu);
    }

    /**
     * If there are no possible moves, the CPUPlayer does nothing.
     */
    public void doMove() {
    }
}