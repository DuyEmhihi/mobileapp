package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 1;

    public static class User {
        public long id;
        public String fullname;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "password TEXT NOT NULL, "
                + "fullname TEXT NOT NULL)");
        db.execSQL("CREATE TABLE tasks ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "title TEXT NOT NULL, "
                + "due_time INTEGER NOT NULL, "
                + "note TEXT, "
                + "done INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Mã hóa mật khẩu bằng SHA-256 để không lưu mật khẩu dạng chữ thường
    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }

    // ---------- Tài khoản ----------

    /** Trả về true nếu đăng ký thành công, false nếu tên đăng nhập đã tồn tại. */
    public boolean register(String username, String password, String fullname) {
        ContentValues v = new ContentValues();
        v.put("username", username);
        v.put("password", hash(password));
        v.put("fullname", fullname);
        return getWritableDatabase().insert("users", null, v) != -1;
    }

    /** Trả về User nếu đúng tài khoản, null nếu sai. */
    public User login(String username, String password) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, fullname FROM users WHERE username = ? AND password = ?",
                new String[]{username, hash(password)});
        User user = null;
        if (c.moveToFirst()) {
            user = new User();
            user.id = c.getLong(0);
            user.fullname = c.getString(1);
        }
        c.close();
        return user;
    }

    // ---------- Công việc ----------

    public long addTask(long userId, String title, long dueTime, String note) {
        ContentValues v = new ContentValues();
        v.put("user_id", userId);
        v.put("title", title);
        v.put("due_time", dueTime);
        v.put("note", note);
        v.put("done", 0);
        return getWritableDatabase().insert("tasks", null, v);
    }

    public List<Task> getTasks(long userId) {
        List<Task> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id, title, due_time, note, done FROM tasks "
                        + "WHERE user_id = ? ORDER BY done ASC, due_time ASC",
                new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            list.add(new Task(
                    c.getLong(0),
                    c.getString(1),
                    c.getLong(2),
                    c.getString(3),
                    c.getInt(4) == 1));
        }
        c.close();
        return list;
    }

    public void setDone(long taskId, boolean done) {
        ContentValues v = new ContentValues();
        v.put("done", done ? 1 : 0);
        getWritableDatabase().update("tasks", v, "id = ?", new String[]{String.valueOf(taskId)});
    }

    public void deleteTask(long taskId) {
        getWritableDatabase().delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
    }
}
