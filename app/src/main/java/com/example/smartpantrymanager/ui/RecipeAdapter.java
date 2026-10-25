package com.example.smartpantrymanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.data.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of recipes, each with a short subtitle line.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    /** One row in the list: the recipe plus the text shown beneath its name. */
    public static class Row {
        public final Recipe recipe;
        public final String subtitle;

        public Row(Recipe recipe, String subtitle) {
            this.recipe = recipe;
            this.subtitle = subtitle;
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void setRows(List<Row> newRows) {
        rows.clear();
        rows.addAll(newRows);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Row row = rows.get(position);
        holder.tvName.setText(row.recipe.getName());
        holder.tvSubtitle.setText(row.subtitle);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(row.recipe));
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvSubtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvSubtitle = itemView.findViewById(R.id.tvRecipeSubtitle);
        }
    }
}
