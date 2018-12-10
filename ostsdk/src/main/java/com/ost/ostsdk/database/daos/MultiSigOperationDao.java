package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.MultiSigOperation;

@Dao
public abstract class MultiSigOperationDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((MultiSigOperation) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((MultiSigOperation[]) baseEntity);
    }

    public void delete(BaseEntity baseEntity) {
        this.delete((MultiSigOperation) baseEntity);
    }

    @Insert
    public abstract void insert(MultiSigOperation multiSigOperation);

    @Insert
    public abstract void insertAll(MultiSigOperation... multiSigOperation);

    @Delete
    public abstract void delete(MultiSigOperation multiSigOperation);

    @Query("SELECT * FROM multi_sig_operation WHERE id IN (:ids)")
    public abstract MultiSigOperation[] getByIds(String[] ids);

    @Query("SELECT * FROM multi_sig_operation WHERE id=:id")
    public abstract MultiSigOperation getById(String id);

    @Query("DELETE FROM multi_sig_operation")
    public abstract void deleteAll();
}