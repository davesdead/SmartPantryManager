package com.example.smartpantrymanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * CRUD operations for the user's pantry.
 */
@Dao
public interface PantryItemDao {

    @Insert
    long insert(PantryItem item);              // Create

    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    List<PantryItem> getAll();                 // Read (list)

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    PantryItem getById(int id);                // Read (single, for editing)

    @Update
    void update(PantryItem item);              // Update

    @Delete
    void delete(PantryItem item);              // Delete
}