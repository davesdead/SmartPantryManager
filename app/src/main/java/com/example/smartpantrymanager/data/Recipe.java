package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * A recipe from the seeded collection.
 * Its ingredients are stored separately in the recipe_ingredients table.
 */
@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String steps;   // preparation method, one step per line

    public Recipe() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
}
