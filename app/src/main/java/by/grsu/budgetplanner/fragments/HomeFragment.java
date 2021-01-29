package by.grsu.budgetplanner.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.activities.AddWalletActivity;
import by.grsu.budgetplanner.activities.MainActivity;
import by.grsu.budgetplanner.adapters.WalletsListAdapter;
import by.grsu.budgetplanner.model.database.App;
import by.grsu.budgetplanner.model.database.Database;
import by.grsu.budgetplanner.model.entities.Wallet;

public class HomeFragment extends Fragment implements View.OnClickListener, MainActivity.FragmentLifeCycle {

    ListView lvWalletsList;
    WalletsListAdapter adapter;
    Database db;
    List<Wallet> wallets;
    GetWalletItems getWallets;
    Wallet wallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask.execute(() -> db = App.getInstance().getDatabase());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        getWallets = new GetWalletItems();
        getWallets.execute();
        Button addWalletBtn = layout.findViewById(R.id.add_btn);
        lvWalletsList = layout.findViewById(R.id.walletsList);
        lvWalletsList.setOnItemLongClickListener((parent, view, position, id) -> {
            wallet = (Wallet)parent.getAdapter().getItem(position);
            return false;
        });
        registerForContextMenu(lvWalletsList);
        lvWalletsList.setEnabled(true);
        addWalletBtn.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.add_btn:
                Intent intent = new Intent(getActivity(), AddWalletActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.deleteMenuItem:
                DeleteWalletItem deleteWalletItem = new DeleteWalletItem();
                deleteWalletItem.execute();
                break;
            case R.id.editMenuItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Редактирование Кошелька");
                builder.setCancelable(true);
                View editWalletView= getLayoutInflater().inflate(R.layout.edit_wallet, null);
                builder.setView(editWalletView);
                EditText etEditWalletName = editWalletView.findViewById(R.id.edit_wallet_name);
                etEditWalletName.setText(wallet.getName());
                EditText etEditWalletBalance = editWalletView.findViewById(R.id.edit_wallet_balance);
                etEditWalletBalance.setText(String.valueOf(wallet.getBalance()));
                Spinner spEditCurrencyType = editWalletView.findViewById(R.id.edit_currncy_list);
                switch(wallet.getCurrency())
                {
                    case "BYN": spEditCurrencyType.setSelection(0);break;
                    case "USD": spEditCurrencyType.setSelection(1);break;
                    case "RUB": spEditCurrencyType.setSelection(3);break;
                    case "EUR": spEditCurrencyType.setSelection(2);break;

                }
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(etEditWalletBalance.getText().toString().isEmpty() || etEditWalletName.getText().toString().isEmpty())
                        {
                            Toast toast = Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            wallet.setBalance(Double.parseDouble(etEditWalletBalance.getText().toString()));
                            wallet.setName(etEditWalletName.getText().toString());
                            wallet.setCurrency(spEditCurrencyType.getSelectedItem().toString());
                            AsyncTask.execute(() -> db.walletDao().update(wallet));
                            adapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     dialog.cancel();
                    }
                });
                builder.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResumeFragment() {
    }

    public class GetWalletItems extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            wallets = new ArrayList<Wallet>();
            wallets = db.walletDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new WalletsListAdapter(getContext(), wallets);
            lvWalletsList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }
    public class DeleteWalletItem extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            App.getInstance().getDatabase().walletDao().delete(wallet);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            wallets.remove(wallet);
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }
    }
}