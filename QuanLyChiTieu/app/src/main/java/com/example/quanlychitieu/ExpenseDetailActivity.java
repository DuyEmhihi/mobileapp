package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {
    public static final String EXTRA_EXPENSE = "expense";
    public static final String EXTRA_POSITION = "position";

    private int position = -1;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvNote = findViewById(R.id.tvNote);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        Expense e = (Expense) getIntent().getSerializableExtra(EXTRA_EXPENSE);
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);

        if (e != null) {
            tvName.setText(e.getName());
            tvAmount.setText(Expense.formatMoney(e.getAmount()));
            tvCategory.setText("Danh mục: " + e.getCategory());
            tvDate.setText("Ngày chi tiêu: " + e.getDate());
            String note = e.getNote().isEmpty() ? "(không có)" : e.getNote();
            tvNote.setText("Ghi chú: " + note);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(EXTRA_POSITION, position);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
}
