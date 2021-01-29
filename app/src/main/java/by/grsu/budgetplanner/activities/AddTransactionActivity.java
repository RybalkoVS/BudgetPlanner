package by.grsu.budgetplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.model.database.App;
import by.grsu.budgetplanner.model.database.Database;
import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.entities.TransactionCategory;
import by.grsu.budgetplanner.model.entities.Wallet;
import by.grsu.budgetplanner.enums.TransactionType;

public class AddTransactionActivity extends AppCompatActivity{

    public static final String EXTRA_WALLETID = "walletId";
    public static final String EXTRA_TRANSACTIONTYPE = "transactionType";

    Button applyBtn;
    EditText etTransactionSum,etTransactionCategory;
    Database db;
    AtomicReference<Wallet> wallet;
    AtomicReference<List<TransactionCategory>> transactionCategories;
    TextView tvWalletName, tvWalletBalance;
    int walletId;
    String categoryName;
    TransactionCategory transactionCategory;
    String transactionType;
    Context context;
    ArrayAdapter<TransactionCategory> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add_transaction);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        transactionType = getIntent().getExtras().getString(EXTRA_TRANSACTIONTYPE);
        db = App.getInstance().getDatabase();
        applyBtn = findViewById(R.id.applyTransactionBtn);
        etTransactionSum = findViewById(R.id.transactinSum);
        tvWalletName = findViewById(R.id.sentWalletName);
        tvWalletBalance = findViewById(R.id.sentWalletBalance);
        etTransactionCategory = findViewById(R.id.transactionCategory);
        transactionCategories = new AtomicReference<>();
        AsyncTask.execute(()-> transactionCategories.set(db.transactionCategoryDao().getAll()));
        GetWallet getWallet = new GetWallet();
        getWallet.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void onApplyTransaction(View view) {
        boolean incorrectData = false;
        if(etTransactionCategory.getText().toString().isEmpty() || etTransactionSum.getText().toString().isEmpty())
        {
            incorrectData = true;
        }
        if(incorrectData == true)
        {
            Toast toast = Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            GetTransactionCategory getCategory = new GetTransactionCategory();
            getCategory.execute();
        }
    }

    public void onDropDownListButtonClick(View view) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactionCategories.get());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.choose_category_title);
        dialogBuilder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                etTransactionCategory.setText(lw.getAdapter().getItem(which).toString());
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.setCancelable(true);
        dialogBuilder.show();
    }

    public class GetWallet extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            walletId = getIntent().getExtras().getInt(EXTRA_WALLETID);
            wallet = new AtomicReference<>();
            wallet.set(db.walletDao().getById(walletId));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            tvWalletName.setText(wallet.get().getName());
            tvWalletBalance.setText(wallet.get().getBalance() + " " + wallet.get().getCurrency());
            super.onPostExecute(result);
        }
    }
    public class GetTransactionCategory extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            TransactionCategory checkCategory = db.transactionCategoryDao().getByName(etTransactionCategory.getText().toString());
            if(checkCategory == null)
            {
                transactionCategory = new TransactionCategory(etTransactionCategory.getText().toString());
            }
            else
            {
                transactionCategory = checkCategory;
            }
            db.transactionCategoryDao().insert(transactionCategory);
            categoryName = transactionCategory.getName();
            double sum = Double.parseDouble(etTransactionSum.getText().toString());
            Double oldWalletBalance = wallet.get().getBalance();
            TransactionType type = null;
            switch (transactionType)
            {
                case "Доходы":
                    type = TransactionType.EARN;
                    wallet.get().setBalance(oldWalletBalance + sum);
                    db.walletDao().update(wallet.get());
                    break;
                case "Расходы":
                    type = TransactionType.EXPENSE;
                    wallet.get().setBalance(oldWalletBalance - sum);
                    db.walletDao().update(wallet.get());
                    break;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(System.currentTimeMillis());
            Transaction transaction = new Transaction(type, date, walletId, categoryName, sum);
            db.transactionDao().insert(transaction);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            super.onPostExecute(aVoid);
        }
    }
}