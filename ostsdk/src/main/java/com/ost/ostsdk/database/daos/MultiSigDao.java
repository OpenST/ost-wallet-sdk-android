package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.MultiSig;

@Dao
public abstract class MultiSigDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((MultiSig) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((MultiSig[]) baseEntity);
    }

    @Insert
    public abstract void insert(MultiSig multiSig);

    @Insert
    public abstract void insertAll(MultiSig... multiSig);

    @Query("DELETE FROM multi_sig WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM multi_sig WHERE id IN (:ids)")
    public abstract MultiSig[] getByIds(String[] ids);

    @Query("SELECT * FROM multi_sig WHERE id=:id")
    public abstract MultiSig getById(String id);

    @Query("DELETE FROM multi_sig")
    public abstract void deleteAll();

    @Query("SELECT * FROM multi_sig WHERE parent_id=:id")
    public abstract MultiSig[] getByParentId(String id);
}