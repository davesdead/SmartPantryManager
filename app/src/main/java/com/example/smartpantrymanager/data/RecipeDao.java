package com.example.smartpantrymanager.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Operations for the recipe collection and each recipe's ingredients.
 */
@Dao
public interface RecipeDao {

    @Insert
    long insertRecipe(Recipe recipe);          // returns the new recipe's id

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    Recipe getRecipeById(int id);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredientsForRecipe(int recipeId);

    @Query("SELECT COUNT(*) FROM recipes")
    int countRecipes();                        // used to check if seeding is needed
}
