package com.example.smartpantrymanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The app's Room database. Holds the pantry, recipe and recipe-ingredient tables.
 * Uses the singleton pattern so only one database connection exists app-wide.
 */
@Database(
        entities = {PantryItem.class, Recipe.class, RecipeIngredient.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PantryItemDao pantryItemDao();
    public abstract RecipeDao recipeDao();

    private static volatile AppDatabase INSTANCE;

    // Background threads for database work, since Room blocks queries on the UI thread
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry.db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}