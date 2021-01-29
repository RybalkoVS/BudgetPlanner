package by.grsu.budgetplanner.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.model.entities.Transaction;

public class TransactionsListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater lInflater;
    List<Transaction> objects;

    public TransactionsListAdapter(Context context, List<Transaction> transactions) {
        mContext = context;
        objects = transactions;
        lInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Transaction getTransaction(int position)
    {
        return (Transaction) getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.transactions_list_adapter, parent, false);
        }
        Transaction transaction = getTransaction(position);
        ((TextView)view.findViewById(R.id.transactinDate)).setText(transaction.getDate());
        ((TextView)view.findViewById(R.id.transactionCategoryName)).setText(transaction.getCategoryName());
        switch(transaction.getType())
        {
            case EARN:
                ((TextView)view.findViewById(R.id.sum)).setText("+" + transaction.getSum());
                ((TextView)view.findViewById(R.id.sum)).setTextColor(Color.GREEN);
                break;
            case EXPENSE:
                ((TextView)view.findViewById(R.id.sum)).setText("-" + transaction.getSum());
                ((TextView)view.findViewById(R.id.sum)).setTextColor(Color.RED);
                break;
        }
        return view;
    }
}
