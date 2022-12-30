package com.example.libraryassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.libraryassistant.adapter.BookAdapter;
import com.example.libraryassistant.apiclient.ApiClient;
import com.example.libraryassistant.apiclient.Book;
import com.example.libraryassistant.apiclient.BookInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FloatingActionButton fabAdd;
    BookAdapter adapter;
    RecyclerView recBook;

    BookInterface bookInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        fabAdd = findViewById(R.id.fab_add);

        bookInterface = ApiClient.getClient().create(BookInterface.class);
        recBook = findViewById(R.id.rec_book);
        recBook.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View menuItem = findViewById(R.id.action_logout);

                menuItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
            }
        });
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllBooks();
    }

    private void getAllBooks() {
        Call<List<Book>> allBooks = bookInterface.getBook();
        allBooks.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.i("Test", response.raw().toString());
                ArrayList<Book> listBook = (ArrayList<Book>) response.body();
                adapter = new BookAdapter(listBook);
                recBook.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("response_error", t.getMessage());
            }
        });

        ArrayList<Book> listBook = new ArrayList<>();
        adapter = new BookAdapter(listBook);
        recBook.setAdapter(adapter);
    }
}