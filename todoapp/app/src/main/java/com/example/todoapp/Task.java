package com.example.todoapp;

public class Task {
    public long id;
    public String title;
    public long dueTime;   // thời điểm hạn, tính bằng mili giây
    public String note;
    public boolean done;

    public Task(long id, String title, long dueTime, String note, boolean done) {
        this.id = id;
        this.title = title;
        this.dueTime = dueTime;
        this.note = note;
        this.done = done;
    }
}
