package by.grsu.budgetplanner.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import by.grsu.budgetplanner.model.entities.Transaction;
import by.grsu.budgetplanner.model.entities.Wallet;

public class WalletWithTransactions {
    @Embedded public Wallet wallet;
    @Relation(
            parentColumn = "id",
            entityColumn = "walletId"
    )
    public List<Transaction> transactions;
}
