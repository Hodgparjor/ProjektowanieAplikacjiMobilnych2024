package com.example.todo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<String> categories;
    private Set<String> selectedCategories;

    public CategoryAdapter(Context context, List<String> categories, Set<String> selectedCategoriesSet) {
        this.categories = categories;
        this.selectedCategories = new HashSet<>(selectedCategoriesSet);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryCheckBox.setText(category);
        holder.categoryCheckBox.setChecked(selectedCategories.contains(category));
        holder.categoryCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedCategories.add(category);
            } else {
                selectedCategories.remove(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public Set<String> getSelectedCategories() {
        return selectedCategories;
    }

}
