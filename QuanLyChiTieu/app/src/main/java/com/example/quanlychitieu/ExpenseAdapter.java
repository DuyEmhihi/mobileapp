package com.example.quanlychitieu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Expense> list;

    public ExpenseAdapter(Context context, ArrayList<Expense> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }

    @Override
    public Object getItem(int position) { return list.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        }
        Expense e = list.get(position);
        TextView tvName = convertView.findViewById(R.id.tvItemName);
        TextView tvCatDate = convertView.findViewById(R.id.tvItemCategoryDate);
        TextView tvAmount = convertView.findViewById(R.id.tvItemAmount);

        tvName.setText(e.getName());
        tvCatDate.setText(e.getCategory() + " • " + e.getDate());
        tvAmount.setText(Expense.formatMoney(e.getAmount()));
        return convertView;
    }
}
