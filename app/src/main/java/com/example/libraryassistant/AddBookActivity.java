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

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    final Calendar myCalendar = Calendar.getInstance();
    private EditText edtTitle, edtAuthor, edtIsbn, edtPublisher, edtPublishedAt, edtDescription, edtImage;
    private Button btnSave, btnClose;
    private ImageView imgPrev;
    private MultipartBody.Part image;
    private BookInterface bookInterface;
    private boolean isAllFieldValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("Tambah Buku");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        bookInterface = ApiClient.getClient().create(BookInterface.class);

        edtTitle = findViewById(R.id.edt_title);
        edtAuthor = findViewById(R.id.edt_author);
        edtIsbn = findViewById(R.id.edt_isbn);
        edtPublisher = findViewById(R.id.edt_publisher);
        edtPublishedAt = findViewById(R.id.edt_published_at);
        edtDescription = findViewById(R.id.edt_description);
        edtImage = findViewById(R.id.edt_image);

        edtPublishedAt = findViewById(R.id.edt_published_at);
        edtImage = findViewById(R.id.edt_image);
        btnSave = findViewById(R.id.btn_save);
        btnClose = findViewById(R.id.btn_close);
        imgPrev = findViewById(R.id.img_preview);

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
                new DatePickerDialog(AddBookActivity.this, date, myCalendar
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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddBookActivity.this);
                builder.setTitle("Konfirmasi Tambah Data Buku");
                builder.setMessage("Apakah data yang diisi sudah benar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        // Save disini
                        createBook();

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
            startActivity(new Intent(AddBookActivity.this, LoginActivity.class));
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
                    Log.d("File: ", file.getName());
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
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

    private boolean checkAllFields(){
        if (edtTitle.getText().toString().isEmpty()){
            edtTitle.setError("Judul tidak boleh kosong");
            return false;
        }

        if (edtAuthor.getText().toString().isEmpty()){
            edtAuthor.setError("Penulis tidak boleh kosong");
            return false;
        }

        if (edtIsbn.getText().toString().isEmpty()){
            edtIsbn.setError("ISBN tidak boleh kosong");
            return false;
        }

        if (edtPublisher.getText().toString().isEmpty()){
            edtPublisher.setError("Penerbit tidak boleh kosong");
            return false;
        }

        if (edtPublishedAt.getText().toString().isEmpty()){
            edtPublishedAt.setError("Tanggal terbit tidak boleh kosong");
            return false;
        }

        if (edtDescription.getText().toString().isEmpty()){
            edtDescription.setError("Deskripsi tidak boleh kosong");
            return false;
        }

        if (edtImage.getText().toString().isEmpty()){
            edtImage.setError("Gambar tidak boleh kosong");
            return false;
        }

        return true;
    }

    private void createBook() {
        String title = ((EditText) findViewById(R.id.edt_title)).getText().toString();
        String author = ((EditText) findViewById(R.id.edt_author)).getText().toString();
        String isbn = ((EditText) findViewById(R.id.edt_isbn)).getText().toString();
        String publisher = ((EditText) findViewById(R.id.edt_publisher)).getText().toString();
        String publishedAt = ((EditText) findViewById(R.id.edt_published_at)).getText().toString();
        String description = ((EditText) findViewById(R.id.edt_description)).getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date publishedDate = format.parse(publishedAt, new ParsePosition(0));

        Book book = new Book(1, null, null, null, null, null, null, null);
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setPublished_at(publishedDate);
        book.setDescription(description);
//        book.setImage(image);

        RequestBody authorBody = RequestBody.create(MediaType.parse("text/plain"), author);
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody isbnBody = RequestBody.create(MediaType.parse("text/plain"), isbn);
        RequestBody publisherBody = RequestBody.create(MediaType.parse("text/plain"), publisher);
        RequestBody publishedAtBody = RequestBody.create(MediaType.parse("text/plain"), publishedAt);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        isAllFieldValid = checkAllFields();

        if (isAllFieldValid) {
            Call<Book> call = bookInterface.postBook(titleBody, authorBody, isbnBody, publisherBody, publishedAtBody, descriptionBody, image);
            call.enqueue(new Callback<Book>() {
                @Override
                public void onResponse(Call<Book> call, Response<Book> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddBookActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddBookActivity.this, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
                        Log.e("Error", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Book> call, Throwable t) {
                    Toast.makeText(AddBookActivity.this, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                }
            });
        }
    }
}