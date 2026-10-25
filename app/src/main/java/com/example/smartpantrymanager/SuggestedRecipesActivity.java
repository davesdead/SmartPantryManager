package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.Recipe;
import com.example.smartpantrymanager.data.RecipeIngredient;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.ui.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lists only the recipes the user can make right now, using the strict-matching rule.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private AppDatabase db;
    private RecipeAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Suggested Recipes");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = AppDatabase.getInstance(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvSuggested);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);
    }

    /** Re-runs matching whenever the screen is shown, so it always reflects the current pantry. */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        AppDatabase.databaseExecutor.execute(() -> {
            // Build the pantry index once, then test every recipe against it
            Map<String, Double> pantryIndex =
                    RecipeMatcher.buildPantryIndex(db.pantryItemDao().getAll());

            List<RecipeAdapter.Row> rows = new ArrayList<>();
            for (Recipe recipe : db.recipeDao().getAllRecipes()) {
                List<RecipeIngredient> needed = db.recipeDao().getIngredientsForRecipe(recipe.getId());
                if (RecipeMatcher.canMake(needed, pantryIndex)) {
                    rows.add(new RecipeAdapter.Row(recipe,
                            needed.size() + " ingredients · all in your pantry"));
                }
            }

            runOnUiThread(() -> {
                adapter.setRows(rows);
                tvEmpty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}