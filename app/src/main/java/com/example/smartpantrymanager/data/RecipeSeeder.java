package com.example.smartpantrymanager.data;

import java.util.Arrays;

/**
 * Pre-loads the recipe collection the first time the app runs.
 * Ingredient names are lowercase and singular to keep matching consistent.
 * Units are limited to "g", "ml" and "pcs".
 */
public final class RecipeSeeder {

    private RecipeSeeder() { }

    public static void seedIfEmpty(AppDatabase db) {
        RecipeDao dao = db.recipeDao();
        if (dao.countRecipes() > 0) return;   // already seeded on a previous run

        // A transaction ensures the seed is all-or-nothing:
        // if the app is killed mid-seed, no half-loaded data is left behind.
        db.runInTransaction(() -> {
            add(dao, "Scrambled Eggs",
                    "Whisk the eggs and milk together.\nMelt butter in a pan over low heat.\nAdd the eggs and stir gently until just set.",
                    ing("egg", 3, "pcs"), ing("milk", 50, "ml"), ing("butter", 10, "g"));

            add(dao, "French Toast",
                    "Whisk eggs and milk in a shallow dish.\nDip each bread slice in the mixture.\nFry in butter until golden on both sides.",
                    ing("bread", 2, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"), ing("butter", 10, "g"));

            add(dao, "Pancakes",
                    "Mix flour, eggs and milk into a smooth batter.\nMelt a little butter in a pan.\nPour in batter and cook until bubbles form, then flip.",
                    ing("flour", 200, "g"), ing("egg", 2, "pcs"), ing("milk", 300, "ml"), ing("butter", 20, "g"));

            add(dao, "Cheese Omelette",
                    "Whisk the eggs.\nMelt butter in a pan and pour in the eggs.\nSprinkle cheese over one half, fold and cook until set.",
                    ing("egg", 3, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"));

            add(dao, "Tomato Pasta",
                    "Boil the pasta until tender.\nFry chopped onion and garlic in oil.\nAdd chopped tomatoes and simmer for 10 minutes.\nToss the sauce with the pasta.",
                    ing("pasta", 200, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"), ing("oil", 15, "ml"));

            add(dao, "Garlic Butter Pasta",
                    "Boil the pasta until tender.\nMelt butter and fry crushed garlic for 1 minute.\nToss with pasta and top with cheese.",
                    ing("pasta", 200, "g"), ing("butter", 40, "g"), ing("garlic", 3, "pcs"), ing("cheese", 30, "g"));

            add(dao, "Grilled Cheese Sandwich",
                    "Butter the outside of both bread slices.\nPlace cheese between the slices.\nFry in a pan until golden and the cheese melts.",
                    ing("bread", 2, "pcs"), ing("cheese", 60, "g"), ing("butter", 15, "g"));

            add(dao, "Egg Fried Rice",
                    "Cook the rice and let it cool.\nFry chopped onion and carrot in oil.\nAdd rice and stir-fry, then push aside and scramble the eggs.\nMix everything together.",
                    ing("rice", 200, "g"), ing("egg", 2, "pcs"), ing("onion", 1, "pcs"), ing("carrot", 1, "pcs"), ing("oil", 15, "ml"));

            add(dao, "Potato Wedges",
                    "Cut potatoes into wedges.\nToss with oil.\nBake at 200°C for 35 minutes, turning halfway.",
                    ing("potato", 4, "pcs"), ing("oil", 30, "ml"));

            add(dao, "Mashed Potatoes",
                    "Peel and boil the potatoes until soft.\nDrain and mash with butter.\nStir in warm milk until smooth.",
                    ing("potato", 4, "pcs"), ing("butter", 30, "g"), ing("milk", 100, "ml"));

            add(dao, "Tomato Soup",
                    "Fry chopped onion and garlic in butter.\nAdd chopped tomatoes and a cup of water.\nSimmer for 20 minutes, then blend until smooth.",
                    ing("tomato", 6, "pcs"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"), ing("butter", 20, "g"));

            add(dao, "Chicken Stir-Fry",
                    "Cook the rice.\nSlice chicken and fry in oil until cooked through.\nAdd sliced onion and carrot and stir-fry for 5 minutes.\nServe over rice.",
                    ing("chicken breast", 300, "g"), ing("carrot", 2, "pcs"), ing("onion", 1, "pcs"), ing("rice", 200, "g"), ing("oil", 15, "ml"));

            add(dao, "Spanish Omelette",
                    "Thinly slice potatoes and onion.\nFry slowly in oil until soft.\nPour beaten eggs over and cook until set, then flip.",
                    ing("egg", 4, "pcs"), ing("potato", 2, "pcs"), ing("onion", 1, "pcs"), ing("oil", 30, "ml"));

            add(dao, "Banana Smoothie",
                    "Peel the bananas.\nBlend with milk until smooth.",
                    ing("banana", 2, "pcs"), ing("milk", 250, "ml"));

            add(dao, "Banana Bread",
                    "Mash the bananas.\nMix in melted butter, sugar and eggs.\nFold in flour.\nBake at 180°C for 55 minutes.",
                    ing("banana", 3, "pcs"), ing("flour", 250, "g"), ing("egg", 2, "pcs"), ing("butter", 80, "g"), ing("sugar", 150, "g"));

            add(dao, "Chicken and Rice",
                    "Fry chopped onion and garlic.\nAdd diced chicken and brown it.\nAdd rice and two cups of water, cover and simmer for 20 minutes.",
                    ing("chicken breast", 300, "g"), ing("rice", 200, "g"), ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"));

            add(dao, "Macaroni and Cheese",
                    "Boil the pasta until tender.\nMelt butter, add milk and heat gently.\nStir in cheese until melted.\nMix sauce with pasta.",
                    ing("pasta", 200, "g"), ing("cheese", 100, "g"), ing("milk", 200, "ml"), ing("butter", 30, "g"));

            add(dao, "Banana Oat Porridge",
                    "Simmer oats in milk for 5 minutes, stirring.\nSlice the banana on top.",
                    ing("oats", 80, "g"), ing("milk", 250, "ml"), ing("banana", 1, "pcs"));
        });
    }

    /** Inserts one recipe, then its ingredients linked by the new recipe id. */
    private static void add(RecipeDao dao, String name, String steps, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setSteps(steps);
        int recipeId = (int) dao.insertRecipe(recipe);

        for (RecipeIngredient i : ingredients) {
            i.setRecipeId(recipeId);
        }
        dao.insertIngredients(Arrays.asList(ingredients));
    }

    /** Shorthand for building one ingredient row. */
    private static RecipeIngredient ing(String name, double quantity, String unit) {
        RecipeIngredient i = new RecipeIngredient();
        i.setName(name);
        i.setQuantity(quantity);
        i.setUnit(unit);
        return i;
    }
}
