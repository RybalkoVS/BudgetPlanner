package by.grsu.budgetplanner.model.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.concurrent.atomic.AtomicInteger;

import by.grsu.budgetplanner.R;
import by.grsu.budgetplanner.model.entities.TransactionCategory;

public class App extends Application {
    public static App instance;

    private Database database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, Database.class, "database")
                .fallbackToDestructiveMigration()
                .build();
        AsyncTask.execute(() -> database.transactionCategoryDao().insert(new TransactionCategory(getString(R.string.default_category_salary))));
        AsyncTask.execute(() -> database.transactionCategoryDao().insert(new TransactionCategory(getString(R.string.default_category_home))));
        AsyncTask.execute(() -> database.transactionCategoryDao().insert(new TransactionCategory(getString(R.string.default_category_clothes))));
        AsyncTask.execute(() -> database.transactionCategoryDao().insert(new TransactionCategory(getString(R.string.default_category_food))));
        AsyncTask.execute(() -> database.transactionCategoryDao().insert(new TransactionCategory(getString(R.string.default_category_scholarship))));

    }

    public static App getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}
