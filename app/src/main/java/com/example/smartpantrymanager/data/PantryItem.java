package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents one ingredient the user currently has in their pantry.
 * Room maps this class to the "pantry_items" table.
 */
@Entity(tableName = "pantry_items")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private double quantity;
    private String unit;        // "g", "ml" or "pcs"
    private Long expiryDate;    // epoch milliseconds; null if no expiry set

    public PantryItem() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Long expiryDate) { this.expiryDate = expiryDate; }
}