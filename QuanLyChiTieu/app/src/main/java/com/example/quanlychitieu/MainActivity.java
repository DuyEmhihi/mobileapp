package com.example.quanlychitieu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Expense> expenses = new ArrayList<>();
    private ExpenseAdapter adapter;
    private TextView tvTotal;

    // Nhận kết quả từ AddExpenseActivity
    private final ActivityResultLauncher<Intent> addLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    @SuppressWarnings("deprecation")
                    Expense e = (Expense) result.getData().getSerializableExtra(AddExpenseActivity.EXTRA_EXPENSE);
                    if (e != null) {
                        expenses.add(e);
                        adapter.notifyDataSetChanged();
                        updateTotal();
                    }
                }
            });

    // Nhận kết quả từ ExpenseDetailActivity (khi bấm Xóa)
    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int pos = result.getData().getIntExtra(ExpenseDetailActivity.EXTRA_POSITION, -1);
                    if (pos >= 0 && pos < expenses.size()) {
                        expenses.remove(pos);
                        adapter.notifyDataSetChanged();
                        updateTotal();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotal = findViewById(R.id.tvTotal);
        ListView lvExpenses = findViewById(R.id.lvExpenses);
        Button btnAdd = findViewById(R.id.btnAdd);

        // Dữ liệu mẫu
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        expenses.add(new Expense("Ăn sáng", 30000, "Ăn uống", today, ""));
        expenses.add(new Expense("Đổ xăng", 50000, "Đi lại", today, ""));
        expenses.add(new Expense("Mua sách", 120000, "Học tập", today, ""));

        adapter = new ExpenseAdapter(this, expenses);
        lvExpenses.setAdapter(adapter);
        updateTotal();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLauncher.launch(new Intent(MainActivity.this, AddExpenseActivity.class));
            }
        });

        lvExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
                intent.putExtra(ExpenseDetailActivity.EXTRA_EXPENSE, expenses.get(position));
                intent.putExtra(ExpenseDetailActivity.EXTRA_POSITION, position);
                detailLauncher.launch(intent);
            }
        });
    }

    private void updateTotal() {
        long total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        tvTotal.setText("Tổng đã chi: " + Expense.formatMoney(total));
    }
}
