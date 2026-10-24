package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * One ingredient required by a recipe, with the quantity and unit needed.
 * recipeId links each row back to its parent Recipe.
 */
@Entity(
        tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index("recipeId")
)
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int recipeId;
    private String name;
    private double quantity;
    private String unit;    // "g", "ml" or "pcs"

    public RecipeIngredient() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}