package by.grsu.budgetplanner.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String currency;
    private double balance;


    public Wallet(String name, String currency, double balance) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return name;
    }
}
