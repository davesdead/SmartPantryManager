package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.RecipeSeeder;
import com.example.smartpantrymanager.ui.PantryAdapter;
import android.content.Intent;

import java.util.List;

/**
 * Home screen: displays every ingredient currently in the user's pantry.
 */
public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private PantryAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));

        db = AppDatabase.getInstance(this);
        AppDatabase.databaseExecutor.execute(() -> RecipeSeeder.seedIfEmpty(db));

        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvPantry = findViewById(R.id.rvPantry);
        rvPantry.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(item -> {
            // Explicit Intent carrying the tapped item's id -> opens in edit mode
            Intent intent = new Intent(this, AddEditIngredientActivity.class);
            intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
            startActivity(intent);
        });
        rvPantry.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                // No extra -> opens in add mode
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    /**
     * onResume runs every time this screen comes back into view,
     * e.g. after returning from the Add/Edit screen, so the list is always current.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<PantryItem> items = db.pantryItemDao().getAll();   // background thread
            runOnUiThread(() -> {                                   // back to UI thread
                adapter.setItems(items);
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}