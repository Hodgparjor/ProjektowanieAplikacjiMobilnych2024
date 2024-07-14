package com.example.todo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Task {
    int id;
    String title;
    String category;
    String description;
    long creation;
    long deadline;
    boolean isCompleted;
    boolean isNotificationEnabled;
    private List<String> attachments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }


    public Task(int id, String title, String description, long creation, long deadline, boolean isCompleted, boolean isNotificationEnabled, String category, List<String> attachments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creation = creation;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.isNotificationEnabled = isNotificationEnabled;
        this.category = category;
        List<String> nullClearedAttachments = new ArrayList<>();
        if(attachments != null) {
            for (String attachment : attachments) {
                if(attachment != null && !attachment.isEmpty()) {
                    nullClearedAttachments.add(attachment);
                }
            }
        }
        this.attachments = nullClearedAttachments;
    }

}
