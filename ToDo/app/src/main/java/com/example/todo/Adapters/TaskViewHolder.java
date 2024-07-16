package com.example.todo.Adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView descriptionTextView;
    TextView categoryTextView;
    TextView deadlineTextView;
    CheckBox isCompletedCheckBox;
    ImageView deleteButton;
    ImageView attachmentImageView;

    TaskViewHolder(View view) {
        super(view);
        titleTextView = view.findViewById(R.id.taskTitle);
        descriptionTextView = view.findViewById(R.id.taskDescription);
        categoryTextView = view.findViewById(R.id.taskCategory);
        deadlineTextView = view.findViewById(R.id.taskDeadline);
        isCompletedCheckBox = view.findViewById(R.id.isCompletedCheckBox);
        deleteButton = view.findViewById(R.id.deleteTaskBtn);
        attachmentImageView = view.findViewById(R.id.attachmentImageView);
    }
}
