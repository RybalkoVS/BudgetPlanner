package by.grsu.budgetplanner.model.database;

import androidx.room.RoomDatabase;

import by.grsu.budgetplanner.model.dao.TransactionCategoryDao;
import by.grsu.budgetplanner.model.dao.TransactionDao;
import by.grsu.budgetplanner.model.dao.WalletDao;
import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.entities.TransactionCategory;
import by.grsu.budgetplanner.model.entities.Wallet;

@androidx.room.Database(entities = {Wallet.class, Transaction.class, TransactionCategory.class},exportSchema = false,version = 2)
public abstract class Database extends RoomDatabase {
    public abstract WalletDao walletDao();
    public abstract TransactionDao transactionDao();
    public abstract TransactionCategoryDao transactionCategoryDao();
}
