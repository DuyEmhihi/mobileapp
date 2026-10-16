package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE = "expense";

    private EditText etName, etAmount, etNote;
    private Spinner spCategory;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etName = findViewById(R.id.etName);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spCategory = findViewById(R.id.spCategory);
        datePicker = findViewById(R.id.datePicker);
        Button btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
            }
        });
    }

    private void saveExpense() {
        String name = etName.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Tên khoản chi không được để trống");
            etName.requestFocus();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
        } catch (NumberFormatException ex) {
            etAmount.setError("Số tiền không hợp lệ");
            etAmount.requestFocus();
            return;
        }
        if (amount <= 0) {
            etAmount.setError("Số tiền phải lớn hơn 0");
            etAmount.requestFocus();
            return;
        }

        String category = spCategory.getSelectedItem().toString();
        String date = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
        String note = etNote.getText().toString().trim();

        Expense expense = new Expense(name, amount, category, date, note);

        Intent result = new Intent();
        result.putExtra(EXTRA_EXPENSE, expense);
        setResult(RESULT_OK, result);
        finish();
    }
}
