package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstTransaction;

public interface OstTransactionModel {

    void insertTransaction(OstTransaction executableRule);

    void insertAllTransactions(OstTransaction[] executableRule);

    void deleteTransaction(String id);

    OstTransaction[] getTransactionsByIds(String[] ids);

    OstTransaction getTransactionById(String id);

    void deleteAllTransactions();

    OstTransaction insert(OstTransaction ostTransaction);
}