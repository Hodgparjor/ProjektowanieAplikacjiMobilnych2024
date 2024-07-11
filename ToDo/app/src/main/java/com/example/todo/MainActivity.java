package com.example.todo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

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

        initializeButtons();
    }

    private void initializeButtons() {
        findViewById(R.id.settingsBtn).setOnClickListener(v -> { showSettingsDialog(); });
        findViewById(R.id.sortBtn).setOnClickListener(v -> { changeSorting(); });
        findViewById(R.id.addTaskBtn).setOnClickListener(v -> { showAddTaskDialog(); });

    }

    private void changeSorting() {

    }

    private void showAddTaskDialog() {

    }

    private void showSettingsDialog() {

    }
}