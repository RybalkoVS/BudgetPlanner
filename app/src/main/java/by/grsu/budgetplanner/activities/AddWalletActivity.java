package by.grsu.budgetplanner.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.model.database.App;
import by.grsu.budgetplanner.model.database.Database;
import by.grsu.budgetplanner.model.entities.Wallet;

public class AddWalletActivity extends AppCompatActivity {

    Button btnCreateWallet;
    EditText etWalletName, etWalletBalance;
    Spinner walletCurrency;
    Database db;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        btnCreateWallet = findViewById(R.id.createWalletButton);
        etWalletName = findViewById(R.id.walletName);
        etWalletBalance = findViewById(R.id.walletBalance);
        walletCurrency = findViewById(R.id.currncyList);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        db = App.getInstance().getDatabase();
    }

    public void onCreateButtonClick(View view) {
        boolean incorrectData = false;
        String name = "";
        double balance = 0;
        Toast toast;
        if(etWalletName.getText().toString().isEmpty() || etWalletBalance.getText().toString().isEmpty())
        {
            incorrectData = true;
        }
        else
        {
            name = etWalletName.getText().toString();
            balance = Double.parseDouble(etWalletBalance.getText().toString());
        }
        if(name == "")
        {
            incorrectData = true;
        }
        if(incorrectData == true)
        {
            toast = Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            String currency = walletCurrency.getSelectedItem().toString();
            Wallet wallet = new Wallet(name, currency, balance);
            AsyncTask.execute(() -> db.walletDao().insert(wallet));
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
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
}