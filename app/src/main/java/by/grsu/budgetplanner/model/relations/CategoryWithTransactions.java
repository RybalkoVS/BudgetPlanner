package by.grsu.budgetplanner.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.entities.TransactionCategory;

public class CategoryWithTransactions {
    @Embedded
    public TransactionCategory transactionCategory;
    @Relation(
            parentColumn = "name",
            entityColumn = "categoryName"
    )
    public List<Transaction> transactions;
}

