package com.example.todoapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onChecked(Task task, boolean checked);
        void onLongClick(Task task);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TASK = 1;

    // Mỗi phần tử là String (tiêu đề mục) hoặc Task (công việc)
    private final List<Object> items = new ArrayList<>();
    private final Listener listener;

    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateTimeFmt = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public TaskAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_TASK;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(inflater.inflate(R.layout.item_header, parent, false));
        }
        return new TaskHolder(inflater.inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).tvHeader.setText((String) items.get(position));
            return;
        }

        final Task task = (Task) items.get(position);
        TaskHolder h = (TaskHolder) holder;

        h.tvTitle.setText(task.title);

        if (task.note == null || task.note.trim().isEmpty()) {
            h.tvNote.setVisibility(View.GONE);
        } else {
            h.tvNote.setVisibility(View.VISIBLE);
            h.tvNote.setText(task.note);
        }

        // Hôm nay chỉ hiện giờ, ngày khác hiện cả ngày/tháng
        h.tvTime.setText(isToday(task.dueTime)
                ? timeFmt.format(task.dueTime)
                : dateTimeFmt.format(task.dueTime));

        // Quá hạn mà chưa xong thì tô đỏ
        boolean overdue = !task.done && task.dueTime < System.currentTimeMillis();
        h.tvTime.setTextColor(Color.parseColor(overdue ? "#D32F2F" : "#757575"));

        // Gạch ngang tên khi đã hoàn thành
        if (task.done) {
            h.tvTitle.setPaintFlags(h.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.tvTitle.setPaintFlags(h.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Gỡ listener cũ trước khi setChecked để tránh gọi nhầm khi view được tái sử dụng
        h.cbDone.setOnCheckedChangeListener(null);
        h.cbDone.setChecked(task.done);
        h.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.done = isChecked;
            listener.onChecked(task, isChecked);
        });

        h.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(task);
            return true;
        });
    }

    private boolean isToday(long millis) {
        Calendar now = Calendar.getInstance();
        Calendar t = Calendar.getInstance();
        t.setTimeInMillis(millis);
        return now.get(Calendar.YEAR) == t.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == t.get(Calendar.DAY_OF_YEAR);
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderHolder(View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tvHeader);
        }
    }

    static class TaskHolder extends RecyclerView.ViewHolder {
        CheckBox cbDone;
        TextView tvTitle, tvNote, tvTime;

        TaskHolder(View v) {
            super(v);
            cbDone = v.findViewById(R.id.cbDone);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvNote = v.findViewById(R.id.tvNote);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }
}
