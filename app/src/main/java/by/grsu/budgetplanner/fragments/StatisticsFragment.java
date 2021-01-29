package by.grsu.budgetplanner.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.activities.MainActivity;
import by.grsu.budgetplanner.enums.TransactionType;
import by.grsu.budgetplanner.model.database.App;
import by.grsu.budgetplanner.model.database.Database;
import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.relations.WalletWithTransactions;

public class StatisticsFragment extends Fragment implements View.OnClickListener, MainActivity.FragmentLifeCycle {

    private static final String TAG = "MyActivity";
    Database db;
    TextView tvStartDate, tvEndDate;
    DatePickerDialog picker;
    Spinner spTransactionType, spCurrencyType;
    AnyChartView anyChartView;
    TransactionType type;
    AtomicReference<List<WalletWithTransactions>> walletWithTransactions;
    Date startDate, endDate, transactionDate;
    List<Transaction> neededTransactions;
    Pie pie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "createV");
        View layout = inflater.inflate(R.layout.fragment_statistics, container, false);
        anyChartView = layout.findViewById(R.id.any_chart_view);
        tvStartDate = layout.findViewById(R.id.start_date);
        tvEndDate = layout.findViewById(R.id.end_date);
        spCurrencyType = layout.findViewById(R.id.currency_types);
        spCurrencyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(neededTransactions != null) {
                    neededTransactions.clear();
                }
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTransactionType = layout.findViewById(R.id.transaction_types);
        spTransactionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(neededTransactions != null) {
                    neededTransactions.clear();
                }
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        if(savedInstanceState != null) {
            tvStartDate.setText(savedInstanceState.getString("StartDate"));
            tvEndDate.setText(savedInstanceState.getString("EndDate"));
            spCurrencyType.setSelection(savedInstanceState.getInt("CurrencyType"));
            spTransactionType.setSelection((savedInstanceState.getInt("TransactionsType")));
        }
        else{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(System.currentTimeMillis());
            tvStartDate.setText(date);
            tvEndDate.setText(date);
            spTransactionType.setSelection(1);
            spCurrencyType.setSelection(0);
        }
        getData();
        return layout;
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        if(v == tvStartDate)
        {
            picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+= 1;
                    tvStartDate.setText(year + "-" + month + "-" + dayOfMonth);
                    if(neededTransactions != null) {
                        neededTransactions.clear();
                    }
                    getData();
                }
            },mYear, mMonth, mDay);
            picker.show();
        }
        if(v == tvEndDate)
        {
            picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+= 1;
                    tvEndDate.setText(year + "-" + month + "-" + dayOfMonth);
                    if(neededTransactions != null) {
                        neededTransactions.clear();
                    }
                    getData();
                }
            },mYear, mMonth, mDay);
            picker.show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "create");
    }

    public void getData()
    {
        GetWalletsWithTransactions getWalletsWithTransactions = new GetWalletsWithTransactions();
        getWalletsWithTransactions.execute();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("CurrencyType",spCurrencyType.getSelectedItemPosition());
        outState.putInt("TransactionsType", spTransactionType.getSelectedItemPosition());
        outState.putString("StartDate", tvStartDate.getText().toString());
        outState.putString("EndDate", tvEndDate.getText().toString());
        Log.v(TAG, "save");
        super.onSaveInstanceState(outState);
    }

    public List<Transaction> getNeededTransactionItems()
    {
        Log.v(TAG, "setTr");
        List<Transaction> neededTransactions = new ArrayList<>();
        switch (spTransactionType.getSelectedItem().toString())
        {
            case"Расходы":
                type = TransactionType.EXPENSE;
                break;
            case"Доходы":
                type = TransactionType.EARN;
                break;
        }
        String currencyType = spCurrencyType.getSelectedItem().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate = formatter.parse(tvStartDate.getText().toString());
            endDate = formatter.parse(tvEndDate.getText().toString());
        }
        catch  (ParseException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < walletWithTransactions.get().size(); i++)
        {
            for(int j = 0; j < walletWithTransactions.get().get(i).transactions.size(); j++) {
                try {
                    transactionDate = formatter.parse(walletWithTransactions.get().get(i).transactions.get(j).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (walletWithTransactions.get().get(i).transactions.get(j).getType() == type
                        && walletWithTransactions.get().get(i).wallet.getCurrency().contains(currencyType)
                        && (transactionDate.before(endDate) || transactionDate.equals(endDate)) && (transactionDate.after(startDate) || transactionDate.equals(startDate)))
                {
                    neededTransactions.add(walletWithTransactions.get().get(i).transactions.get(j));
                }
            }
        }
        return  neededTransactions;
    }
    public void setupPieChart(List<Transaction> transactions)
    {
        List<DataEntry> dataEntries = new ArrayList<>();
        for(int i = 0; i < transactions.size(); i++)
        {
            if(dataEntries.size() > 0)
            {
                int index = 0;
                boolean flag = false;
                for(int j = 0; j < dataEntries.size(); j++)
                {
                    if(dataEntries.get(j).generateJs().contains(transactions.get(i).getCategoryName()))
                    {
                        flag = true;
                        index = j;
                    }
                }
                if(flag == true)
                {
                    double value =  Double.parseDouble(dataEntries.get(index).getValue("value").toString());
                    value+= transactions.get(i).getSum();
                    dataEntries.remove(index);
                    dataEntries.add( new ValueDataEntry(transactions.get(i).getCategoryName(), value));
                }
                else
                {
                    dataEntries.add( new ValueDataEntry(transactions.get(i).getCategoryName(), transactions.get(i).getSum()));
                }
            }
            else{
                dataEntries.add( new ValueDataEntry(transactions.get(i).getCategoryName(), transactions.get(i).getSum()));
            }
        }
        if(dataEntries.size() < 1)
        {
            pie.title("Транзакции с заданными параметрами отсутствуют");
        }else if(dataEntries.size() > 0) {
            pie.title("Диаграмма транзакций");
            pie.data(dataEntries);
        }
    }

    @Override
    public void onResumeFragment() {
        Log.v(TAG, "resume");
    }

    public class GetWalletsWithTransactions extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            db = App.getInstance().getDatabase();
            walletWithTransactions = new AtomicReference<>();
            walletWithTransactions.set(db.walletDao().getWalletsWithTransactions());
            Log.v(TAG, "back");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(pie != null) {
                View v;
                LinearLayout layout = getActivity().findViewById(R.id.any_chart_layout);
                if (layout == null) {
                    v = getLayoutInflater().inflate(R.layout.fragment_statistics, (ViewGroup) getView(), false);
                    anyChartView = v.findViewById(R.id.any_chart_view);
                } else {
                    layout.removeAllViews();
                    anyChartView = new AnyChartView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 700);
                    params.gravity = Gravity.CENTER;
                    anyChartView.setLayoutParams(params);
                    layout.addView(anyChartView);
                }
            }
            pie = AnyChart.pie();
            pie.innerRadius(50);
            pie.radius(100);
            anyChartView.clear();
            anyChartView.setChart(pie);
            neededTransactions = getNeededTransactionItems();
            anyChartView.setChart(pie);
            setupPieChart(neededTransactions);
            super.onPostExecute(result);
        }
    }
}