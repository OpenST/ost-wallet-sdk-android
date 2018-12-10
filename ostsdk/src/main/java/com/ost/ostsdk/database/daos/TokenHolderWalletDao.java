package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.TokenHolderWallet;

@Dao
public abstract class TokenHolderWalletDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((TokenHolderWallet) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((TokenHolderWallet[]) baseEntity);
    }

    public void delete(BaseEntity baseEntity) {
        this.delete((TokenHolderWallet) baseEntity);
    }

    @Insert
    public abstract void insert(TokenHolderWallet tokenHolderWallet);

    @Insert
    public abstract void insertAll(TokenHolderWallet... tokenHolderWallet);

    @Delete
    public abstract void delete(TokenHolderWallet tokenHolderWallet);

    @Query("SELECT * FROM token_holder_wallet WHERE id IN (:ids)")
    public abstract TokenHolderWallet[] getByIds(String[] ids);

    @Query("SELECT * FROM token_holder_wallet WHERE id=:id")
    public abstract TokenHolderWallet getById(String id);

    @Query("DELETE FROM token_holder_wallet")
    public abstract void deleteAll();
}