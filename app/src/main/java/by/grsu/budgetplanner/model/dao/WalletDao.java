package by.grsu.budgetplanner.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import by.grsu.budgetplanner.model.entities.Wallet;
import by.grsu.budgetplanner.model.relations.WalletWithTransactions;

@Dao
public interface WalletDao {
    @Query("SELECT * FROM wallet")
    List<Wallet> getAll();

    @Query("SELECT * FROM wallet WHERE name = :name")
    Wallet getByName(String name);

    @Query("SELECT * FROM wallet WHERE id = :id")
    Wallet getById(long id);

    @Transaction
    @Query("SELECT * FROM Wallet")
    List<WalletWithTransactions> getWalletsWithTransactions();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Wallet wallet);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Wallet wallet);

    @Delete
    void delete(Wallet wallet);
}
