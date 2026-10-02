package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.Listener {

    private DBHelper db;
    private long userId;
    private TaskAdapter adapter;
    private RecyclerView rvTasks;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("session", MODE_PRIVATE);
        userId = sp.getLong("user_id", -1);
        if (userId == -1) { // chưa đăng nhập thì quay về màn đăng nhập
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        // Header: avatar + tên người dùng
        String fullname = sp.getString("fullname", "");
        ((TextView) findViewById(R.id.tvName)).setText(fullname);
        ((TextView) findViewById(R.id.tvAvatar)).setText(initialOf(fullname));

        tvEmpty = findViewById(R.id.tvEmpty);
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this);
        rvTasks.setAdapter(adapter);

        // Nút + : sang màn thêm mới
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddTaskActivity.class)));

        // Đăng xuất
        findViewById(R.id.tvLogout).setOnClickListener(v -> {
            getSharedPreferences("session", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (db != null) {
            loadTasks(); // quay lại từ màn thêm mới thì tải lại danh sách
        }
    }

    private void loadTasks() {
        List<Task> all = db.getTasks(userId);

        // Mốc 23:59:59 hôm nay
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endOfToday = c.getTimeInMillis();

        List<Task> today = new ArrayList<>();
        List<Task> upcoming = new ArrayList<>();
        for (Task t : all) {
            if (t.dueTime <= endOfToday) {
                today.add(t);
            } else {
                upcoming.add(t);
            }
        }

        List<Object> items = new ArrayList<>();
        if (!today.isEmpty()) {
            items.add("Công việc hôm nay");
            items.addAll(today);
        }
        if (!upcoming.isEmpty()) {
            items.add("Công việc sắp tới");
            items.addAll(upcoming);
        }

        adapter.setItems(items);
        tvEmpty.setVisibility(items.isEmpty() ? TextView.VISIBLE : TextView.GONE);
    }

    private String initialOf(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) return "?";
        String[] parts = fullname.trim().split("\\s+");
        String last = parts[parts.length - 1]; // tên riêng (từ cuối)
        return last.substring(0, 1).toUpperCase();
    }

    // ---------- TaskAdapter.Listener ----------

    @Override
    public void onChecked(Task task, boolean checked) {
        db.setDone(task.id, checked);
        // đợi RecyclerView xử lý xong click rồi mới sắp xếp lại
        rvTasks.post(this::loadTasks);
    }

    @Override
    public void onLongClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa công việc")
                .setMessage("Bạn có chắc muốn xóa \"" + task.title + "\"?")
                .setPositiveButton("Xóa", (d, w) -> {
                    db.deleteTask(task.id);
                    loadTasks();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
