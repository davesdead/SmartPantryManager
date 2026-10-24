package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.ui.PantryAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
/**
 * Add mode: opened with no extras, creates a new pantry item.
 * Edit mode: opened with EXTRA_ITEM_ID, loads and updates that item.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.example.smartpantrymanager.EXTRA_ITEM_ID";
    private static final String[] UNITS = {"g", "ml", "pcs"};

    private AppDatabase db;
    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity;
    private Spinner spUnit;
    private Button btnExpiry, btnClearExpiry;

    private Long selectedExpiry = null;     // null = no expiry set
    private PantryItem existingItem = null; // null = add mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // back arrow
        }

        db = AppDatabase.getInstance(this);

        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        spUnit = findViewById(R.id.spUnit);
        btnExpiry = findViewById(R.id.btnExpiry);
        btnClearExpiry = findViewById(R.id.btnClearExpiry);

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(unitAdapter);

        btnExpiry.setOnClickListener(v -> showDatePicker());
        btnClearExpiry.setOnClickListener(v -> {
            selectedExpiry = null;
            updateExpiryLabel();
        });
        findViewById(R.id.btnSave).setOnClickListener(v -> saveItem());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());

        // Read the data passed in by the Intent. -1 means no id was sent, so this is add mode.
        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);
        if (itemId == -1) {
            setTitle("Add Ingredient");
        } else {
            setTitle("Edit Ingredient");
            loadItem(itemId);
        }
    }

    /** Fetches the item on a background thread, then fills the form on the UI thread. */
    private void loadItem(int itemId) {
        AppDatabase.databaseExecutor.execute(() -> {
            PantryItem item = db.pantryItemDao().getById(itemId);
            runOnUiThread(() -> {
                if (item == null) {   // item was deleted elsewhere
                    finish();
                    return;
                }
                existingItem = item;
                etName.setText(item.getName());
                etQuantity.setText(PantryAdapter.formatQuantity(item.getQuantity()));
                spUnit.setSelection(Math.max(0, Arrays.asList(UNITS).indexOf(item.getUnit())));
                selectedExpiry = item.getExpiryDate();
                updateExpiryLabel();

                findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);   // only shown in edit mode
            });
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        if (selectedExpiry != null) cal.setTimeInMillis(selectedExpiry);

        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar picked = Calendar.getInstance();
            picked.set(year, month, day, 0, 0, 0);
            picked.set(Calendar.MILLISECOND, 0);
            selectedExpiry = picked.getTimeInMillis();
            updateExpiryLabel();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateExpiryLabel() {
        if (selectedExpiry == null) {
            btnExpiry.setText("Set expiry date");
            btnClearExpiry.setVisibility(View.GONE);
        } else {
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            btnExpiry.setText(fmt.format(new Date(selectedExpiry)));
            btnClearExpiry.setVisibility(View.VISIBLE);
        }
    }

    /** Validates the form; only writes to the database if every field is valid. */
    private void saveItem() {
        tilName.setError(null);
        tilQuantity.setError(null);

        String name = textOf(etName);
        String quantityText = textOf(etQuantity);
        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError("Ingredient name is required");
            valid = false;
        } else if (!name.matches(".*[a-zA-Z].*")) {
            tilName.setError("Name must contain letters");
            valid = false;
        }

        double quantity = 0;
        if (quantityText.isEmpty()) {
            tilQuantity.setError("Quantity is required");
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    tilQuantity.setError("Must be greater than 0");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Enter a valid number");
                valid = false;
            }
        }

        if (!valid) return;

        final boolean isNew = (existingItem == null);
        final PantryItem item = isNew ? new PantryItem() : existingItem;
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(UNITS[spUnit.getSelectedItemPosition()]);
        item.setExpiryDate(selectedExpiry);

        AppDatabase.databaseExecutor.execute(() -> {
            if (isNew) {
                db.pantryItemDao().insert(item);   // Create
            } else {
                db.pantryItemDao().update(item);   // Update
            }
            runOnUiThread(() -> {
                Toast.makeText(this, isNew ? "Ingredient added" : "Ingredient updated",
                        Toast.LENGTH_SHORT).show();
                finish();   // back to MainActivity, whose onResume reloads the list
            });
        });
    }

    /** Asks the user to confirm before permanently removing the item. */
    private void confirmDelete() {
        if (existingItem == null) return;   // nothing to delete in add mode

        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove \"" + existingItem.getName() + "\" from your pantry? This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteItem())
                .show();
    }

    private void deleteItem() {
        AppDatabase.databaseExecutor.execute(() -> {
            db.pantryItemDao().delete(existingItem);   // Delete
            runOnUiThread(() -> {
                Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                finish();   // MainActivity's onResume reloads the list without this item
            });
        });
    }

    private static String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    /** Toolbar back arrow closes this screen. */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}