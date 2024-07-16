package com.example.todo.Adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

public class AttachmentViewHolder extends RecyclerView.ViewHolder {
    TextView attachmentFileNameTextView;
    ImageButton attachmentDeleteBtn;
    public AttachmentViewHolder(@NonNull View itemView) {
        super(itemView);
        attachmentFileNameTextView = itemView.findViewById(R.id.attachmentFileName);
        attachmentDeleteBtn = itemView.findViewById(R.id.attachmentDeleteBtn);

    }
}
