package com.ost.mobilesdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ost.mobilesdk.models.entities.OstBaseEntity;
import com.ost.mobilesdk.models.entities.OstTransaction;

@Dao
public abstract class OstTransactionDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstTransaction) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstTransaction[]) baseEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(OstTransaction executableRule);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(OstTransaction... executableRule);

    @Query("DELETE FROM `transaction` WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM `transaction` WHERE id IN (:ids)")
    public abstract OstTransaction[] getByIds(String[] ids);

    @Query("SELECT * FROM `transaction` WHERE id=:id")
    public abstract OstTransaction getById(String id);

    @Query("DELETE FROM `transaction`")
    public abstract void deleteAll();

    @Query("SELECT * FROM `transaction` WHERE parent_id=:id")
    public abstract OstTransaction[] getByParentId(String id);
}