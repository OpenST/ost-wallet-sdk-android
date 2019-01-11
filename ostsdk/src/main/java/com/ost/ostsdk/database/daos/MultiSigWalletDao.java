package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.MultiSigWallet;

@Dao
public abstract class MultiSigWalletDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((MultiSigWallet) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((MultiSigWallet[]) baseEntity);
    }

    public void delete(BaseEntity baseEntity) {
        this.delete((MultiSigWallet) baseEntity);
    }

    @Insert
    public abstract void insert(MultiSigWallet multiSigWallet);

    @Insert
    public abstract void insertAll(MultiSigWallet... multiSigWallet);

    @Query("DELETE FROM multi_sig_wallet WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM multi_sig_wallet WHERE id IN (:ids)")
    public abstract MultiSigWallet[] getByIds(String[] ids);

    @Query("SELECT * FROM multi_sig_wallet WHERE id=:id")
    public abstract MultiSigWallet getById(String id);

    @Query("DELETE FROM multi_sig_wallet")
    public abstract void deleteAll();

    @Query("SELECT * FROM multi_sig_wallet WHERE parent_id=:id")
    public abstract MultiSigWallet[] getByParentId(String id);
}