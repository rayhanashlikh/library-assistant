package com.example.libraryassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddBookActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle("Tambah Buku");

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(AddBookActivity.this, LoginActivity.class));
        }
    }

    void createBook() {

    }
}