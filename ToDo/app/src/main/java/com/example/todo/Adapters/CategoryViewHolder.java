package com.example.todo.Adapters;

import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    CheckBox categoryCheckBox;

    CategoryViewHolder(View view) {
        super(view);
        categoryCheckBox = view.findViewById(R.id.categoryCheckBox);
    }
}
