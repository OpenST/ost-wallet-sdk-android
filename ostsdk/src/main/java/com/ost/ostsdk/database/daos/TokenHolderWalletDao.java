package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.TokenHolderWallet;

@Dao
public interface TokenHolderWalletDao {
    @Insert
    void insert(TokenHolderWallet tokenHolderWallet);

    @Insert
    void insertAll(TokenHolderWallet... tokenHolderWallet);

    @Delete
    void delete(TokenHolderWallet tokenHolderWallet);

    @Query("SELECT * FROM token_holder_wallet WHERE id IN (:ids)")
    TokenHolderWallet getByIds(double[] ids);

    @Query("SELECT * FROM token_holder_wallet WHERE id=:id")
    TokenHolderWallet getById(double id);

    @Query("DELETE FROM token_holder_wallet")
    void deleteAll();
}
