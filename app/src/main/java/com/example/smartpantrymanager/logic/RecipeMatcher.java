package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.RecipeIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implements the strict-matching rule: a recipe is suggested only if EVERY
 * required ingredient is in the pantry in at least the required quantity.
 *
 * Contains no Android code, so it can be unit tested directly.
 */
public final class RecipeMatcher {

    private static final double EPSILON = 1e-9;   // tolerance for floating-point comparison

    private RecipeMatcher() { }

    /**
     * Makes ingredient names comparable despite trivial differences:
     * "  Tomatoes " -> "tomato", "Chicken Breasts" -> "chicken breast".
     */
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String s = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z ]", " ")   // remove digits and punctuation
                .replaceAll("\\s+", " ")      // collapse repeated spaces
                .trim();
        if (s.isEmpty()) return s;

        // Only the last word is made singular: "chicken breasts" -> "chicken breast"
        int lastSpace = s.lastIndexOf(' ');
        String head = s.substring(0, lastSpace + 1);
        String last = s.substring(lastSpace + 1);
        return head + singularize(last);
    }

    /** Simple English plural rules; enough for typical ingredient names. */
    static String singularize(String w) {
        if (w.length() <= 3) return w;
        if (w.endsWith("ies")) return w.substring(0, w.length() - 3) + "y";      // berries -> berry
        if (w.endsWith("oes")) return w.substring(0, w.length() - 2);            // tomatoes -> tomato
        if (w.endsWith("ches") || w.endsWith("shes") || w.endsWith("xes")) {
            return w.substring(0, w.length() - 2);                                // peaches -> peach
        }
        if (w.endsWith("ss") || w.endsWith("us")) return w;                       // leave "asparagus"
        if (w.endsWith("s")) return w.substring(0, w.length() - 1);               // eggs -> egg
        return w;
    }

    /** Groups units by what they measure: weight, volume or count. */
    static String baseUnit(String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        switch (u) {
            case "g":
            case "kg":
                return "g";
            case "ml":
            case "l":
                return "ml";
            default:
                return u;   // "pcs"
        }
    }

    /** Converts a quantity into its base unit, e.g. 1.5 kg -> 1500 g. */
    static double toBase(double quantity, String unit) {
        String u = unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
        if (u.equals("kg") || u.equals("l")) return quantity * 1000;
        return quantity;
    }

    private static String key(String name, String unit) {
        return normalizeName(name) + "|" + baseUnit(unit);
    }

    /**
     * Totals the pantry by normalised name and base unit.
     * Duplicate entries are summed: two "egg" rows of 2 pcs = 4 pcs available.
     */
    public static Map<String, Double> buildPantryIndex(List<PantryItem> pantry) {
        Map<String, Double> index = new HashMap<>();
        for (PantryItem item : pantry) {
            index.merge(key(item.getName(), item.getUnit()),
                    toBase(item.getQuantity(), item.getUnit()),
                    Double::sum);
        }
        return index;
    }

    /**
     * Counts how many of a recipe's ingredients the pantry cannot satisfy.
     * An ingredient is missing if its name is absent, its unit type differs,
     * or the available quantity is below what the recipe needs.
     */
    public static int countMissing(List<RecipeIngredient> required, Map<String, Double> pantryIndex) {
        int missing = 0;
        for (RecipeIngredient ri : required) {
            double have = pantryIndex.getOrDefault(key(ri.getName(), ri.getUnit()), 0.0);
            double need = toBase(ri.getQuantity(), ri.getUnit());
            if (have + EPSILON < need) {
                missing++;
            }
        }
        return missing;
    }

    /** The strict rule: suggested only when nothing is missing. */
    public static boolean canMake(List<RecipeIngredient> required, Map<String, Double> pantryIndex) {
        return !required.isEmpty() && countMissing(required, pantryIndex) == 0;
    }
}