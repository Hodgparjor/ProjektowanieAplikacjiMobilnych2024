package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_TASKS = "tasks";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String CREATION = "creation";
    private static final String DEADLINE = "deadline";
    private static final String IS_COMPLETED = "is_completed";
    private static final String IS_NOTIFICATION_ENABLED = "is_notification_enabled";
    private static final String CATEGORY = "category";
    private static final String ATTACHMENTS = "attachments";
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTasksTable = "CREATE TABLE " + TABLE_TASKS + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + CREATION + " INTEGER,"
                + DEADLINE + " INTEGER,"
                + IS_COMPLETED + " INTEGER,"
                + IS_NOTIFICATION_ENABLED + " INTEGER,"
                + CATEGORY + " TEXT,"
                + ATTACHMENTS + " TEXT"
                + ")";
        db.execSQL(CreateTasksTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public void createTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, task.getTitle());
        values.put(DESCRIPTION, task.getDescription());
        values.put(CREATION, task.getCreation());
        values.put(DEADLINE, task.getDeadline());
        values.put(IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(IS_NOTIFICATION_ENABLED, task.isNotificationEnabled() ? 1 : 0);
        values.put(CATEGORY, task.getCategory());
        values.put(ATTACHMENTS, String.join(",", task.getAttachments()));

        long id = db.insert(TABLE_TASKS, null, values);
        task.setId((int) id);
        db.close();
    }

    public Task readTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, new String[]{ID, TITLE, DESCRIPTION, CREATION, DEADLINE, IS_COMPLETED, IS_NOTIFICATION_ENABLED, CATEGORY, ATTACHMENTS},
                ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Task task = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(CREATION)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(DEADLINE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(IS_NOTIFICATION_ENABLED)) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY)),
                    new ArrayList<>(List.of(cursor.getString(cursor.getColumnIndexOrThrow(ATTACHMENTS)).split(",")))
            );
            cursor.close();
            return task;
        } else {
            cursor.close();
            return null;
        }
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, task.getTitle());
        values.put(DESCRIPTION, task.getDescription());
        values.put(CREATION, task.getCreation());
        values.put(DEADLINE, task.getDeadline());
        values.put(IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(IS_NOTIFICATION_ENABLED, task.isNotificationEnabled() ? 1 : 0);
        values.put(CATEGORY, task.getCategory());
        values.put(ATTACHMENTS, String.join(",", task.getAttachments()));
        int result = db.update(TABLE_TASKS, values, ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        Log.d("Database", "updated task. Title: " + task.getTitle() + " Completed: " + task.isCompleted);

        return result;
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + CATEGORY + " FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(CREATION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DEADLINE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(IS_COMPLETED)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(IS_NOTIFICATION_ENABLED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY)),
                        new ArrayList<>(List.of(cursor.getString(cursor.getColumnIndexOrThrow(ATTACHMENTS)).split(",")))
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskList;
    }
}
