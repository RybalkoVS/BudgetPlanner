package by.grsu.budgetplanner.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;


import by.grsu.budgetplanner.model.entities.typeconverters.TransactionTypeConverter;
import by.grsu.budgetplanner.enums.TransactionType;


@Entity
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @TypeConverters({TransactionTypeConverter.class})
    private TransactionType type;
    private String date;
    @ColumnInfo(index = true)
    private int walletId;
    @ColumnInfo(index = true)
    private String categoryName;
    private double sum;

    @Ignore
    public Transaction()
    {

    }
    public Transaction(TransactionType type, String date, int walletId, String categoryName, double sum) {
        this.type = type;
        this.date = date;
        this.walletId = walletId;
        this.categoryName = categoryName;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
