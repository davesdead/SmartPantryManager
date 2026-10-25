package com.example.smartpantrymanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.RecipeIngredient;
import com.example.smartpantrymanager.logic.RecipeMatcher;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RecipeMatcherTest {

    private static PantryItem have(String name, double qty, String unit) {
        PantryItem p = new PantryItem();
        p.setName(name);
        p.setQuantity(qty);
        p.setUnit(unit);
        return p;
    }

    private static RecipeIngredient need(String name, double qty, String unit) {
        RecipeIngredient r = new RecipeIngredient();
        r.setName(name);
        r.setQuantity(qty);
        r.setUnit(unit);
        return r;
    }

    private static boolean canMake(List<PantryItem> pantry, List<RecipeIngredient> recipe) {
        Map<String, Double> index = RecipeMatcher.buildPantryIndex(pantry);
        return RecipeMatcher.canMake(recipe, index);
    }

    @Test
    public void allIngredientsPresent_isSuggested() {
        assertTrue(canMake(
                Arrays.asList(have("egg", 3, "pcs"), have("milk", 100, "ml"), have("butter", 50, "g")),
                Arrays.asList(need("egg", 3, "pcs"), need("milk", 50, "ml"), need("butter", 10, "g"))));
    }

    @Test
    public void fourOfFiveIngredients_isNotSuggested() {
        assertFalse(canMake(
                Arrays.asList(have("pasta", 500, "g"), have("tomato", 5, "pcs"),
                        have("onion", 2, "pcs"), have("garlic", 4, "pcs")),          // no oil
                Arrays.asList(need("pasta", 200, "g"), need("tomato", 3, "pcs"),
                        need("onion", 1, "pcs"), need("garlic", 2, "pcs"), need("oil", 15, "ml"))));
    }

    @Test
    public void insufficientQuantity_isNotSuggested() {
        assertFalse(canMake(
                Arrays.asList(have("egg", 2, "pcs")),
                Arrays.asList(need("egg", 3, "pcs"))));
    }

    @Test
    public void pluralAndCaseDifferences_stillMatch() {
        assertTrue(canMake(
                Arrays.asList(have("  Tomatoes ", 6, "pcs")),
                Arrays.asList(need("tomato", 6, "pcs"))));
    }

    @Test
    public void kilogramsSatisfyGrams() {
        assertTrue(canMake(
                Arrays.asList(have("flour", 1, "kg")),
                Arrays.asList(need("flour", 250, "g"))));
    }

    @Test
    public void differentUnitTypes_doNotMatch() {
        assertFalse(canMake(
                Arrays.asList(have("egg", 200, "g")),
                Arrays.asList(need("egg", 2, "pcs"))));
    }

    @Test
    public void duplicatePantryEntries_areSummed() {
        assertTrue(canMake(
                Arrays.asList(have("egg", 2, "pcs"), have("eggs", 2, "pcs")),
                Arrays.asList(need("egg", 3, "pcs"))));
    }

    @Test
    public void countMissing_reportsOneForAlmostThere() {
        Map<String, Double> index = RecipeMatcher.buildPantryIndex(
                Arrays.asList(have("banana", 3, "pcs")));
        assertEquals(1, RecipeMatcher.countMissing(
                Arrays.asList(need("banana", 2, "pcs"), need("milk", 250, "ml")), index));
    }

    @Test
    public void normalizeName_handlesCommonCases() {
        assertEquals("tomato", RecipeMatcher.normalizeName("Tomatoes"));
        assertEquals("berry", RecipeMatcher.normalizeName("berries"));
        assertEquals("chicken breast", RecipeMatcher.normalizeName("Chicken  Breasts!"));
        assertEquals("egg", RecipeMatcher.normalizeName("EGGS"));
    }
}