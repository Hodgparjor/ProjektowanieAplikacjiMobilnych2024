package com.example.todo.Adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.DbHelper;
import com.example.todo.R;
import com.example.todo.Task;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
    private Context context;
    private List<Task> tasksList;
    private List<Task> allTasksList;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasksList = tasks;
        this.allTasksList = new ArrayList<>(tasks);
    }

    public void updateTasksList(List<Task> tasks) {
        this.tasksList = tasks;
        this.allTasksList = new ArrayList<>(tasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasksList.get(position);
        boolean isCompleted = task.isCompleted();
        holder.titleTextView.setText(task.getTitle());
        holder.categoryTextView.setText(task.getCategory());
        holder.descriptionTextView.setText(task.getDescription());
        holder.isCompletedCheckBox.setChecked(isCompleted);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = "Due: " + dateFormat.format(new Date(task.getDeadline()));
        holder.deadlineTextView.setText(formattedDate);

        if(isCompleted) {
            holder.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.secondaryLighter));
            holder.descriptionTextView.setTextColor(ContextCompat.getColor(context, R.color.secondaryLighter));
            holder.deadlineTextView.setTextColor(ContextCompat.getColor(context, R.color.secondaryLighter));
        } else {
            holder.titleTextView.setTextColor(ContextCompat.getColor(context, R.color.primary));
            holder.descriptionTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.deadlineTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.deleteButton.setOnClickListener(v -> {
            try (DbHelper db = new DbHelper(context)) {
                List<String> attachments = task.getAttachments();
                for (String attachment : attachments) {
                    File file = new File(attachment);
                    if (file.exists()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            Log.d("TaskAdapter", "Deleted file: " + attachment);
                        } else {
                            Log.d("TaskAdapter", "Failed to delete file: " + attachment);
                        }
                    }
                }
                task.setAttachments(new ArrayList<>());
                db.updateTask(task);
                db.deleteTask(task);
                tasksList.remove(task);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.isCompletedCheckBox.setOnClickListener(v -> {
            task.setCompleted(holder.isCompletedCheckBox.isChecked());
            try (DbHelper db = new DbHelper(context)) {
                db.updateTask(task);
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        });

        if(task.getAttachments() != null && !task.getAttachments().isEmpty() && !task.getAttachments().get(0).isEmpty()) {
            holder.attachmentImageView.setVisibility(View.VISIBLE);
        } else {
            holder.attachmentImageView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public void filter(String text) {
        List<Task> filteredTasks = new ArrayList<>();

        if (text.isEmpty()) {
            filteredTasks.addAll(allTasksList);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Task task : allTasksList) {
                if (task.getTitle().toLowerCase().contains(filterPattern) ||
                        task.getCategory().toLowerCase().contains(filterPattern)) {
                    filteredTasks.add(task);
                }
            }
        }

        tasksList.clear();
        tasksList.addAll(filteredTasks);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void sort(boolean isDescending) {
        if(isDescending) {
            tasksList.sort(Comparator.comparingLong(Task::getDeadline));
        } else {
            tasksList.sort((task1, task2) -> Long.compare(task2.getDeadline(), task1.getDeadline()));
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
