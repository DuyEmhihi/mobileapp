package com.example.todoapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtFullname, edtUsername, edtPassword, edtConfirm;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBHelper(this);

        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);

        findViewById(R.id.btnRegister).setOnClickListener(v -> doRegister());
        findViewById(R.id.tvBackLogin).setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String fullname = edtFullname.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirm = edtConfirm.getText().toString();

        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 4) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 4 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.register(username, password, fullname)) {
            Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập", Toast.LENGTH_SHORT).show();
            finish(); // quay lại màn đăng nhập
        } else {
            Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
        }
    }
}
