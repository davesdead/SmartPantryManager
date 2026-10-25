package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeIngredient;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.ui.PantryAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Shows a recipe's full ingredient list (marked against the pantry) and its method.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.example.smartpantrymanager.EXTRA_RECIPE_ID";

    private AppDatabase db;
    private TextView tvIngredients, tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvSteps = findViewById(R.id.tvSteps);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {   // opened without a recipe: nothing to show
            finish();
            return;
        }
        loadRecipe(recipeId);
    }

    private void loadRecipe(int recipeId) {
        AppDatabase.databaseExecutor.execute(() -> {
            Recipe recipe = db.recipeDao().getRecipeById(recipeId);
            if (recipe == null) {
                runOnUiThread(this::finish);
                return;
            }
            List<RecipeIngredient> ingredients = db.recipeDao().getIngredientsForRecipe(recipeId);
            Map<String, Double> pantryIndex =
                    RecipeMatcher.buildPantryIndex(db.pantryItemDao().getAll());

            // Ingredient list, each marked against the current pantry
            StringBuilder ing = new StringBuilder();
            for (RecipeIngredient ri : ingredients) {
                boolean inPantry =
                        RecipeMatcher.countMissing(Collections.singletonList(ri), pantryIndex) == 0;
                ing.append(inPantry ? "✓  " : "✗  ")
                        .append(PantryAdapter.formatQuantity(ri.getQuantity()))
                        .append(" ").append(ri.getUnit())
                        .append("  ").append(ri.getName())
                        .append("\n");
            }

            // Steps are stored one per line; number them for display
            StringBuilder steps = new StringBuilder();
            String[] lines = recipe.getSteps().split("\n");
            for (int i = 0; i < lines.length; i++) {
                steps.append(i + 1).append(". ").append(lines[i]).append("\n");
            }

            runOnUiThread(() -> {
                setTitle(recipe.getName());
                tvIngredients.setText(ing.toString().trim());
                tvSteps.setText(steps.toString().trim());
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}