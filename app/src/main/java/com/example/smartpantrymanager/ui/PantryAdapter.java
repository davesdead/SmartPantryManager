package com.example.smartpantrymanager.ui;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Binds PantryItem objects from the database to rows in the pantry RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets MainActivity react when a row is tapped, without the adapter knowing about Activities. */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** Replaces the displayed list with fresh data from the database. */
    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
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
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Shows 2.0 as "2" but keeps 0.5 as "0.5". */
    public static String formatQuantity(double q) {
        return q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvQuantity, tvExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
        }

        void bind(PantryItem item, OnItemClickListener listener) {
            tvName.setText(item.getName());
            tvQuantity.setText(formatQuantity(item.getQuantity()) + " " + item.getUnit());

            if (item.getExpiryDate() != null) {
                tvExpiry.setText("Expires: " + DATE_FORMAT.format(new Date(item.getExpiryDate())));
                tvExpiry.setVisibility(View.VISIBLE);
            } else {
                tvExpiry.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}