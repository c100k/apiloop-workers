/**
 * 
 */
package io.apiloop.test.workers;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Utilities to test components easier
 */
public final class FixturesUtils {
    
    /**
     * 
     */
    private FixturesUtils() {
        
    }
    
    /**
     * Get a list of bad values for a string that should be mandatory
     * @return
     */
    public static List<String> mandatoryStringBadValues() {
        return asList(null, "");
    }

    /**
     * Get a list of bad values for an integer that should be mandatory
     * @return
     */
    public static List<Integer> mandatoryIntegerBadValues() {
        return singletonList(null);
    }

    /**
     * Get a list of bad values for a boolean that should be mandatory
     * @return
     */
    public static List<Boolean> mandatoryBooleanBadValues() {
        return singletonList(null);
    }
    
}
