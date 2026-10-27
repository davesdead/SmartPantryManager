package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartpantrymanager.data.Prefs;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Lets the user control expiring-soon highlighting. Changes are saved immediately.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final int[] DAY_OPTIONS = {1, 3, 7};
    private static final String[] DAY_LABELS = {"1 day", "3 days", "7 days"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Settings");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MaterialSwitch switchExpiry = findViewById(R.id.switchExpiry);
        Spinner spDays = findViewById(R.id.spDays);

        ArrayAdapter<String> daysAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DAY_LABELS);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDays.setAdapter(daysAdapter);

        // Show the currently saved values
        boolean enabled = Prefs.isExpiryAlertsEnabled(this);
        switchExpiry.setChecked(enabled);
        spDays.setEnabled(enabled);
        int savedDays = Prefs.getWarningDays(this);
        for (int i = 0; i < DAY_OPTIONS.length; i++) {
            if (DAY_OPTIONS[i] == savedDays) spDays.setSelection(i);
        }

        // Save changes immediately
        switchExpiry.setOnCheckedChangeListener((button, isChecked) -> {
            Prefs.setExpiryAlertsEnabled(this, isChecked);
            spDays.setEnabled(isChecked);   // days setting only matters when alerts are on
        });

        spDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.setWarningDays(SettingsActivity.this, DAY_OPTIONS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}