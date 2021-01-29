package by.grsu.budgetplanner.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;


import by.grsu.budgetplanner.model.entities.TransactionCategory;
import by.grsu.budgetplanner.model.relations.CategoryWithTransactions;

@Dao
public interface TransactionCategoryDao {
    @Query("SELECT * FROM `TransactionCategory`")
    List<TransactionCategory> getAll();

    @Query("SELECT * FROM `TransactionCategory` WHERE name = :name")
    TransactionCategory getByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionCategory transactionCategory);

    @Transaction
    @Query("SELECT * FROM `TransactionCategory`")
    List<CategoryWithTransactions> getCategoriesWithTransactions();

    @Delete
    void delete(TransactionCategory transactionCategory);
}
