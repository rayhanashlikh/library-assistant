package com.example.libraryassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    TextView txtLogin;
    EditText edtEmail, edtPassword, edtConfPassword;
    Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtLogin = findViewById(R.id.txt_login);
        btnRegister = findViewById(R.id.btn_register);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfPassword = findViewById(R.id.edt_konfirmasi_password);

        mAuth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    private void createUser() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String conf_password = edtConfPassword.getText().toString();

//        if (password != conf_password) {
//            edtConfPassword.setError("Konfirmasi Password harus sesuai");
//            edtConfPassword.requestFocus();
//        } else {
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError("Email tidak boleh kosong");
                edtEmail.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Password tidak boleh kosong");
                edtPassword.requestFocus();
            } else if (TextUtils.isEmpty(conf_password)) {
                edtConfPassword.setError("Konfirmasi password tidak boleh kosong");
                edtConfPassword.requestFocus();
            } else if (!password.equals(conf_password)) {
                edtConfPassword.setError("Konfirmasi Password harus sesuai");
                edtPassword.requestFocus();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User berhasil melakukan pendaftaran akun", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Pendaftaran akun error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//            }
        }
    }
}