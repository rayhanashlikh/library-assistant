package com.example.libraryassistant;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.libraryassistant.apiclient.ApiClient;
import com.example.libraryassistant.apiclient.Book;
import com.example.libraryassistant.apiclient.BookInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateBookActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    final Calendar myCalendar = Calendar.getInstance();
    private EditText edtTitle, edtAuthor, edtIsbn, edtPublisher, edtPublishedAt, edtDescription, edtImage;
    private Button btnDelete, btnSave, btnClose;
    private ImageView imgPrev;

    private Book book;
    private BookInterface bookInterface;
    private MultipartBody.Part image;
    public static final String URL_ORIGIN = "https://7ab0-66-96-233-161.ap.ngrok.io/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("Book Detail");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        Intent it = getIntent();
        book = (Book) it.getSerializableExtra("current_book");
        bookInterface = ApiClient.getClient().create(BookInterface.class);

        edtTitle = findViewById(R.id.edt_title);
        edtAuthor = findViewById(R.id.edt_author);
        edtIsbn = findViewById(R.id.edt_isbn);
        edtPublisher = findViewById(R.id.edt_publisher);
        edtPublishedAt = findViewById(R.id.edt_published_at);
        edtDescription = findViewById(R.id.edt_description);
        edtImage = findViewById(R.id.edt_image);

        btnDelete = findViewById(R.id.btn_delete);
        btnSave = findViewById(R.id.btn_save);
        btnClose = findViewById(R.id.btn_close);
        imgPrev = findViewById(R.id.img_preview);

        edtTitle.setText(book.getTitle());
        edtAuthor.setText(book.getAuthor());
        edtIsbn.setText(book.getIsbn());
        edtPublisher.setText(book.getPublisher());
        edtDescription.setText(book.getDescription());

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat);
        edtPublishedAt.setText(sdf.format(book.getPublished_at()));

        String sub_url = book.getImage().substring(22, 62);
        Log.i("test: ", sub_url);
        String url = URL_ORIGIN + sub_url;

        Picasso.get()
                .load(url)
                .into(imgPrev);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        edtPublishedAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UpdateBookActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBook();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBook();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UpdateBookActivity.this, LoginActivity.class));
        }
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(myFormat);
        edtPublishedAt.setText(sdf.format(myCalendar.getTime()));
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    String filePath = getRealPathFromURIPath(uri);
                    String name = uri.getLastPathSegment();
                    name = name.substring(name.lastIndexOf(File.separator) + 1);
                    edtImage.setText(name);
                    imgPrev.setImageURI(uri);

                    File file = new File(filePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg,png"), file);
                    image = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                }
            });

    private String getRealPathFromURIPath(Uri contentURI) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void updateBook() {
        String title = ((EditText) findViewById(R.id.edt_title)).getText().toString();
        String author = ((EditText) findViewById(R.id.edt_author)).getText().toString();
        String isbn = ((EditText) findViewById(R.id.edt_isbn)).getText().toString();
        String publisher = ((EditText) findViewById(R.id.edt_publisher)).getText().toString();
        String publishedAt = ((EditText) findViewById(R.id.edt_published_at)).getText().toString();
//        String image = ((EditText) findViewById(R.id.edt_image)).getText().toString();
        String description = ((EditText) findViewById(R.id.edt_description)).getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date publishedDate = format.parse(publishedAt, new java.text.ParsePosition(0));

        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setPublished_at(publishedDate);
        book.setDescription(description);

        RequestBody authorBody = RequestBody.create(MediaType.parse("text/plain"), author);
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody isbnBody = RequestBody.create(MediaType.parse("text/plain"), isbn);
        RequestBody publisherBody = RequestBody.create(MediaType.parse("text/plain"), publisher);
        RequestBody publishedAtBody = RequestBody.create(MediaType.parse("text/plain"), publishedAt);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        if (image == null) {
            Call<Book> call = bookInterface.updateBook(book.getId(), title, author, isbn, publisher, publishedAt, description);
            call.enqueue(new Callback<Book>() {
                @Override
                public void onResponse(Call<Book> call, Response<Book> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateBookActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateBookActivity.this, "Book updated failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Book> call, Throwable t) {
                    Toast.makeText(UpdateBookActivity.this, "Book updated error", Toast.LENGTH_SHORT).show();
                    Log.e("Error: ", t.getMessage());
                }
            });
        } else {
            Call<Book> call = bookInterface.updateBookWithImage(book.getId(),
                    titleBody, authorBody, isbnBody, publisherBody, publishedAtBody, descriptionBody, image);
            call.enqueue(new Callback<Book>() {
                @Override
                public void onResponse(Call<Book> call, Response<Book> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateBookActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateBookActivity.this, "Book update failed", Toast.LENGTH_SHORT).show();
                        Log.e("test", "onResponse: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Book> call, Throwable t) {
                    Toast.makeText(UpdateBookActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Error: ", t.getMessage());
                }
            });
        }
    }

    private void deleteBook() {
        bookInterface.deleteBook(book.getId()).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    Log.d("delete_response", response.raw().toString());
                    Toast.makeText(UpdateBookActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.d("delete_response", t.getMessage());
            }
        });
    }

    private void removeBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus Data Buku");
        builder.setMessage("Apakah anda benar-benar ingin menghapus data buku ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });
        builder.setNegativeButton("Batalkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void saveBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Update Data Buku");
        builder.setMessage("Apakah data yang diisi sudah benar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateBook();
            }
        });
        builder.setNegativeButton("Batalkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}