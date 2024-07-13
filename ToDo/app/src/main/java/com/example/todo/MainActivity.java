package com.example.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.Adapters.CategoryAdapter;
import com.example.todo.Adapters.TaskAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private DbHelper db;
    private SharedPreferencesManager preferencesManager;
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
        Init();
        applySettings();
    }

    private void Init() {
        db = new DbHelper(this);
        preferencesManager = new SharedPreferencesManager(this);
        initializeTasksRecyclerView();
        initializeButtons();
        initializeFiltering();
    }

    private void initializeButtons() {
        findViewById(R.id.settingsBtn).setOnClickListener(v -> { showSettingsDialog(); });
        findViewById(R.id.sortBtn).setOnClickListener(v -> { changeSorting(); });
        findViewById(R.id.addTaskBtn).setOnClickListener(v -> { showAddTaskDialog(); });
    }

    private void initializeTasksRecyclerView() {
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        taskAdapter = new TaskAdapter(this, db.getAllTasks(), this::showEditTaskDialog);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void initializeFiltering() {
        ((android.widget.SearchView)findViewById(R.id.searchView)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.filter(newText);
                return false;
            }
        });

    }

    private void changeSorting() {
        ImageButton sortButton = findViewById(R.id.sortBtn);
        isSortDescending = !isSortDescending;

        taskAdapter.sort(isSortDescending);
        if(isSortDescending) {
            sortButton.setImageResource(R.drawable.sort_descending);
        } else {
            sortButton.setImageResource(R.drawable.sort_ascending);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_task, null);
        builder.setView(view);

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
            refreshTaskList();
//            scheduleNotification(task);

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

    private void refreshTaskList() {
        taskAdapter.updateTasksList(db.getAllTasks());
        taskAdapter.sort(isSortDescending);
        applySettings();
    }

    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_task, null);
        builder.setView(view);

        EditText titleEditText = view.findViewById(R.id.title);
        EditText descriptionEditText = view.findViewById(R.id.description);
        EditText categoryEditText = view.findViewById(R.id.category);
        CheckBox enableNotificationCheckBox = view.findViewById(R.id.enableNotificationCheckBox);
        Button selectDateTimeBtn = view.findViewById(R.id.selectDateTimeBtn);
        TextView selectedDateTimeText = view.findViewById(R.id.selectedDateTimeText);
        RecyclerView attachmentsRecyclerView = view.findViewById(R.id.attachmentsRecyclerView);
        Button addAttachmentButton = view.findViewById(R.id.addAttachmentBtn);

        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        categoryEditText.setText(task.getCategory());
        enableNotificationCheckBox.setChecked(task.isNotificationEnabled());
        selectedDeadline = task.getDeadline();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDeadline = sdf.format(selectedDeadline);
        selectedDateTimeText.setText(formattedDeadline);
        selectDateTimeBtn.setOnClickListener(v -> showDateTimePicker(selectedDateTimeText));
//        addAttachmentButton.setOnClickListener(v -> pickAttachment());
//
//        //currentAttachments list with task attachments
//        currentAttachments = new ArrayList<>(task.getAttachments());
//
//        //RecyclerView for attachments
//        attachmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        attachmentsAdapter = new AttachmentsAdapter(currentAttachments, this);
//        attachmentsRecyclerView.setAdapter(attachmentsAdapter);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String category = categoryEditText.getText().toString();
            boolean isNotificationEnabled = enableNotificationCheckBox.isChecked();

            if (title.isEmpty() || category.isEmpty() || selectedDeadline == 0) {
                return;
            }

            task.setTitle(title);
            task.setDescription(description);
            task.setCategory(category);
            task.setNotificationEnabled(isNotificationEnabled);
            task.setDeadline(selectedDeadline);
            //task.setAttachments(new ArrayList<>(currentAttachments));

            db.updateTask(task);
            refreshTaskList();
//            cancelNotification(task);
//            scheduleNotification(task);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

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

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(newDate.getTime());
                dueDateTextView.setText(formattedDate);
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

        showCompletedTasksCheckBox.setChecked(preferencesManager.getShowCompleted());
        notificationTimeEditText.setText(String.valueOf(preferencesManager.getNotificationTime()));

        List<String> categories = db.getCategories();
        CategoryAdapter categoriesAdapter = new CategoryAdapter(this, categories, preferencesManager.getSelectedCategories());
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        builder.setPositiveButton("Save", (dialog, which) -> {
            preferencesManager.putShowCompleted(showCompletedTasksCheckBox.isChecked());

            Set<String> selectedCategories = categoriesAdapter.getSelectedCategories();
            preferencesManager.putSelectedCategories(selectedCategories);

            int notificationTime = Integer.parseInt(notificationTimeEditText.getText().toString());
            preferencesManager.putNotificationTime(notificationTime);

            applySettings();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void applySettings() {
        boolean showCompleted = preferencesManager.getShowCompleted();
        Set<String> selectedCategories = preferencesManager.getSelectedCategories();

        List<Task> taskList = db.getAllTasks();
        if (!showCompleted) {
            taskList = filterCompletedTasks(taskList);
        }
        if (!selectedCategories.isEmpty()) {
            Log.i("ApplySettings","Amount of categories: " + selectedCategories.toString());
            taskList = filterTasksByCategory(taskList, selectedCategories);
        }

        List<Task> finalTaskList = taskList;
        new Handler(Looper.getMainLooper()).post(() -> {
            taskAdapter.updateTasksList(finalTaskList);
            taskAdapter.sort(isSortDescending);
        });
    }

    private List<Task> filterCompletedTasks(List<Task> taskList) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : taskList) {
            if (!task.isCompleted()) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    private List<Task> filterTasksByCategory(List<Task> taskList, Set<String> categories) {
        List<Task> filteredList = new ArrayList<>();
        for (Task task : taskList) {
            if (categories.contains(task.getCategory())) {
                filteredList.add(task);
            }
        }
        return filteredList;
    }

}