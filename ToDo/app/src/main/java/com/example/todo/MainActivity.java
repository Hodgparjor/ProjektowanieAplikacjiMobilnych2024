package com.example.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;

    private DbHelper db;
    private boolean isSortDescending = true;

    private long selectedDeadline = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DbHelper(this);

        initializeButtons();
    }

    private void initializeButtons() {
        findViewById(R.id.settingsBtn).setOnClickListener(v -> { showSettingsDialog(); });
        findViewById(R.id.sortBtn).setOnClickListener(v -> { changeSorting(); });
        findViewById(R.id.addTaskBtn).setOnClickListener(v -> { showAddTaskDialog(); });
    }

    private void changeSorting() {
        ImageButton sortButton = findViewById(R.id.sortBtn);
        isSortDescending = !isSortDescending;

        if(isSortDescending) {
            sortButton.setImageResource(R.drawable.sort_descending);
        } else {
            sortButton.setImageResource(R.drawable.sort_ascending);
        }

        //TODO: Add sorting.

    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_task, null);
        builder.setView(view);
        
        long selectedDeadline = 1;
        List<String> selectedAttachments = new ArrayList<>();

        EditText titleEditText = view.findViewById(R.id.title);
        EditText descriptionEditText = view.findViewById(R.id.description);
        EditText categoryEditText = view.findViewById(R.id.category);
        CheckBox notificationCheckBox = view.findViewById(R.id.enableNotificationCheckBox);
        Button selectDateTimeBtn = view.findViewById(R.id.selectDateTimeBtn);
        TextView selectedDateTimeText = view.findViewById(R.id.selectedDateTimeText);
        RecyclerView attachmentsRecyclerView = view.findViewById(R.id.attachmentsRecyclerView);
        Button addAttachmentBtn = view.findViewById(R.id.addAttachmentBtn);

        selectDateTimeBtn.setOnClickListener(v -> showDateTimePicker(selectedDateTimeText));
//        addAttachmentBtn.setOnClickListener(v -> pickAttachment());
//
//        // Setup RecyclerView for attachments
//        attachmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        attachmentsAdapter = new AttachmentsAdapter(currentAttachments, this);
//        attachmentsRecyclerView.setAdapter(attachmentsAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String category = categoryEditText.getText().toString();
            boolean isNotificationEnabled = notificationCheckBox.isChecked();

            if (title.isEmpty() || category.isEmpty() || selectedDeadline == 0) {
                return;
            }

            long creationTime = System.currentTimeMillis();
            Task task = new Task(0, title, description, creationTime, selectedDeadline, false, isNotificationEnabled, category, new ArrayList<>(selectedAttachments));
            db.createTask(task);
//            scheduleNotification(task);
//            updateTaskList();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Delete all attachments added during the dialog
            for (String attachment : selectedAttachments) {
                File file = new File(attachment);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        Log.d("MainActivityFileHandler", "File deleted: " + attachment);
                    } else {
                        Log.d("MainActivityFileHandler", "Failed to delete file: " + attachment);
                    }
                }
            }
            selectedAttachments.clear();
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void showDateTimePicker(TextView dueDateTextView) {
        Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                newDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newDate.set(Calendar.MINUTE, minute);
                selectedDeadline = newDate.getTimeInMillis();
                dueDateTextView.setText(newDate.getTime().toString());
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.settings, null);
        builder.setView(view);

        CheckBox showCompletedTasksCheckBox = view.findViewById(R.id.showCompletedTasksCheckBox);
        RecyclerView categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        EditText notificationTimeEditText = view.findViewById(R.id.notificationTime);

//        showCompletedTasksCheckBox.setChecked(sharedPreferencesHelper.loadHideCompleted());
//        notificationTimeEditText.setText(String.valueOf(sharedPreferencesHelper.loadNotificationTime()));

        List<String> categories = db.getCategories();
        categories.add(0, "all");
//        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, categories, sharedPreferencesHelper.loadSelectedCategories());
//        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        categoriesRecyclerView.setAdapter(categoriesAdapter);

        builder.setPositiveButton("Save", (dialog, which) -> {
//            boolean hideCompleted = showCompletedTasksCheckBox.isChecked();
//            sharedPreferencesHelper.saveHideCompleted(hideCompleted);
//
//            Set<String> selectedCategories = categoriesAdapter.getSelectedCategories();
//            sharedPreferencesHelper.saveSelectedCategories(selectedCategories);
//
//            int notificationTime = Integer.parseInt(notificationTimeEditText.getText().toString());
//            String notificationUnit = notificationUnitSpinner.getSelectedItem().toString();
//            sharedPreferencesHelper.saveNotificationTime(notificationTime, notificationUnit);
//
//            applySettings();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}