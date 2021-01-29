package by.grsu.budgetplanner.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import by.grsu.budgetplanner.enums.TransactionType;
import by.grsu.budgetplanner.model.entities.Transaction;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM `transaction`")
    List<Transaction> getAll();

    @Query("SELECT * FROM `transaction` WHERE id = :id")
    Transaction getById(long id);

    @Query("SELECT * FROM `transaction` WHERE walletId = :id")
    List<Transaction> getByWalletId(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
