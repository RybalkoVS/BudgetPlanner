package by.grsu.budgetplanner.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.activities.MainActivity;
import by.grsu.budgetplanner.adapters.TransactionsListAdapter;
import by.grsu.budgetplanner.model.database.App;
import by.grsu.budgetplanner.model.database.Database;
import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.entities.Wallet;

public class TransactionsListFragment extends Fragment implements MainActivity.FragmentLifeCycle {

    ListView lvTransactionsList;
    Database db;
    TransactionsListAdapter adapter;
    List<Transaction> transactions;
    GetTransactionItems getTransactions;
    Transaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_transactions_list, container, false);
        db = App.getInstance().getDatabase();
        lvTransactionsList = layout.findViewById(R.id.transactionsList);
        lvTransactionsList.setOnItemLongClickListener((parent, view, position, id) -> {
            transaction = (Transaction) parent.getAdapter().getItem(position);
            return false;
        });
        lvTransactionsList.setEnabled(true);
        registerForContextMenu(lvTransactionsList);
        getTransactions = new GetTransactionItems();
        getTransactions.execute();
        return layout;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.transactions_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_transaction_item:
                DeleteTransactionItem delete = new DeleteTransactionItem();
                delete.execute();
                getTransactions = new GetTransactionItems();
                getTransactions.execute();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResumeFragment() {
    }

    public class GetTransactionItems extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            transactions = new ArrayList<>();
            transactions = db.transactionDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(transactions.size() < 1)
            {
                FrameLayout layout = getActivity().findViewById(R.id.transaction_layout);
                ListView view = getActivity().findViewById(R.id.transactionsList);
                layout.removeView(view);
                TextView notifyText = new TextView(getContext());
                notifyText.setText(R.string.no_transactions_notify);
                layout.addView(notifyText);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                notifyText.setLayoutParams(params);
            }
            else {
                adapter = new TransactionsListAdapter(getContext(), transactions);
                lvTransactionsList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            super.onPostExecute(result);
        }
    }
    public class DeleteTransactionItem extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            App.getInstance().getDatabase().transactionDao().delete(transaction);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            transactions.remove(transaction);
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }
}