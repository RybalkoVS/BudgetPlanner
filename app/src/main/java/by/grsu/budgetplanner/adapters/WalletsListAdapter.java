package by.grsu.budgetplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.activities.AddTransactionActivity;
import by.grsu.budgetplanner.model.entities.Wallet;

public class WalletsListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater lInflater;
    List<Wallet> objects;
    String transactionType = "Расходы";

    public WalletsListAdapter(Context context, List<Wallet> wallets) {
        mContext = context;
        objects = wallets;
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

    public Wallet getWallet(int position) {
        return (Wallet) getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.wallets_list_adapter, parent, false);
            view.setLongClickable(true);
        }
        Wallet w = getWallet(position);
        ((TextView) view.findViewById(R.id.walletName)).setText(w.getName());
        ((TextView) view.findViewById(R.id.walletBalance)).setText(w.getBalance() + " " + w.getCurrency());
        Button addTransactionBtn = view.findViewById(R.id.addTransactionBtn);
        View.OnClickListener addBtnListener = v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle(R.string.choose_transaction_type);
            dialog.setSingleChoiceItems(R.array.transaction_types, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ListView lw = ((AlertDialog) dialog).getListView();
                    transactionType = lw.getAdapter().getItem(which).toString();
                }
            });
            dialog.setCancelable(true);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mContext, AddTransactionActivity.class);
                    intent.putExtra(AddTransactionActivity.EXTRA_WALLETID, w.getId());
                    intent.putExtra(AddTransactionActivity.EXTRA_TRANSACTIONTYPE, transactionType);
                    mContext.startActivity(intent);
                }
            });
            dialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        };
        addTransactionBtn.setOnClickListener(addBtnListener);
        return view;
    }
}
