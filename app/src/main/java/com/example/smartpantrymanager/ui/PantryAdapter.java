package com.example.smartpantrymanager.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.data.PantryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Binds PantryItem objects from the database to rows in the pantry RecyclerView.
 * Highlights items that are expired or expiring soon, based on the user's settings.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private static final long DAY_MS = 24L * 60 * 60 * 1000;
    private static final int WARNING_COLOR = Color.parseColor("#C62828");
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    private boolean expiryAlertsEnabled = true;
    private int warningDays = 3;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    /** Called by MainActivity with the values saved on the Settings screen. */
    public void setExpirySettings(boolean enabled, int days) {
        this.expiryAlertsEnabled = enabled;
        this.warningDays = days;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener, expiryAlertsEnabled, warningDays);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    /** Whole days from today (midnight) until the expiry date; negative if already expired. */
    static long daysUntil(long expiryMillis) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return Math.round((expiryMillis - today.getTimeInMillis()) / (double) DAY_MS);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvQuantity, tvExpiry;
        private final ColorStateList defaultExpiryColor;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            defaultExpiryColor = tvExpiry.getTextColors();
        }

        void bind(PantryItem item, OnItemClickListener listener,
                  boolean alertsEnabled, int warningDays) {
            tvName.setText(item.getName());
            tvQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

            // Reset styling first: recycled rows may still carry a previous item's warning colour
            tvExpiry.setTextColor(defaultExpiryColor);
            tvExpiry.setTypeface(null, Typeface.NORMAL);

            if (item.getExpiryDate() == null) {
                tvExpiry.setVisibility(View.GONE);
            } else {
                tvExpiry.setVisibility(View.VISIBLE);
                String date = DATE_FORMAT.format(new Date(item.getExpiryDate()));
                long days = daysUntil(item.getExpiryDate());

                if (alertsEnabled && days < 0) {
                    tvExpiry.setText("Expired: " + date);
                    tvExpiry.setTextColor(WARNING_COLOR);
                    tvExpiry.setTypeface(null, Typeface.BOLD);
                } else if (alertsEnabled && days <= warningDays) {
                    tvExpiry.setText(days == 0 ? "Expires today"
                            : "Expires in " + days + (days == 1 ? " day" : " days"));
                    tvExpiry.setTextColor(WARNING_COLOR);
                    tvExpiry.setTypeface(null, Typeface.BOLD);
                } else {
                    tvExpiry.setText("Expires: " + date);
                }
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}