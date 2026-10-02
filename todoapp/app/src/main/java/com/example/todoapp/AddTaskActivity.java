package com.example.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText edtTitle, edtDue, edtNote;
    private DBHelper db;
    private long userId;

    private final Calendar due = Calendar.getInstance();
    private boolean dueSelected = false;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = new DBHelper(this);
        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        userId = sp.getLong("user_id", -1);

        edtTitle = findViewById(R.id.edtTitle);
        edtDue = findViewById(R.id.edtDue);
        edtNote = findViewById(R.id.edtNote);

        edtDue.setOnClickListener(v -> pickDateTime());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    // Chọn ngày xong thì chọn tiếp giờ
    private void pickDateTime() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            due.set(Calendar.YEAR, year);
            due.set(Calendar.MONTH, month);
            due.set(Calendar.DAY_OF_MONTH, day);

            new TimePickerDialog(this, (tpView, hour, minute) -> {
                due.set(Calendar.HOUR_OF_DAY, hour);
                due.set(Calendar.MINUTE, minute);
                due.set(Calendar.SECOND, 0);
                due.set(Calendar.MILLISECOND, 0);
                dueSelected = true;
                edtDue.setText(fmt.format(due.getTime()));
            }, due.get(Calendar.HOUR_OF_DAY), due.get(Calendar.MINUTE), true).show();

        }, due.get(Calendar.YEAR), due.get(Calendar.MONTH), due.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void save() {
        String title = edtTitle.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dueSelected) {
            Toast.makeText(this, "Vui lòng chọn hạn hoàn thành", Toast.LENGTH_SHORT).show();
            return;
        }

        db.addTask(userId, title, due.getTimeInMillis(), note);
        Toast.makeText(this, "Đã thêm công việc", Toast.LENGTH_SHORT).show();
        finish(); // MainActivity sẽ tự tải lại danh sách ở onResume
    }
}
