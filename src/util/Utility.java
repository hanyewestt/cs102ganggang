package util;

import java.util.HashMap;
import item.*;

public class Utility {

    public static HashMap<Gem, Integer> generateEmptyHashmap() {
        HashMap<Gem, Integer> result = new HashMap<>(Gem.values().length);
        for (Gem gem : Gem.values()) {
            result.put(gem, 0);
        }

        return result;
    }
}
