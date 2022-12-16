package com.example.libraryassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class UpdateBookActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle("Book Detail");
        setSupportActionBar(toolbar);
    }
}