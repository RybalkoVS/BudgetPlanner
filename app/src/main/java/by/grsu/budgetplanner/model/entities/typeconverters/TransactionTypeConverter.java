package by.grsu.budgetplanner.model.entities.typeconverters;

import androidx.room.TypeConverter;

import by.grsu.budgetplanner.enums.TransactionType;

public class TransactionTypeConverter {

    @TypeConverter
    public String fromTransactionType(TransactionType type)
    {
        return type.toString();
    }

    @TypeConverter
    public TransactionType fromString(String type)
    {
        if(type.contains("EARN"))
        {
            return TransactionType.EARN;
        }
        return TransactionType.EXPENSE;
    }
}
